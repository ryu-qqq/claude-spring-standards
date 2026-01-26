"""
RuleWeightAdjuster 테스트
AESA-129 Task 5.3: 위반 빈도 기반 규칙 가중치 동적 조정 테스트
"""

import pytest
from datetime import datetime, timedelta
from uuid import uuid4

from src.feedback.weight_adjuster import (
    RuleWeightAdjuster,
    WeightCalculationConfig,
    get_weight_adjuster,
    reset_weight_adjuster,
)
from src.feedback.models import (
    ViolationLog,
    ViolationPattern,
    LogContext,
    PatternType,
)


def create_test_log(
    rule_code: str = "AGG-001",
    rule_name: str = "Lombok 금지",
    layer: str = "DOMAIN",
    project_id: str = "test-project",
    timestamp: datetime = None,
    was_auto_fixed: bool = False,
) -> ViolationLog:
    """테스트용 ViolationLog 생성"""
    return ViolationLog(
        log_id=str(uuid4()),
        rule_code=rule_code,
        rule_name=rule_name,
        severity="CRITICAL",
        message=f"테스트 위반: {rule_code}",
        layer=layer,
        context=LogContext(
            project_id=project_id,
            user_id="user-1",
            session_id=str(uuid4()),
            file_path="src/Order.java",
        ),
        timestamp=timestamp or datetime.utcnow(),
        was_auto_fixed=was_auto_fixed,
    )


class TestWeightCalculationConfig:
    """WeightCalculationConfig 테스트"""

    def test_default_values(self):
        """기본값 확인"""
        config = WeightCalculationConfig()

        assert config.MIN_WEIGHT == 0.1
        assert config.MAX_WEIGHT == 3.0
        assert config.DEFAULT_WEIGHT == 1.0
        assert config.FREQUENCY_MULTIPLIER == 0.1
        assert config.FREQUENCY_CAP == 50
        assert config.AUTO_FIX_DISCOUNT == 0.3
        assert config.TIME_DECAY_HALF_LIFE_DAYS == 30

    def test_pattern_boost_values(self):
        """패턴 부스트 값 확인"""
        config = WeightCalculationConfig()

        assert config.RECURRING_PATTERN_BOOST == 0.2
        assert config.CORRELATED_PATTERN_BOOST == 0.1
        assert config.PROJECT_SPECIFIC_ADJUSTMENT == 0.15


class TestRuleWeightAdjusterBasic:
    """RuleWeightAdjuster 기본 기능 테스트"""

    @pytest.fixture
    def adjuster(self):
        """RuleWeightAdjuster fixture"""
        return RuleWeightAdjuster()

    def test_calculate_weight_default(self, adjuster):
        """기본 가중치 계산"""
        weight = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=0,
        )

        assert weight == 1.0  # DEFAULT_WEIGHT

    def test_calculate_weight_with_violations(self, adjuster):
        """위반 횟수에 따른 가중치 증가"""
        weight_0 = adjuster.calculate_weight("AGG-001", violation_count=0)
        weight_10 = adjuster.calculate_weight("AGG-001", violation_count=10)
        weight_50 = adjuster.calculate_weight("AGG-001", violation_count=50)

        # 위반 횟수가 많을수록 가중치 증가
        assert weight_10 > weight_0
        assert weight_50 > weight_10

    def test_calculate_weight_frequency_cap(self, adjuster):
        """빈도 상한 테스트"""
        weight_50 = adjuster.calculate_weight("AGG-001", violation_count=50)
        weight_100 = adjuster.calculate_weight("AGG-001", violation_count=100)
        weight_500 = adjuster.calculate_weight("AGG-001", violation_count=500)

        # FREQUENCY_CAP(50)을 넘어가면 동일한 가중치
        assert weight_50 == weight_100
        assert weight_100 == weight_500

    def test_calculate_weight_auto_fix_discount(self, adjuster):
        """자동 수정 비율에 따른 가중치 감소"""
        weight_no_fix = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            auto_fix_rate=0.0,
        )
        weight_half_fix = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            auto_fix_rate=0.5,
        )
        weight_full_fix = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            auto_fix_rate=1.0,
        )

        # 자동 수정 비율이 높을수록 가중치 감소
        assert weight_half_fix < weight_no_fix
        assert weight_full_fix < weight_half_fix

    def test_calculate_weight_bounds(self, adjuster):
        """가중치 범위 제한 테스트"""
        config = adjuster._config

        # 최소값 테스트 (높은 auto_fix_rate로 감소)
        weight_min = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=0,
            auto_fix_rate=1.0,
        )
        assert weight_min >= config.MIN_WEIGHT

        # 최대값 테스트 (높은 위반 횟수)
        weight_max = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=1000,
            auto_fix_rate=0.0,
        )
        assert weight_max <= config.MAX_WEIGHT


class TestTimeDecay:
    """시간 감쇠 테스트"""

    @pytest.fixture
    def adjuster(self):
        return RuleWeightAdjuster()

    def test_recent_violation_boost(self, adjuster):
        """최근 위반에 대한 가중치 증가"""
        now = datetime.utcnow()

        weight_recent = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            last_violation_time=now,
        )
        weight_old = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            last_violation_time=now - timedelta(days=60),
        )
        weight_no_time = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
        )

        # 최근 위반이 더 높은 가중치
        assert weight_recent > weight_old
        # 시간 정보 없는 경우와 비교
        assert weight_recent >= weight_no_time

    def test_time_decay_calculation(self, adjuster):
        """시간 감쇠 계산 정확성"""
        now = datetime.utcnow()

        # 방금 전
        decay_now = adjuster._calculate_time_decay(now)
        assert decay_now < 0.1  # 거의 0

        # 30일 전 (half-life)
        decay_30d = adjuster._calculate_time_decay(now - timedelta(days=30))
        assert 0.4 < decay_30d < 0.6  # 약 0.5

        # 90일 전
        decay_90d = adjuster._calculate_time_decay(now - timedelta(days=90))
        assert decay_90d > 0.8  # 대부분 감쇠


class TestPatternAdjustment:
    """패턴 기반 가중치 조정 테스트"""

    @pytest.fixture
    def adjuster(self):
        return RuleWeightAdjuster()

    def test_recurring_pattern_boost(self, adjuster):
        """반복 패턴 부스트"""
        patterns = [
            ViolationPattern(
                pattern_id="pattern-1",
                pattern_type=PatternType.RECURRING,
                rule_codes=["AGG-001"],
                occurrence_count=5,
                confidence=0.8,
                description="반복 위반 패턴",
            )
        ]

        weight_no_pattern = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
        )
        weight_with_pattern = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            patterns=patterns,
        )

        assert weight_with_pattern > weight_no_pattern

    def test_correlated_pattern_boost(self, adjuster):
        """상관 패턴 부스트"""
        patterns = [
            ViolationPattern(
                pattern_id="pattern-1",
                pattern_type=PatternType.CORRELATED,
                rule_codes=["AGG-001", "AGG-002"],
                occurrence_count=3,
                confidence=0.7,
                description="상관 위반 패턴",
            )
        ]

        weight_no_pattern = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
        )
        weight_with_pattern = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            patterns=patterns,
        )

        assert weight_with_pattern > weight_no_pattern

    def test_project_specific_pattern(self, adjuster):
        """프로젝트 특화 패턴"""
        patterns = [
            ViolationPattern(
                pattern_id="pattern-1",
                pattern_type=PatternType.PROJECT_SPECIFIC,
                rule_codes=["AGG-001"],
                occurrence_count=10,
                confidence=0.9,
                description="프로젝트 특화 패턴",
            )
        ]

        weight_no_pattern = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
        )
        weight_with_pattern = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            patterns=patterns,
        )

        assert weight_with_pattern > weight_no_pattern

    def test_pattern_not_matching_rule(self, adjuster):
        """규칙과 무관한 패턴"""
        patterns = [
            ViolationPattern(
                pattern_id="pattern-1",
                pattern_type=PatternType.RECURRING,
                rule_codes=["AGG-002"],  # 다른 규칙
                occurrence_count=5,
                confidence=0.8,
                description="다른 규칙 패턴",
            )
        ]

        weight_no_pattern = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
        )
        weight_with_pattern = adjuster.calculate_weight(
            rule_code="AGG-001",
            violation_count=10,
            patterns=patterns,
        )

        # 패턴이 다른 규칙에 대한 것이면 영향 없음
        assert weight_with_pattern == weight_no_pattern


class TestUpdateWeightsFromLogs:
    """로그 기반 가중치 업데이트 테스트"""

    @pytest.fixture
    def adjuster(self):
        return RuleWeightAdjuster()

    @pytest.mark.asyncio
    async def test_update_from_empty_logs(self, adjuster):
        """빈 로그로 업데이트"""
        weights = await adjuster.update_weights_from_logs([])

        assert weights == {}

    @pytest.mark.asyncio
    async def test_update_from_single_rule_logs(self, adjuster):
        """단일 규칙 로그로 업데이트"""
        logs = [
            create_test_log(rule_code="AGG-001"),
            create_test_log(rule_code="AGG-001"),
            create_test_log(rule_code="AGG-001"),
        ]

        weights = await adjuster.update_weights_from_logs(logs)

        assert "AGG-001" in weights
        assert weights["AGG-001"].violation_count == 3
        assert weights["AGG-001"].adjusted_weight > 1.0

    @pytest.mark.asyncio
    async def test_update_from_multiple_rules(self, adjuster):
        """다중 규칙 로그로 업데이트"""
        logs = [
            create_test_log(rule_code="AGG-001"),
            create_test_log(rule_code="AGG-001"),
            create_test_log(rule_code="AGG-002"),
            create_test_log(rule_code="ENT-001"),
            create_test_log(rule_code="ENT-001"),
            create_test_log(rule_code="ENT-001"),
            create_test_log(rule_code="ENT-001"),
        ]

        weights = await adjuster.update_weights_from_logs(logs)

        assert len(weights) == 3
        assert weights["AGG-001"].violation_count == 2
        assert weights["AGG-002"].violation_count == 1
        assert weights["ENT-001"].violation_count == 4

        # 위반 횟수가 많은 규칙이 더 높은 가중치
        assert weights["ENT-001"].adjusted_weight > weights["AGG-002"].adjusted_weight

    @pytest.mark.asyncio
    async def test_update_with_auto_fix(self, adjuster):
        """자동 수정 포함 로그"""
        logs = [
            create_test_log(rule_code="AGG-001", was_auto_fixed=True),
            create_test_log(rule_code="AGG-001", was_auto_fixed=True),
            create_test_log(rule_code="AGG-001", was_auto_fixed=False),
            create_test_log(rule_code="AGG-002", was_auto_fixed=False),
            create_test_log(rule_code="AGG-002", was_auto_fixed=False),
        ]

        weights = await adjuster.update_weights_from_logs(logs)

        # AGG-001: 3건 중 2건 자동 수정 (66.7%)
        assert weights["AGG-001"].auto_fix_rate == pytest.approx(2 / 3, rel=0.01)
        # AGG-002: 2건 중 0건 자동 수정 (0%)
        assert weights["AGG-002"].auto_fix_rate == 0.0

        # 자동 수정 비율이 높은 규칙은 낮은 가중치
        # (단, 위반 횟수 차이도 있으므로 단순 비교는 어려움)

    @pytest.mark.asyncio
    async def test_update_preserves_existing(self, adjuster):
        """기존 가중치 업데이트"""
        logs1 = [create_test_log(rule_code="AGG-001") for _ in range(3)]
        await adjuster.update_weights_from_logs(logs1)

        logs2 = [create_test_log(rule_code="AGG-001") for _ in range(2)]
        weights = await adjuster.update_weights_from_logs(logs2)

        # 새 로그로 업데이트됨 (누적이 아님)
        assert weights["AGG-001"].violation_count == 2


class TestWeightQuery:
    """가중치 조회 테스트"""

    @pytest.fixture
    def adjuster(self):
        return RuleWeightAdjuster()

    def test_get_weight_unknown_rule(self, adjuster):
        """알 수 없는 규칙 조회"""
        weight = adjuster.get_weight("UNKNOWN-001")

        assert weight == adjuster._config.DEFAULT_WEIGHT

    @pytest.mark.asyncio
    async def test_get_weight_known_rule(self, adjuster):
        """알려진 규칙 조회"""
        logs = [create_test_log(rule_code="AGG-001") for _ in range(5)]
        await adjuster.update_weights_from_logs(logs)

        weight = adjuster.get_weight("AGG-001")

        assert weight > 1.0

    def test_get_all_weights_empty(self, adjuster):
        """빈 가중치 조회"""
        weights = adjuster.get_all_weights()

        assert weights == {}

    @pytest.mark.asyncio
    async def test_get_all_weights(self, adjuster):
        """모든 가중치 조회"""
        logs = [
            create_test_log(rule_code="AGG-001"),
            create_test_log(rule_code="AGG-002"),
        ]
        await adjuster.update_weights_from_logs(logs)

        weights = adjuster.get_all_weights()

        assert len(weights) == 2
        assert "AGG-001" in weights
        assert "AGG-002" in weights

    def test_get_weight_summary_empty(self, adjuster):
        """빈 요약 조회"""
        summary = adjuster.get_weight_summary()

        assert summary["total_rules"] == 0
        assert summary["average_weight"] == adjuster._config.DEFAULT_WEIGHT
        assert summary["weight_distribution"] == {}
        assert summary["last_update"] is None

    @pytest.mark.asyncio
    async def test_get_weight_summary(self, adjuster):
        """가중치 요약 조회"""
        logs = [
            create_test_log(rule_code="AGG-001"),
            create_test_log(rule_code="AGG-002"),
            create_test_log(rule_code="AGG-003"),
        ]
        await adjuster.update_weights_from_logs(logs)

        summary = adjuster.get_weight_summary()

        assert summary["total_rules"] == 3
        assert "average_weight" in summary
        assert "min_weight" in summary
        assert "max_weight" in summary
        assert "weight_distribution" in summary
        assert summary["last_update"] is not None


class TestProjectOverrides:
    """프로젝트별 오버라이드 테스트"""

    @pytest.fixture
    def adjuster(self):
        return RuleWeightAdjuster()

    def test_set_project_override(self, adjuster):
        """프로젝트 오버라이드 설정"""
        adjuster.set_project_override("AGG-001", "project-a", 2.0)

        weight = adjuster.get_weight("AGG-001", project_id="project-a")
        assert weight == 2.0

        # 다른 프로젝트는 기본값
        weight_other = adjuster.get_weight("AGG-001", project_id="project-b")
        assert weight_other == adjuster._config.DEFAULT_WEIGHT

    def test_set_project_override_bounds(self, adjuster):
        """오버라이드 범위 제한"""
        config = adjuster._config

        # 최소값 미만
        adjuster.set_project_override("AGG-001", "project-a", 0.01)
        weight_min = adjuster.get_weight("AGG-001", "project-a")
        assert weight_min == config.MIN_WEIGHT

        # 최대값 초과
        adjuster.set_project_override("AGG-002", "project-a", 5.0)
        weight_max = adjuster.get_weight("AGG-002", "project-a")
        assert weight_max == config.MAX_WEIGHT

    def test_remove_project_override(self, adjuster):
        """프로젝트 오버라이드 제거"""
        adjuster.set_project_override("AGG-001", "project-a", 2.0)
        result = adjuster.remove_project_override("AGG-001", "project-a")

        assert result is True
        weight = adjuster.get_weight("AGG-001", "project-a")
        assert weight == adjuster._config.DEFAULT_WEIGHT

    def test_remove_nonexistent_override(self, adjuster):
        """존재하지 않는 오버라이드 제거"""
        result = adjuster.remove_project_override("AGG-001", "project-x")

        assert result is False

    def test_get_project_overrides(self, adjuster):
        """프로젝트별 오버라이드 조회"""
        adjuster.set_project_override("AGG-001", "project-a", 2.0)
        adjuster.set_project_override("AGG-002", "project-a", 1.5)
        adjuster.set_project_override("AGG-001", "project-b", 0.5)

        overrides_a = adjuster.get_project_overrides("project-a")
        overrides_b = adjuster.get_project_overrides("project-b")

        assert overrides_a == {"AGG-001": 2.0, "AGG-002": 1.5}
        assert overrides_b == {"AGG-001": 0.5}


class TestSingleton:
    """싱글톤 패턴 테스트"""

    def teardown_method(self):
        """각 테스트 후 싱글톤 리셋"""
        reset_weight_adjuster()

    def test_singleton_instance(self):
        """싱글톤 인스턴스 확인"""
        adjuster1 = get_weight_adjuster()
        adjuster2 = get_weight_adjuster()

        assert adjuster1 is adjuster2

    def test_singleton_reset(self):
        """싱글톤 리셋"""
        adjuster1 = get_weight_adjuster()
        reset_weight_adjuster()
        adjuster2 = get_weight_adjuster()

        assert adjuster1 is not adjuster2

    def test_singleton_with_config(self):
        """설정과 함께 싱글톤 생성"""
        config = WeightCalculationConfig()
        adjuster = get_weight_adjuster(config=config)

        assert adjuster._config is config


class TestAggregateRuleStats:
    """규칙 통계 집계 테스트"""

    @pytest.fixture
    def adjuster(self):
        return RuleWeightAdjuster()

    def test_aggregate_empty_logs(self, adjuster):
        """빈 로그 집계"""
        stats = adjuster._aggregate_rule_stats([])

        assert stats == {}

    def test_aggregate_single_rule(self, adjuster):
        """단일 규칙 집계"""
        now = datetime.utcnow()
        logs = [
            create_test_log(rule_code="AGG-001", timestamp=now - timedelta(hours=2)),
            create_test_log(rule_code="AGG-001", timestamp=now - timedelta(hours=1)),
            create_test_log(rule_code="AGG-001", timestamp=now, was_auto_fixed=True),
        ]

        stats = adjuster._aggregate_rule_stats(logs)

        assert "AGG-001" in stats
        assert stats["AGG-001"]["count"] == 3
        assert stats["AGG-001"]["auto_fix_rate"] == pytest.approx(1 / 3, rel=0.01)
        assert stats["AGG-001"]["last_violation"] == now

    def test_aggregate_multiple_rules(self, adjuster):
        """다중 규칙 집계"""
        logs = [
            create_test_log(rule_code="AGG-001"),
            create_test_log(rule_code="AGG-001"),
            create_test_log(rule_code="ENT-001"),
            create_test_log(rule_code="ENT-001", was_auto_fixed=True),
        ]

        stats = adjuster._aggregate_rule_stats(logs)

        assert len(stats) == 2
        assert stats["AGG-001"]["count"] == 2
        assert stats["AGG-001"]["auto_fix_rate"] == 0.0
        assert stats["ENT-001"]["count"] == 2
        assert stats["ENT-001"]["auto_fix_rate"] == 0.5
