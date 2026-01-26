"""
Feedback Loop Integration
AESA-129 Task 5.1: ValidationEngine과 ViolationLogger 통합

ValidationEngine의 검증 결과를 자동으로 로깅하는 통합 레이어
"""

import asyncio
import threading
from typing import Optional
import logging

from ..validation.models import ValidationResult, ValidationContext, Violation
from ..validation.engine import ValidationEngine, get_validation_engine
from .models import LogContext
from .logger import ViolationLogger, get_violation_logger

logger = logging.getLogger(__name__)


def _run_async_in_background(coro) -> None:
    """비동기 코루틴을 백그라운드에서 실행 (동기 컨텍스트용)

    동기 메서드에서 비동기 로깅을 fire-and-forget 방식으로 실행합니다.
    running event loop가 있으면 create_task, 없으면 새 스레드에서 실행합니다.
    """
    try:
        asyncio.get_running_loop()
        # running event loop가 있으면 task 생성
        asyncio.create_task(coro)
    except RuntimeError:
        # running event loop가 없으면 새 스레드에서 실행
        def run_in_thread():
            asyncio.run(coro)

        thread = threading.Thread(target=run_in_thread, daemon=True)
        thread.start()


class LoggingValidationEngine:
    """로깅 기능이 통합된 ValidationEngine 래퍼

    ValidationEngine의 모든 검증 결과를 자동으로 ViolationLogger에 전달합니다.
    """

    def __init__(
        self,
        engine: Optional[ValidationEngine] = None,
        violation_logger: Optional[ViolationLogger] = None,
        enable_logging: bool = True,
    ):
        """통합 엔진 초기화

        Args:
            engine: ValidationEngine 인스턴스 (없으면 싱글톤 사용)
            violation_logger: ViolationLogger 인스턴스 (없으면 싱글톤 사용)
            enable_logging: 로깅 활성화 여부
        """
        self._engine = engine or get_validation_engine()
        self._violation_logger = violation_logger
        self._enable_logging = enable_logging
        self._initialized = False

    async def initialize(self, dsn: str) -> None:
        """비동기 초기화

        Args:
            dsn: PostgreSQL 연결 문자열
        """
        if self._initialized:
            return

        if self._enable_logging and self._violation_logger is None:
            self._violation_logger = await get_violation_logger(dsn)

        self._initialized = True
        logger.info(
            "LoggingValidationEngine initialized with logging=%s", self._enable_logging
        )

    async def shutdown(self) -> None:
        """리소스 정리"""
        if self._violation_logger:
            await self._violation_logger.stop()

    def validate(
        self,
        context: ValidationContext,
        log_context: Optional[LogContext] = None,
    ) -> ValidationResult:
        """코드 검증 수행 (동기)

        검증 수행 후 비동기로 로깅을 스케줄링합니다.

        Args:
            context: 검증 컨텍스트
            log_context: 로깅 컨텍스트 (선택)

        Returns:
            ValidationResult: 검증 결과
        """
        result = self._engine.validate(context)

        # 비동기 로깅 스케줄링 (fire-and-forget)
        if self._enable_logging and result.violations:
            _run_async_in_background(self._log_result(result, log_context))

        return result

    async def validate_async(
        self,
        context: ValidationContext,
        log_context: Optional[LogContext] = None,
    ) -> ValidationResult:
        """코드 검증 수행 (비동기)

        Args:
            context: 검증 컨텍스트
            log_context: 로깅 컨텍스트 (선택)

        Returns:
            ValidationResult: 검증 결과
        """
        result = self._engine.validate(context)

        # 로깅
        if self._enable_logging and result.violations:
            await self._log_result(result, log_context)

        return result

    def validate_zero_tolerance(
        self,
        code: str,
        layer: str,
        log_context: Optional[LogContext] = None,
    ) -> ValidationResult:
        """Zero-Tolerance 규칙만 검증 (동기)

        Args:
            code: 검증할 Java 코드
            layer: 대상 레이어
            log_context: 로깅 컨텍스트 (선택)

        Returns:
            ValidationResult: Zero-Tolerance 검증 결과
        """
        result = self._engine.validate_zero_tolerance(code, layer)

        # 비동기 로깅 스케줄링 (fire-and-forget)
        if self._enable_logging and result.violations:
            _run_async_in_background(self._log_result(result, log_context))

        return result

    async def validate_zero_tolerance_async(
        self,
        code: str,
        layer: str,
        log_context: Optional[LogContext] = None,
    ) -> ValidationResult:
        """Zero-Tolerance 규칙만 검증 (비동기)

        Args:
            code: 검증할 Java 코드
            layer: 대상 레이어
            log_context: 로깅 컨텍스트 (선택)

        Returns:
            ValidationResult: Zero-Tolerance 검증 결과
        """
        result = self._engine.validate_zero_tolerance(code, layer)

        # 로깅
        if self._enable_logging and result.violations:
            await self._log_result(result, log_context)

        return result

    async def validate_and_regenerate(
        self,
        code: str,
        layer: str,
        log_context: Optional[LogContext] = None,
        regenerate_callback: Optional[callable] = None,
        max_attempts: int = 3,
    ) -> tuple[ValidationResult, str, int]:
        """검증 후 재생성 (모든 시도 로깅)

        Args:
            code: 초기 코드
            layer: 대상 레이어
            log_context: 로깅 컨텍스트 (선택)
            regenerate_callback: 재생성 콜백
            max_attempts: 최대 재시도 횟수

        Returns:
            tuple[ValidationResult, str, int]: (최종 결과, 최종 코드, 시도 횟수)
        """
        # 각 시도마다 로깅
        current_code = code
        attempt = 0

        while attempt < max_attempts:
            attempt += 1

            result = self._engine.validate_zero_tolerance(current_code, layer)

            # 로깅 (시도 정보 포함)
            if self._enable_logging and result.violations:
                enriched_context = log_context or LogContext()
                enriched_context.metadata["attempt"] = attempt
                enriched_context.metadata["max_attempts"] = max_attempts
                await self._log_result(result, enriched_context)

            # 검증 통과
            if result.is_valid:
                return result, current_code, attempt

            # 재생성 콜백
            if regenerate_callback is None:
                fixed_code = self._engine._auto_fix(current_code, result.suggestions)
                if fixed_code == current_code:
                    break
                current_code = fixed_code

                # 자동 수정 로깅
                if self._enable_logging:
                    for suggestion in result.suggestions:
                        if suggestion.auto_fixable:
                            await self._log_auto_fix(
                                suggestion.violation,
                                log_context,
                                suggestion.after_code,
                            )
            else:
                new_code = regenerate_callback(result.suggestions)
                if new_code is None or new_code == current_code:
                    break
                current_code = new_code

        # 최종 검증
        final_result = self._engine.validate_zero_tolerance(current_code, layer)
        return final_result, current_code, attempt

    async def _log_result(
        self,
        result: ValidationResult,
        context: Optional[LogContext] = None,
    ) -> None:
        """검증 결과 로깅"""
        if not self._violation_logger or not result.violations:
            return

        try:
            await self._violation_logger.log_from_validation_result(result, context)
        except Exception as e:
            logger.error("Failed to log validation result: %s", e)

    async def _log_auto_fix(
        self,
        violation: Violation,
        context: Optional[LogContext] = None,
        fix_applied: Optional[str] = None,
    ) -> None:
        """자동 수정 로깅"""
        if not self._violation_logger:
            return

        try:
            await self._violation_logger.log(
                violation=violation,
                context=context,
                was_auto_fixed=True,
                fix_applied=fix_applied,
            )
        except Exception as e:
            logger.error("Failed to log auto-fix: %s", e)

    # ==================== 통계 조회 ====================

    async def get_violation_stats(
        self,
        days: int = 7,
        project_id: Optional[str] = None,
    ) -> dict:
        """위반 통계 조회"""
        if not self._violation_logger:
            return {"error": "Logging not enabled"}

        return {
            "by_rule": await self._violation_logger.get_violation_stats_by_rule(
                days, project_id
            ),
            "by_layer": await self._violation_logger.get_violation_stats_by_layer(
                days, project_id
            ),
            "logger_stats": self._violation_logger.get_stats(),
        }

    # ==================== 프로퍼티 ====================

    @property
    def engine(self) -> ValidationEngine:
        """내부 ValidationEngine 반환"""
        return self._engine

    @property
    def logging_enabled(self) -> bool:
        """로깅 활성화 여부"""
        return self._enable_logging

    def enable_logging(self) -> None:
        """로깅 활성화"""
        self._enable_logging = True

    def disable_logging(self) -> None:
        """로깅 비활성화"""
        self._enable_logging = False


# ==================== 편의 함수 ====================

_logging_engine: Optional[LoggingValidationEngine] = None


async def get_logging_validation_engine(
    dsn: Optional[str] = None,
    enable_logging: bool = True,
) -> LoggingValidationEngine:
    """싱글톤 LoggingValidationEngine 반환

    Args:
        dsn: PostgreSQL 연결 문자열 (최초 호출 시 필수)
        enable_logging: 로깅 활성화 여부

    Returns:
        LoggingValidationEngine 인스턴스
    """
    global _logging_engine

    if _logging_engine is None:
        _logging_engine = LoggingValidationEngine(enable_logging=enable_logging)
        if dsn and enable_logging:
            await _logging_engine.initialize(dsn)

    return _logging_engine
