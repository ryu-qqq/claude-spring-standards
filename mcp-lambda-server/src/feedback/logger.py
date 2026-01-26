"""
Feedback Loop Logger
AESA-129 Task 5.1: 비동기 위반 로거

ValidationEngine과 통합하여 위반 사항을 비동기로 로깅
"""

import asyncio
import logging
from collections import deque
from datetime import datetime
from typing import TYPE_CHECKING, Optional

from .models import LogContext, ViolationLog

if TYPE_CHECKING:
    from ..validation.models import ValidationResult, Violation

from .storage import ViolationStorage, get_violation_storage

# 로깅 설정
logger = logging.getLogger(__name__)

# Singleton instance
_logger: Optional["ViolationLogger"] = None


class ViolationLogger:
    """비동기 위반 로거

    ValidationEngine의 위반 결과를 비동기적으로 PostgreSQL에 저장합니다.
    배치 처리와 버퍼링을 통해 성능을 최적화합니다.
    """

    def __init__(
        self,
        storage: ViolationStorage,
        batch_size: int = 50,
        flush_interval: float = 5.0,
        max_buffer_size: int = 1000,
    ):
        """로거 초기화

        Args:
            storage: ViolationStorage 인스턴스
            batch_size: 배치당 처리할 로그 수
            flush_interval: 자동 플러시 간격 (초)
            max_buffer_size: 최대 버퍼 크기 (초과 시 강제 플러시)
        """
        self._storage = storage
        self._batch_size = batch_size
        self._flush_interval = flush_interval
        self._max_buffer_size = max_buffer_size

        # 로그 버퍼
        self._buffer: deque[ViolationLog] = deque(maxlen=max_buffer_size)
        self._lock = asyncio.Lock()

        # 자동 플러시 태스크
        self._flush_task: Optional[asyncio.Task] = None
        self._running = False

        # 통계
        self._total_logged = 0
        self._total_flushed = 0
        self._last_flush_time: Optional[datetime] = None

    async def start(self) -> None:
        """자동 플러시 태스크 시작"""
        if self._running:
            return

        self._running = True
        self._flush_task = asyncio.create_task(self._auto_flush_loop())
        logger.info(
            "ViolationLogger started with batch_size=%d, flush_interval=%.1fs",
            self._batch_size,
            self._flush_interval,
        )

    async def stop(self) -> None:
        """로거 종료 (버퍼 플러시 포함)"""
        self._running = False

        if self._flush_task:
            self._flush_task.cancel()
            try:
                await self._flush_task
            except asyncio.CancelledError:
                pass

        # 남은 버퍼 플러시
        await self.flush()
        logger.info(
            "ViolationLogger stopped. Total logged: %d, Total flushed: %d",
            self._total_logged,
            self._total_flushed,
        )

    async def log(
        self,
        violation: "Violation",  # validation/models.py의 Violation
        context: Optional[LogContext] = None,
        class_type: Optional[str] = None,
        was_auto_fixed: bool = False,
        fix_applied: Optional[str] = None,
    ) -> None:
        """위반 사항 로깅

        Args:
            violation: Violation 객체
            context: 추가 컨텍스트 정보
            class_type: 클래스 타입
            was_auto_fixed: 자동 수정 여부
            fix_applied: 적용된 수정 내용
        """
        log_entry = ViolationLog.from_violation(
            violation=violation,
            context=context,
            class_type=class_type,
        )
        log_entry.was_auto_fixed = was_auto_fixed
        log_entry.fix_applied = fix_applied

        async with self._lock:
            self._buffer.append(log_entry)
            self._total_logged += 1

        # 버퍼가 가득 차면 즉시 플러시
        if len(self._buffer) >= self._max_buffer_size:
            await self.flush()

    async def log_batch(
        self,
        violations: list["Violation"],
        context: Optional[LogContext] = None,
        class_type: Optional[str] = None,
    ) -> None:
        """여러 위반 사항 일괄 로깅

        Args:
            violations: Violation 객체 목록
            context: 공통 컨텍스트 정보
            class_type: 클래스 타입
        """
        log_entries = [
            ViolationLog.from_violation(v, context, class_type) for v in violations
        ]

        async with self._lock:
            self._buffer.extend(log_entries)
            self._total_logged += len(log_entries)

        # 버퍼가 가득 차면 즉시 플러시
        if len(self._buffer) >= self._max_buffer_size:
            await self.flush()

    async def log_from_validation_result(
        self,
        result: "ValidationResult",  # validation/models.py의 ValidationResult
        context: Optional[LogContext] = None,
    ) -> None:
        """ValidationResult에서 모든 위반 사항 로깅

        Args:
            result: ValidationResult 객체
            context: 추가 컨텍스트 정보
        """
        if not result.violations:
            return

        await self.log_batch(
            violations=result.violations,
            context=context,
            class_type=result.class_type,
        )

    async def flush(self) -> int:
        """버퍼의 로그를 데이터베이스에 저장

        Returns:
            저장된 로그 수
        """
        async with self._lock:
            if not self._buffer:
                return 0

            # 배치 크기만큼 추출
            batch: list[ViolationLog] = []
            while self._buffer and len(batch) < self._batch_size:
                batch.append(self._buffer.popleft())

        if not batch:
            return 0

        try:
            await self._storage.save_violation_logs_batch(batch)
            self._total_flushed += len(batch)
            self._last_flush_time = datetime.utcnow()

            # 규칙별 위반 횟수 증가
            for log in batch:
                await self._storage.increment_violation_count(log.rule_code)

            logger.debug("Flushed %d violation logs", len(batch))
            return len(batch)

        except Exception as e:
            # 실패 시 버퍼에 다시 추가 (맨 앞에)
            async with self._lock:
                for log in reversed(batch):
                    self._buffer.appendleft(log)

            logger.error("Failed to flush violation logs: %s", e)
            raise

    async def _auto_flush_loop(self) -> None:
        """자동 플러시 루프"""
        while self._running:
            try:
                await asyncio.sleep(self._flush_interval)

                if self._buffer:
                    flushed = await self.flush()
                    if flushed > 0:
                        logger.debug("Auto-flushed %d violation logs", flushed)

            except asyncio.CancelledError:
                break
            except Exception as e:
                logger.error("Error in auto-flush loop: %s", e)

    # ==================== 조회 메서드 ====================

    async def get_recent_violations(
        self,
        rule_code: Optional[str] = None,
        severity: Optional[str] = None,
        layer: Optional[str] = None,
        project_id: Optional[str] = None,
        limit: int = 100,
    ) -> list[dict]:
        """최근 위반 로그 조회"""
        return await self._storage.get_violation_logs(
            rule_code=rule_code,
            severity=severity,
            layer=layer,
            project_id=project_id,
            limit=limit,
        )

    async def get_violation_stats_by_rule(
        self,
        days: int = 7,
        project_id: Optional[str] = None,
    ) -> dict[str, int]:
        """규칙별 위반 통계 조회"""
        from datetime import timedelta

        since = datetime.utcnow() - timedelta(days=days)
        return await self._storage.get_violation_count_by_rule(
            since=since,
            project_id=project_id,
        )

    async def get_violation_stats_by_layer(
        self,
        days: int = 7,
        project_id: Optional[str] = None,
    ) -> dict[str, int]:
        """레이어별 위반 통계 조회"""
        from datetime import timedelta

        since = datetime.utcnow() - timedelta(days=days)
        return await self._storage.get_violation_count_by_layer(
            since=since,
            project_id=project_id,
        )

    # ==================== 상태 조회 ====================

    @property
    def buffer_size(self) -> int:
        """현재 버퍼 크기"""
        return len(self._buffer)

    @property
    def total_logged(self) -> int:
        """총 로깅된 수"""
        return self._total_logged

    @property
    def total_flushed(self) -> int:
        """총 플러시된 수"""
        return self._total_flushed

    @property
    def is_running(self) -> bool:
        """로거 실행 여부"""
        return self._running

    def get_stats(self) -> dict:
        """로거 통계 반환"""
        return {
            "is_running": self._running,
            "buffer_size": len(self._buffer),
            "total_logged": self._total_logged,
            "total_flushed": self._total_flushed,
            "pending": self._total_logged - self._total_flushed,
            "last_flush_time": self._last_flush_time.isoformat()
            if self._last_flush_time
            else None,
            "batch_size": self._batch_size,
            "flush_interval": self._flush_interval,
            "max_buffer_size": self._max_buffer_size,
        }


async def get_violation_logger(
    dsn: Optional[str] = None,
    batch_size: int = 50,
    flush_interval: float = 5.0,
) -> ViolationLogger:
    """싱글톤 ViolationLogger 인스턴스 반환

    Args:
        dsn: PostgreSQL 연결 문자열 (최초 호출 시 필수)
        batch_size: 배치당 처리할 로그 수
        flush_interval: 자동 플러시 간격 (초)
    """
    global _logger

    if _logger is None:
        storage = await get_violation_storage(dsn)
        _logger = ViolationLogger(
            storage=storage,
            batch_size=batch_size,
            flush_interval=flush_interval,
        )
        await _logger.start()

    return _logger
