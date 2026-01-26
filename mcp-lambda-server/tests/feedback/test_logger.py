"""
ViolationLogger 테스트
AESA-129 Task 5.1: 비동기 위반 로거 테스트
"""

import pytest
import asyncio

from src.feedback.logger import ViolationLogger
from src.feedback.models import ViolationLog, LogContext
from src.validation.models import Violation, ViolationSeverity


class MockViolationStorage:
    """테스트용 Mock Storage"""

    def __init__(self):
        self.saved_logs: list[ViolationLog] = []
        self.violation_counts: dict[str, int] = {}
        self.save_batch_called = 0

    async def save_violation_logs_batch(self, logs: list[ViolationLog]) -> None:
        self.saved_logs.extend(logs)
        self.save_batch_called += 1

    async def increment_violation_count(self, rule_code: str) -> None:
        self.violation_counts[rule_code] = self.violation_counts.get(rule_code, 0) + 1


def create_test_violation(rule_code: str = "AGG-001") -> Violation:
    """테스트용 Violation 생성"""
    return Violation(
        rule_code=rule_code,
        rule_name="테스트 규칙",
        severity=ViolationSeverity.CRITICAL,
        message="테스트 위반 메시지",
        line_number=10,
        layer="DOMAIN",
    )


class TestViolationLogger:
    """ViolationLogger 테스트"""

    @pytest.fixture
    def mock_storage(self):
        """Mock Storage fixture"""
        return MockViolationStorage()

    @pytest.fixture
    def logger(self, mock_storage):
        """Logger fixture"""
        return ViolationLogger(
            storage=mock_storage,
            batch_size=5,
            flush_interval=1.0,
            max_buffer_size=10,
        )

    @pytest.mark.asyncio
    async def test_log_single_violation(self, logger, mock_storage):
        """단일 위반 로깅 테스트"""
        violation = create_test_violation()

        await logger.log(violation)

        assert logger.buffer_size == 1
        assert logger.total_logged == 1
        assert logger.total_flushed == 0

    @pytest.mark.asyncio
    async def test_log_batch(self, logger, mock_storage):
        """배치 로깅 테스트"""
        violations = [
            create_test_violation("AGG-001"),
            create_test_violation("AGG-002"),
            create_test_violation("AGG-003"),
        ]

        await logger.log_batch(violations)

        assert logger.buffer_size == 3
        assert logger.total_logged == 3

    @pytest.mark.asyncio
    async def test_flush_to_storage(self, logger, mock_storage):
        """플러시 테스트"""
        violations = [
            create_test_violation("AGG-001"),
            create_test_violation("AGG-002"),
        ]

        await logger.log_batch(violations)
        flushed_count = await logger.flush()

        assert flushed_count == 2
        assert logger.buffer_size == 0
        assert logger.total_flushed == 2
        assert len(mock_storage.saved_logs) == 2
        assert mock_storage.save_batch_called == 1

    @pytest.mark.asyncio
    async def test_flush_empty_buffer(self, logger, mock_storage):
        """빈 버퍼 플러시 테스트"""
        flushed_count = await logger.flush()

        assert flushed_count == 0
        assert mock_storage.save_batch_called == 0

    @pytest.mark.asyncio
    async def test_batch_size_limit(self, logger, mock_storage):
        """배치 크기 제한 테스트"""
        # batch_size=5 설정
        violations = [create_test_violation(f"AGG-00{i}") for i in range(8)]

        await logger.log_batch(violations)
        flushed_count = await logger.flush()

        # 첫 플러시에서 5개만 처리
        assert flushed_count == 5
        assert logger.buffer_size == 3
        assert logger.total_flushed == 5

        # 두 번째 플러시
        flushed_count = await logger.flush()
        assert flushed_count == 3
        assert logger.buffer_size == 0
        assert logger.total_flushed == 8

    @pytest.mark.asyncio
    async def test_auto_flush_on_max_buffer(self, logger, mock_storage):
        """최대 버퍼 크기 초과 시 자동 플러시 테스트"""
        # max_buffer_size=10 설정
        violations = [create_test_violation(f"AGG-{i:03d}") for i in range(12)]

        await logger.log_batch(violations)

        # 버퍼가 가득 차면 자동 플러시
        assert logger.total_flushed >= 5  # 최소 1번 플러시

    @pytest.mark.asyncio
    async def test_log_with_context(self, logger, mock_storage):
        """컨텍스트와 함께 로깅 테스트"""
        violation = create_test_violation()
        context = LogContext(
            project_id="proj-123",
            file_path="src/Order.java",
        )

        await logger.log(violation, context=context, class_type="AGGREGATE")
        await logger.flush()

        assert len(mock_storage.saved_logs) == 1
        saved_log = mock_storage.saved_logs[0]
        assert saved_log.context.project_id == "proj-123"
        assert saved_log.class_type == "AGGREGATE"

    @pytest.mark.asyncio
    async def test_log_auto_fix(self, logger, mock_storage):
        """자동 수정 로깅 테스트"""
        violation = create_test_violation()

        await logger.log(
            violation,
            was_auto_fixed=True,
            fix_applied="@Data 제거",
        )
        await logger.flush()

        saved_log = mock_storage.saved_logs[0]
        assert saved_log.was_auto_fixed is True
        assert saved_log.fix_applied == "@Data 제거"

    @pytest.mark.asyncio
    async def test_increment_violation_count(self, logger, mock_storage):
        """위반 횟수 증가 테스트"""
        violations = [
            create_test_violation("AGG-001"),
            create_test_violation("AGG-001"),
            create_test_violation("AGG-002"),
        ]

        await logger.log_batch(violations)
        await logger.flush()

        assert mock_storage.violation_counts["AGG-001"] == 2
        assert mock_storage.violation_counts["AGG-002"] == 1

    @pytest.mark.asyncio
    async def test_start_and_stop(self, logger, mock_storage):
        """로거 시작/종료 테스트"""
        assert logger.is_running is False

        await logger.start()
        assert logger.is_running is True

        # 로깅 후 종료
        await logger.log(create_test_violation())
        await logger.stop()

        assert logger.is_running is False
        # stop 시 남은 버퍼 플러시
        assert logger.buffer_size == 0

    @pytest.mark.asyncio
    async def test_get_stats(self, logger, mock_storage):
        """통계 조회 테스트"""
        await logger.log(create_test_violation())
        await logger.log(create_test_violation())
        await logger.flush()

        stats = logger.get_stats()

        assert stats["total_logged"] == 2
        assert stats["total_flushed"] == 2
        assert stats["pending"] == 0
        assert stats["buffer_size"] == 0
        assert stats["batch_size"] == 5
        assert stats["flush_interval"] == 1.0
        assert stats["max_buffer_size"] == 10


class TestViolationLoggerAutoFlush:
    """자동 플러시 루프 테스트"""

    @pytest.mark.asyncio
    async def test_auto_flush_loop(self):
        """자동 플러시 루프 테스트"""
        mock_storage = MockViolationStorage()
        logger = ViolationLogger(
            storage=mock_storage,
            batch_size=10,
            flush_interval=0.1,  # 100ms
        )

        await logger.start()

        # 로그 추가
        await logger.log(create_test_violation())

        # 자동 플러시 대기
        await asyncio.sleep(0.2)

        # 자동 플러시 확인
        assert logger.total_flushed >= 1

        await logger.stop()


class TestViolationLoggerErrorHandling:
    """에러 처리 테스트"""

    @pytest.mark.asyncio
    async def test_flush_failure_recovery(self):
        """플러시 실패 시 복구 테스트"""

        class FailingStorage:
            def __init__(self):
                self.fail_count = 0

            async def save_violation_logs_batch(self, logs):
                self.fail_count += 1
                if self.fail_count == 1:
                    raise Exception("DB connection failed")
                # 두 번째 시도부터 성공

            async def increment_violation_count(self, rule_code):
                pass

        failing_storage = FailingStorage()
        logger = ViolationLogger(
            storage=failing_storage,
            batch_size=10,
        )

        await logger.log(create_test_violation())

        # 첫 번째 플러시 실패
        with pytest.raises(Exception):
            await logger.flush()

        # 버퍼에 다시 추가됨
        assert logger.buffer_size == 1
        assert logger.total_flushed == 0
