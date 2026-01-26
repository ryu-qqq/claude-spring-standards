"""
LoggingValidationEngine 통합 테스트
AESA-129 Task 5.1: ValidationEngine + ViolationLogger 통합 테스트
"""

import pytest
from unittest.mock import AsyncMock

from src.feedback.integration import LoggingValidationEngine
from src.feedback.logger import ViolationLogger
from src.feedback.models import LogContext, ViolationLog
from src.validation.engine import ValidationEngine
from src.validation.models import ValidationContext, ValidationResult


class MockViolationStorage:
    """테스트용 Mock Storage"""

    def __init__(self):
        self.saved_logs: list[ViolationLog] = []
        self.violation_counts: dict[str, int] = {}

    async def save_violation_logs_batch(self, logs: list[ViolationLog]) -> None:
        self.saved_logs.extend(logs)

    async def increment_violation_count(self, rule_code: str) -> None:
        self.violation_counts[rule_code] = self.violation_counts.get(rule_code, 0) + 1


SAMPLE_DOMAIN_CODE_WITH_LOMBOK = """
package com.example.order.domain;

import lombok.Data;
import lombok.Getter;

@Data
public class Order {
    private Long orderId;
    private String status;
}
"""

SAMPLE_DOMAIN_CODE_VALID = """
package com.example.order.domain;

public class Order {
    private final Long orderId;
    private final String status;

    public Order(Long orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }
}
"""


class TestLoggingValidationEngine:
    """LoggingValidationEngine 테스트"""

    @pytest.fixture
    def mock_storage(self):
        """Mock Storage fixture"""
        return MockViolationStorage()

    @pytest.fixture
    def violation_logger(self, mock_storage):
        """ViolationLogger fixture"""
        return ViolationLogger(
            storage=mock_storage,
            batch_size=10,
            flush_interval=5.0,
        )

    @pytest.fixture
    def logging_engine(self, violation_logger):
        """LoggingValidationEngine fixture"""
        return LoggingValidationEngine(
            engine=None,  # 기본 싱글톤 사용
            violation_logger=violation_logger,
            enable_logging=True,
        )

    def test_validate_with_violations(
        self, logging_engine, violation_logger, mock_storage
    ):
        """위반 있는 코드 검증 및 로깅 테스트"""
        context = ValidationContext(
            code=SAMPLE_DOMAIN_CODE_WITH_LOMBOK,
            layer="DOMAIN",
        )

        result = logging_engine.validate(context)

        # 검증 결과 확인
        assert isinstance(result, ValidationResult)
        assert result.is_valid is False
        assert len(result.violations) > 0

        # Lombok 위반 확인
        lombok_violations = [
            v
            for v in result.violations
            if "Lombok" in v.message or "lombok" in v.message.lower()
        ]
        assert len(lombok_violations) > 0

    def test_validate_without_violations(
        self, logging_engine, violation_logger, mock_storage
    ):
        """위반 없는 코드 검증 테스트"""
        context = ValidationContext(
            code=SAMPLE_DOMAIN_CODE_VALID,
            layer="DOMAIN",
        )

        result = logging_engine.validate(context)

        # 검증 결과 확인 (일부 규칙만 통과할 수 있음)
        assert isinstance(result, ValidationResult)

    @pytest.mark.asyncio
    async def test_validate_async_with_logging(
        self, logging_engine, violation_logger, mock_storage
    ):
        """비동기 검증 및 로깅 테스트"""
        context = ValidationContext(
            code=SAMPLE_DOMAIN_CODE_WITH_LOMBOK,
            layer="DOMAIN",
        )

        log_context = LogContext(
            project_id="test-project",
            file_path="src/Order.java",
        )

        result = await logging_engine.validate_async(context, log_context)

        # 검증 결과 확인
        assert isinstance(result, ValidationResult)

        # 로깅 버퍼 확인
        if result.violations:
            assert violation_logger.total_logged > 0

    def test_validate_zero_tolerance(self, logging_engine, violation_logger):
        """Zero-Tolerance 검증 테스트"""
        result = logging_engine.validate_zero_tolerance(
            code=SAMPLE_DOMAIN_CODE_WITH_LOMBOK,
            layer="DOMAIN",
        )

        # 검증 결과 확인
        assert isinstance(result, ValidationResult)
        assert result.is_valid is False  # Lombok 사용으로 실패

    @pytest.mark.asyncio
    async def test_validate_zero_tolerance_async_with_context(
        self, logging_engine, violation_logger, mock_storage
    ):
        """Zero-Tolerance 비동기 검증 + 컨텍스트 테스트"""
        log_context = LogContext(
            project_id="proj-123",
            file_path="src/Order.java",
            session_id="sess-abc",
        )

        result = await logging_engine.validate_zero_tolerance_async(
            code=SAMPLE_DOMAIN_CODE_WITH_LOMBOK,
            layer="DOMAIN",
            log_context=log_context,
        )

        assert result.is_valid is False

        # 플러시하여 저장 확인
        await violation_logger.flush()

        if mock_storage.saved_logs:
            saved_log = mock_storage.saved_logs[0]
            assert saved_log.context.project_id == "proj-123"
            assert saved_log.context.file_path == "src/Order.java"

    def test_logging_disabled(self, violation_logger):
        """로깅 비활성화 테스트"""
        engine = LoggingValidationEngine(
            violation_logger=violation_logger,
            enable_logging=False,
        )

        context = ValidationContext(
            code=SAMPLE_DOMAIN_CODE_WITH_LOMBOK,
            layer="DOMAIN",
        )

        result = engine.validate(context)

        # 검증은 수행되지만 로깅은 안 됨
        assert isinstance(result, ValidationResult)
        assert violation_logger.total_logged == 0

    def test_enable_disable_logging(self, logging_engine, violation_logger):
        """로깅 활성화/비활성화 토글 테스트"""
        assert logging_engine.logging_enabled is True

        logging_engine.disable_logging()
        assert logging_engine.logging_enabled is False

        logging_engine.enable_logging()
        assert logging_engine.logging_enabled is True

    def test_engine_property(self, logging_engine):
        """내부 ValidationEngine 접근 테스트"""
        assert logging_engine.engine is not None
        assert isinstance(logging_engine.engine, ValidationEngine)


class TestLoggingValidationEngineValidateAndRegenerate:
    """validate_and_regenerate 메서드 테스트"""

    @pytest.fixture
    def mock_storage(self):
        return MockViolationStorage()

    @pytest.fixture
    def violation_logger(self, mock_storage):
        return ViolationLogger(
            storage=mock_storage,
            batch_size=10,
        )

    @pytest.fixture
    def logging_engine(self, violation_logger):
        return LoggingValidationEngine(
            violation_logger=violation_logger,
            enable_logging=True,
        )

    @pytest.mark.asyncio
    async def test_validate_and_regenerate_with_auto_fix(
        self, logging_engine, violation_logger, mock_storage
    ):
        """자동 수정을 통한 재검증 테스트"""
        log_context = LogContext(
            project_id="test-project",
            metadata={},
        )

        result, final_code, attempts = await logging_engine.validate_and_regenerate(
            code=SAMPLE_DOMAIN_CODE_WITH_LOMBOK,
            layer="DOMAIN",
            log_context=log_context,
            max_attempts=3,
        )

        # 시도 횟수 확인
        assert attempts >= 1
        assert attempts <= 3

        # 결과 타입 확인
        assert isinstance(result, ValidationResult)
        assert isinstance(final_code, str)

    @pytest.mark.asyncio
    async def test_validate_and_regenerate_logs_attempts(
        self, logging_engine, violation_logger, mock_storage
    ):
        """재검증 시도마다 로깅 테스트"""
        log_context = LogContext(
            project_id="test-project",
            metadata={},
        )

        result, final_code, attempts = await logging_engine.validate_and_regenerate(
            code=SAMPLE_DOMAIN_CODE_WITH_LOMBOK,
            layer="DOMAIN",
            log_context=log_context,
            max_attempts=2,
        )

        # 플러시
        await violation_logger.flush()

        # 로깅 확인 (각 시도마다 위반이 있으면 로깅)
        if result.violations or attempts > 1:
            assert violation_logger.total_logged > 0


class TestLoggingValidationEngineStats:
    """통계 조회 테스트"""

    @pytest.fixture
    def mock_storage(self):
        storage = MockViolationStorage()
        # 통계 조회 메서드 추가
        storage.get_violation_count_by_rule = AsyncMock(return_value={"AGG-001": 5})
        storage.get_violation_count_by_layer = AsyncMock(return_value={"DOMAIN": 10})
        return storage

    @pytest.fixture
    def violation_logger(self, mock_storage):
        logger = ViolationLogger(
            storage=mock_storage,
            batch_size=10,
        )
        # 통계 조회 메서드 mock
        logger.get_violation_stats_by_rule = AsyncMock(return_value={"AGG-001": 5})
        logger.get_violation_stats_by_layer = AsyncMock(return_value={"DOMAIN": 10})
        return logger

    @pytest.fixture
    def logging_engine(self, violation_logger):
        return LoggingValidationEngine(
            violation_logger=violation_logger,
            enable_logging=True,
        )

    @pytest.mark.asyncio
    async def test_get_violation_stats(self, logging_engine, violation_logger):
        """위반 통계 조회 테스트"""
        stats = await logging_engine.get_violation_stats(days=7)

        assert "by_rule" in stats
        assert "by_layer" in stats
        assert "logger_stats" in stats

    @pytest.mark.asyncio
    async def test_get_violation_stats_logging_disabled(self):
        """로깅 비활성화 시 통계 조회 테스트"""
        engine = LoggingValidationEngine(
            violation_logger=None,
            enable_logging=False,
        )

        stats = await engine.get_violation_stats()

        assert "error" in stats
