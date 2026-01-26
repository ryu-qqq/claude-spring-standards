"""
ViolationStorage 테스트
AESA-129 Task 5.1: PostgreSQL 비동기 저장소 테스트

Mock 기반 테스트로 실제 DB 연결 없이 스토리지 로직 검증
"""

import pytest
from unittest.mock import AsyncMock, MagicMock, patch
from uuid import uuid4

from src.feedback.storage import (
    ViolationStorage,
    get_violation_storage,
    VIOLATION_LOG_DDL,
    VIOLATION_PATTERN_DDL,
    RULE_WEIGHT_DDL,
)
from src.feedback.models import (
    ViolationLog,
    ViolationPattern,
    RuleWeight,
    LogContext,
    PatternType,
)


# ==================== Helper Classes ====================


class AsyncContextManagerMock:
    """async context manager 프로토콜을 지원하는 Mock 클래스"""

    def __init__(self, return_value):
        self._return_value = return_value

    async def __aenter__(self):
        return self._return_value

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        return None


# ==================== Fixtures ====================


@pytest.fixture
def mock_asyncpg():
    """asyncpg 모듈 모킹 - async context manager 지원"""
    with patch("src.feedback.storage.asyncpg") as mock:
        mock_pool = AsyncMock()
        mock_conn = AsyncMock()

        # pool.acquire()는 await 없이 async context manager를 직접 반환
        # AsyncMock의 메서드는 호출 시 코루틴을 반환하므로, 일반 MagicMock으로 설정
        mock_pool.acquire = MagicMock(return_value=AsyncContextManagerMock(mock_conn))

        mock.create_pool = AsyncMock(return_value=mock_pool)
        yield mock, mock_pool, mock_conn


@pytest.fixture
def sample_violation_log():
    """샘플 ViolationLog"""
    return ViolationLog(
        rule_code="AGG-001",
        rule_name="Lombok 금지",
        severity="CRITICAL",
        message="@Data 사용 금지",
        line_number=10,
        column=5,
        code_snippet="@Data public class Order {}",
        layer="DOMAIN",
        class_type="AGGREGATE",
        context=LogContext(
            project_id="proj-123",
            project_name="Test Project",
            file_path="Order.java",
            class_name="Order",
            session_id="session-abc",
        ),
    )


@pytest.fixture
def sample_violation_pattern():
    """샘플 ViolationPattern"""
    return ViolationPattern(
        pattern_type=PatternType.RECURRING,
        rule_codes=["AGG-001", "AGG-002"],
        occurrence_count=15,
        confidence=0.85,
        description="Lombok 어노테이션 반복 사용",
        project_id="proj-123",
    )


@pytest.fixture
def sample_rule_weight():
    """샘플 RuleWeight"""
    return RuleWeight(
        rule_code="AGG-001",
        base_weight=1.0,
        adjusted_weight=1.5,
        adjustment_reason="빈도 기반 조정",
        violation_count=25,
        auto_fix_rate=0.3,
    )


# ==================== ViolationStorage 초기화 테스트 ====================


class TestViolationStorageInitialization:
    """ViolationStorage 초기화 테스트"""

    def test_init_without_asyncpg_raises_error(self):
        """asyncpg 없이 초기화 시 ImportError"""
        with patch("src.feedback.storage.asyncpg", None):
            with pytest.raises(ImportError, match="asyncpg is required"):
                ViolationStorage("postgresql://localhost/test")

    @pytest.mark.asyncio
    async def test_initialize_creates_pool_and_tables(self, mock_asyncpg):
        """initialize()가 연결 풀 생성 및 테이블 생성"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        # Pool 생성 확인
        mock_module.create_pool.assert_called_once()

        # DDL 실행 확인 (3개 테이블)
        assert mock_conn.execute.call_count == 3

    @pytest.mark.asyncio
    async def test_close_releases_pool(self, mock_asyncpg):
        """close()가 연결 풀 해제"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()
        await storage.close()

        mock_pool.close.assert_called_once()


# ==================== ViolationLog 저장/조회 테스트 ====================


class TestViolationLogOperations:
    """ViolationLog CRUD 테스트"""

    @pytest.mark.asyncio
    async def test_save_violation_log_without_init_raises(self):
        """초기화 없이 저장 시 RuntimeError"""
        with patch("src.feedback.storage.asyncpg", MagicMock()):
            storage = ViolationStorage("postgresql://localhost/test")
            log = ViolationLog(
                rule_code="TEST",
                rule_name="Test Rule",
                severity="WARNING",
                message="Test",
            )

            with pytest.raises(RuntimeError, match="not initialized"):
                await storage.save_violation_log(log)

    @pytest.mark.asyncio
    async def test_save_violation_log_success(self, mock_asyncpg, sample_violation_log):
        """위반 로그 저장 성공"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()
        await storage.save_violation_log(sample_violation_log)

        # INSERT 쿼리 실행 확인
        mock_conn.execute.assert_called()
        call_args = mock_conn.execute.call_args_list[-1]
        assert "INSERT INTO violation_logs" in call_args[0][0]

    @pytest.mark.asyncio
    async def test_save_violation_logs_batch_empty_list(self, mock_asyncpg):
        """빈 목록 배치 저장 - 아무것도 하지 않음"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        initial_call_count = mock_conn.executemany.call_count
        await storage.save_violation_logs_batch([])

        # executemany 호출 안 함
        assert mock_conn.executemany.call_count == initial_call_count

    @pytest.mark.asyncio
    async def test_save_violation_logs_batch_success(
        self, mock_asyncpg, sample_violation_log
    ):
        """배치 저장 성공"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        logs = [sample_violation_log, sample_violation_log]
        await storage.save_violation_logs_batch(logs)

        mock_conn.executemany.assert_called_once()

    @pytest.mark.asyncio
    async def test_get_violation_logs_with_filters(self, mock_asyncpg):
        """필터 조건으로 위반 로그 조회"""
        mock_module, mock_pool, mock_conn = mock_asyncpg
        mock_conn.fetch.return_value = [
            {
                "id": uuid4(),
                "rule_code": "AGG-001",
                "severity": "CRITICAL",
                "layer": "DOMAIN",
            }
        ]

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        results = await storage.get_violation_logs(
            rule_code="AGG-001",
            severity="CRITICAL",
            layer="DOMAIN",
            project_id="proj-123",
            limit=50,
        )

        assert len(results) == 1
        mock_conn.fetch.assert_called_once()

        # 쿼리에 모든 필터 조건 포함 확인
        call_args = mock_conn.fetch.call_args
        query = call_args[0][0]
        assert "rule_code" in query
        assert "severity" in query
        assert "layer" in query
        assert "project_id" in query

    @pytest.mark.asyncio
    async def test_get_violation_count_by_rule(self, mock_asyncpg):
        """규칙별 위반 횟수 집계"""
        mock_module, mock_pool, mock_conn = mock_asyncpg
        mock_conn.fetch.return_value = [
            {"rule_code": "AGG-001", "count": 15},
            {"rule_code": "AGG-002", "count": 10},
        ]

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        result = await storage.get_violation_count_by_rule()

        assert result == {"AGG-001": 15, "AGG-002": 10}

    @pytest.mark.asyncio
    async def test_get_violation_count_by_layer(self, mock_asyncpg):
        """레이어별 위반 횟수 집계"""
        mock_module, mock_pool, mock_conn = mock_asyncpg
        mock_conn.fetch.return_value = [
            {"layer": "DOMAIN", "count": 20},
            {"layer": "APPLICATION", "count": 8},
        ]

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        result = await storage.get_violation_count_by_layer()

        assert result == {"DOMAIN": 20, "APPLICATION": 8}


# ==================== ViolationPattern 저장/조회 테스트 ====================


class TestViolationPatternOperations:
    """ViolationPattern CRUD 테스트"""

    @pytest.mark.asyncio
    async def test_save_pattern_success(self, mock_asyncpg, sample_violation_pattern):
        """패턴 저장 성공"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()
        await storage.save_pattern(sample_violation_pattern)

        mock_conn.execute.assert_called()
        call_args = mock_conn.execute.call_args_list[-1]
        assert "INSERT INTO violation_patterns" in call_args[0][0]

    @pytest.mark.asyncio
    async def test_get_patterns_with_filters(self, mock_asyncpg):
        """필터 조건으로 패턴 조회"""
        mock_module, mock_pool, mock_conn = mock_asyncpg
        mock_conn.fetch.return_value = [
            {
                "id": uuid4(),
                "pattern_type": "RECURRING",
                "confidence": 0.85,
                "rule_codes": '["AGG-001"]',
            }
        ]

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        results = await storage.get_patterns(
            pattern_type="RECURRING",
            project_id="proj-123",
            min_confidence=0.7,
        )

        assert len(results) == 1
        mock_conn.fetch.assert_called_once()


# ==================== RuleWeight 저장/조회 테스트 ====================


class TestRuleWeightOperations:
    """RuleWeight CRUD 테스트"""

    @pytest.mark.asyncio
    async def test_save_rule_weight_upsert(self, mock_asyncpg, sample_rule_weight):
        """가중치 저장 (upsert)"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()
        await storage.save_rule_weight(sample_rule_weight)

        mock_conn.execute.assert_called()
        call_args = mock_conn.execute.call_args_list[-1]
        query = call_args[0][0]
        assert "INSERT INTO rule_weights" in query
        assert "ON CONFLICT" in query

    @pytest.mark.asyncio
    async def test_get_rule_weight_found(self, mock_asyncpg):
        """가중치 조회 - 존재"""
        mock_module, mock_pool, mock_conn = mock_asyncpg
        mock_conn.fetchrow.return_value = {
            "rule_code": "AGG-001",
            "adjusted_weight": 1.5,
            "violation_count": 25,
        }

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        result = await storage.get_rule_weight("AGG-001")

        assert result is not None
        assert result["rule_code"] == "AGG-001"

    @pytest.mark.asyncio
    async def test_get_rule_weight_not_found(self, mock_asyncpg):
        """가중치 조회 - 미존재"""
        mock_module, mock_pool, mock_conn = mock_asyncpg
        mock_conn.fetchrow.return_value = None

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        result = await storage.get_rule_weight("NONEXISTENT")

        assert result is None

    @pytest.mark.asyncio
    async def test_get_all_rule_weights(self, mock_asyncpg):
        """모든 가중치 조회"""
        mock_module, mock_pool, mock_conn = mock_asyncpg
        mock_conn.fetch.return_value = [
            {"rule_code": "AGG-001", "violation_count": 25},
            {"rule_code": "AGG-002", "violation_count": 10},
        ]

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()

        results = await storage.get_all_rule_weights()

        assert len(results) == 2

    @pytest.mark.asyncio
    async def test_increment_violation_count(self, mock_asyncpg):
        """위반 횟수 증가"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        storage = ViolationStorage("postgresql://localhost/test")
        await storage.initialize()
        await storage.increment_violation_count("AGG-001")

        mock_conn.execute.assert_called()
        call_args = mock_conn.execute.call_args_list[-1]
        query = call_args[0][0]
        assert "violation_count" in query
        assert "ON CONFLICT" in query


# ==================== DDL 테스트 ====================


class TestDDLStatements:
    """DDL 문 검증"""

    def test_violation_log_ddl_has_required_columns(self):
        """violation_logs DDL에 필수 컬럼 존재"""
        required_columns = [
            "id UUID",
            "rule_code VARCHAR",
            "rule_name VARCHAR",
            "severity VARCHAR",
            "message TEXT",
            "layer VARCHAR",
            "project_id VARCHAR",
            "was_auto_fixed BOOLEAN",
        ]
        for col in required_columns:
            assert col.lower().split()[0] in VIOLATION_LOG_DDL.lower()

    def test_violation_log_ddl_has_indexes(self):
        """violation_logs DDL에 인덱스 존재"""
        assert "CREATE INDEX" in VIOLATION_LOG_DDL
        assert "idx_violation_logs_rule_code" in VIOLATION_LOG_DDL

    def test_violation_pattern_ddl_has_required_columns(self):
        """violation_patterns DDL에 필수 컬럼 존재"""
        required = ["pattern_type", "rule_codes", "confidence", "description"]
        for col in required:
            assert col in VIOLATION_PATTERN_DDL.lower()

    def test_rule_weight_ddl_has_required_columns(self):
        """rule_weights DDL에 필수 컬럼 존재"""
        required = ["rule_code", "base_weight", "adjusted_weight", "violation_count"]
        for col in required:
            assert col in RULE_WEIGHT_DDL.lower()


# ==================== 싱글톤 테스트 ====================


class TestSingletonFactory:
    """싱글톤 팩토리 함수 테스트"""

    @pytest.mark.asyncio
    async def test_get_violation_storage_without_dsn_raises(self):
        """DSN 없이 최초 호출 시 ValueError"""
        # 싱글톤 리셋
        import src.feedback.storage as storage_module

        storage_module._storage = None

        with pytest.raises(ValueError, match="DSN is required"):
            await get_violation_storage()

    @pytest.mark.asyncio
    async def test_get_violation_storage_returns_singleton(self, mock_asyncpg):
        """동일 인스턴스 반환"""
        mock_module, mock_pool, mock_conn = mock_asyncpg

        # 싱글톤 리셋
        import src.feedback.storage as storage_module

        storage_module._storage = None

        storage1 = await get_violation_storage("postgresql://localhost/test")
        storage2 = await get_violation_storage()  # DSN 없이 호출 가능

        assert storage1 is storage2

        # 클린업
        storage_module._storage = None


# ==================== 에러 케이스 테스트 ====================


class TestErrorHandling:
    """에러 처리 테스트"""

    @pytest.mark.asyncio
    async def test_operations_fail_without_initialization(self):
        """초기화 없이 모든 연산 실패"""
        with patch("src.feedback.storage.asyncpg", MagicMock()):
            storage = ViolationStorage("postgresql://localhost/test")

            with pytest.raises(RuntimeError):
                await storage.get_violation_logs()

            with pytest.raises(RuntimeError):
                await storage.get_violation_count_by_rule()

            with pytest.raises(RuntimeError):
                await storage.get_patterns()

            with pytest.raises(RuntimeError):
                await storage.get_all_rule_weights()
