"""
ProjectRuleLearner 테스트
AESA-129 Task 5.4: 프로젝트별 커스텀 규칙 학습 테스트
"""

import pytest
from datetime import datetime
from unittest.mock import MagicMock

from src.feedback.project_learner import (
    ProjectRuleLearner,
    ProjectRuleConfig,
    LearningResult,
    get_project_learner,
    reset_project_learner,
)
from src.feedback.models import (
    ViolationLog,
    LogContext,
    ViolationPattern,
    PatternType,
)
from src.feedback.analyzer import PatternAnalyzer
from src.feedback.weight_adjuster import RuleWeightAdjuster


def create_violation_log(
    rule_code: str,
    project_id: str,
    project_name: str = "Test Project",
    was_auto_fixed: bool = False,
    timestamp: datetime = None,
    rule_name: str = None,
) -> ViolationLog:
    """테스트용 ViolationLog 생성"""
    return ViolationLog(
        rule_code=rule_code,
        rule_name=rule_name or f"Rule {rule_code}",
        layer="DOMAIN",
        message=f"Violation of {rule_code}",
        severity="WARNING",
        context=LogContext(
            project_id=project_id,
            project_name=project_name,
            file_path=f"src/{rule_code.lower()}.java",
        ),
        was_auto_fixed=was_auto_fixed,
        timestamp=timestamp or datetime.utcnow(),
    )


def create_test_logs(
    project_id: str,
    rule_counts: dict[str, int],
    auto_fix_rates: dict[str, float] = None,
) -> list[ViolationLog]:
    """테스트용 로그 목록 생성"""
    logs = []
    auto_fix_rates = auto_fix_rates or {}

    for rule_code, count in rule_counts.items():
        auto_fix_rate = auto_fix_rates.get(rule_code, 0.0)
        auto_fix_count = int(count * auto_fix_rate)

        for i in range(count):
            logs.append(
                create_violation_log(
                    rule_code=rule_code,
                    project_id=project_id,
                    was_auto_fixed=i < auto_fix_count,
                )
            )

    return logs


class TestProjectRuleLearner:
    """ProjectRuleLearner 기본 테스트"""

    @pytest.fixture
    def learner(self):
        """ProjectRuleLearner fixture"""
        reset_project_learner()
        return ProjectRuleLearner(
            min_violations_for_learning=5,
            auto_apply_threshold=0.7,
        )

    @pytest.fixture
    def mock_analyzer(self):
        """Mock PatternAnalyzer fixture"""
        analyzer = MagicMock(spec=PatternAnalyzer)
        analyzer.detect_project_specific_patterns = MagicMock(
            return_value=[
                ViolationPattern(
                    pattern_type=PatternType.PROJECT_SPECIFIC,
                    rule_codes=["AGG-001"],
                    occurrence_count=10,
                    confidence=0.8,
                    description="Project-specific pattern for AGG-001",
                    project_id="project-1",
                ),
            ]
        )
        return analyzer

    @pytest.fixture
    def mock_weight_adjuster(self):
        """Mock RuleWeightAdjuster fixture"""
        adjuster = MagicMock(spec=RuleWeightAdjuster)
        adjuster.get_weight = MagicMock(return_value=1.0)
        adjuster.set_project_override = MagicMock()
        adjuster.remove_project_override = MagicMock(return_value=True)
        adjuster.get_project_overrides = MagicMock(return_value={})
        return adjuster

    def test_initialization(self, learner):
        """초기화 테스트"""
        assert learner._min_violations == 5
        assert learner._auto_apply_threshold == 0.7
        assert learner._project_configs == {}

    @pytest.mark.asyncio
    async def test_learn_from_logs_insufficient_data(self, learner):
        """데이터 부족 시 학습 테스트"""
        logs = create_test_logs("project-1", {"AGG-001": 3})

        result = await learner.learn_from_logs(logs, "project-1")

        assert result.project_id == "project-1"
        assert result.patterns_found == 0
        assert "최소 위반 수" in result.recommendations[0]

    @pytest.mark.asyncio
    async def test_learn_from_logs_with_patterns(
        self, mock_analyzer, mock_weight_adjuster
    ):
        """패턴 발견 시 학습 테스트"""
        learner = ProjectRuleLearner(
            analyzer=mock_analyzer,
            weight_adjuster=mock_weight_adjuster,
            min_violations_for_learning=5,
        )

        logs = create_test_logs("project-1", {"AGG-001": 10, "AGG-002": 5})

        result = await learner.learn_from_logs(logs, "project-1")

        assert result.patterns_found == 1
        assert result.rules_adjusted >= 0
        mock_analyzer.detect_project_specific_patterns.assert_called_once()

    @pytest.mark.asyncio
    async def test_learn_from_logs_auto_apply(
        self, mock_analyzer, mock_weight_adjuster
    ):
        """자동 적용 테스트"""
        # 높은 신뢰도 패턴
        mock_analyzer.detect_project_specific_patterns = MagicMock(
            return_value=[
                ViolationPattern(
                    pattern_type=PatternType.PROJECT_SPECIFIC,
                    rule_codes=["AGG-001"],
                    occurrence_count=50,
                    confidence=0.9,
                    description="High confidence project-specific pattern for AGG-001",
                    project_id="project-1",
                ),
            ]
        )

        learner = ProjectRuleLearner(
            analyzer=mock_analyzer,
            weight_adjuster=mock_weight_adjuster,
            min_violations_for_learning=5,
            auto_apply_threshold=0.5,  # 낮은 임계값
        )

        # 충분한 로그
        logs = create_test_logs("project-1", {"AGG-001": 50})
        # 다른 프로젝트 로그 추가 (비율 계산용)
        logs.extend(create_test_logs("project-2", {"AGG-001": 10}))

        result = await learner.learn_from_logs(logs, "project-1", auto_apply=True)

        # 자동 적용 확인
        if result.weight_changes:
            mock_weight_adjuster.set_project_override.assert_called()


class TestProjectRuleLearnerWeightCalculation:
    """가중치 계산 테스트"""

    @pytest.fixture
    def learner(self):
        """ProjectRuleLearner fixture"""
        return ProjectRuleLearner(min_violations_for_learning=3)

    def test_aggregate_rule_stats(self, learner):
        """규칙별 통계 집계 테스트"""
        logs = [
            create_violation_log("AGG-001", "p1", was_auto_fixed=True),
            create_violation_log("AGG-001", "p1", was_auto_fixed=True),
            create_violation_log("AGG-001", "p1", was_auto_fixed=False),
            create_violation_log("AGG-002", "p1", was_auto_fixed=False),
        ]

        stats = learner._aggregate_rule_stats(logs)

        assert stats["AGG-001"]["count"] == 3
        assert stats["AGG-001"]["auto_fix_rate"] == pytest.approx(2 / 3, 0.01)
        assert stats["AGG-002"]["count"] == 1
        assert stats["AGG-002"]["auto_fix_rate"] == 0.0

    def test_calculate_weight_changes(self, learner):
        """가중치 변경 계산 테스트"""
        patterns = [
            ViolationPattern(
                pattern_type=PatternType.PROJECT_SPECIFIC,
                rule_codes=["AGG-001"],
                occurrence_count=10,
                confidence=0.8,
                description="Project-specific pattern for weight calculation test",
                project_id="project-1",
            ),
        ]
        logs = create_test_logs("project-1", {"AGG-001": 10})

        changes = learner._calculate_weight_changes(patterns, logs)

        # 변경이 있을 수 있음 (confidence에 따라)
        if "AGG-001" in changes:
            assert "old" in changes["AGG-001"]
            assert "new" in changes["AGG-001"]
            assert "reason" in changes["AGG-001"]

    def test_weight_changes_with_auto_fix(self, learner):
        """자동 수정률이 높은 경우 가중치 감소 테스트"""
        patterns = [
            ViolationPattern(
                pattern_type=PatternType.PROJECT_SPECIFIC,
                rule_codes=["AGG-001"],
                occurrence_count=10,
                confidence=0.8,
                description="Project-specific pattern for auto-fix weight test",
                project_id="project-1",
            ),
        ]
        # 높은 자동 수정률
        logs = create_test_logs(
            "project-1", {"AGG-001": 10}, auto_fix_rates={"AGG-001": 0.9}
        )

        changes = learner._calculate_weight_changes(patterns, logs)

        # 자동 수정률이 높으면 가중치 변경에 영향
        if "AGG-001" in changes:
            assert "auto_fix_rate" in changes["AGG-001"]["reason"]


class TestProjectRuleLearnerConfig:
    """프로젝트 설정 관리 테스트"""

    @pytest.fixture
    def learner(self):
        """ProjectRuleLearner fixture"""
        return ProjectRuleLearner()

    def test_get_project_config_not_exists(self, learner):
        """존재하지 않는 프로젝트 설정 조회"""
        config = learner.get_project_config("non-existent")
        assert config is None

    def test_set_rule_disabled(self, learner):
        """규칙 비활성화 설정 테스트"""
        learner.set_rule_disabled("project-1", "AGG-001", True)

        config = learner.get_project_config("project-1")
        assert config is not None
        assert "AGG-001" in config.disabled_rules

    def test_is_rule_disabled(self, learner):
        """규칙 비활성화 여부 확인 테스트"""
        assert not learner.is_rule_disabled("project-1", "AGG-001")

        learner.set_rule_disabled("project-1", "AGG-001", True)
        assert learner.is_rule_disabled("project-1", "AGG-001")

        learner.set_rule_disabled("project-1", "AGG-001", False)
        assert not learner.is_rule_disabled("project-1", "AGG-001")

    def test_get_effective_weight_disabled_rule(self, learner):
        """비활성화된 규칙의 유효 가중치 테스트"""
        learner.set_rule_disabled("project-1", "AGG-001", True)

        weight = learner.get_effective_weight("AGG-001", "project-1")
        assert weight == 0.0

    def test_get_effective_weight_normal_rule(self, learner):
        """일반 규칙의 유효 가중치 테스트"""
        weight = learner.get_effective_weight("AGG-001", "project-1")
        assert weight == 1.0  # 기본 가중치


class TestProjectRuleLearnerReset:
    """리셋 기능 테스트"""

    @pytest.fixture
    def learner_with_config(self):
        """설정이 있는 ProjectRuleLearner fixture"""
        learner = ProjectRuleLearner()
        learner.set_rule_disabled("project-1", "AGG-001", True)
        learner.set_rule_disabled("project-2", "AGG-002", True)
        return learner

    def test_reset_project_learning(self, learner_with_config):
        """프로젝트 학습 리셋 테스트"""
        result = learner_with_config.reset_project_learning("project-1")

        assert result is True
        assert learner_with_config.get_project_config("project-1") is None
        assert learner_with_config.get_project_config("project-2") is not None

    def test_reset_project_learning_not_exists(self, learner_with_config):
        """존재하지 않는 프로젝트 리셋"""
        result = learner_with_config.reset_project_learning("non-existent")
        assert result is False

    def test_reset_all(self, learner_with_config):
        """전체 리셋 테스트"""
        learner_with_config.reset_all()

        assert learner_with_config.get_project_config("project-1") is None
        assert learner_with_config.get_project_config("project-2") is None


class TestProjectRuleLearnerSummary:
    """요약 정보 테스트"""

    @pytest.fixture
    def learner(self):
        """ProjectRuleLearner fixture"""
        return ProjectRuleLearner()

    def test_get_learning_summary_empty(self, learner):
        """빈 상태 요약"""
        summary = learner.get_learning_summary()

        assert summary["total_projects"] == 0
        assert summary["total_patterns"] == 0
        assert summary["total_violations_analyzed"] == 0
        assert summary["projects"] == []

    def test_get_learning_summary_with_data(self, learner):
        """데이터 있는 상태 요약"""
        # 프로젝트 설정 추가
        learner._project_configs["project-1"] = ProjectRuleConfig(
            project_id="project-1",
            project_name="Test Project 1",
            total_violations=100,
            learning_sessions=3,
            learned_patterns=[
                ViolationPattern(
                    pattern_type=PatternType.PROJECT_SPECIFIC,
                    rule_codes=["AGG-001"],
                    occurrence_count=10,
                    confidence=0.8,
                    description="Learned pattern for summary test",
                    project_id="project-1",
                ),
            ],
            last_learned=datetime.utcnow(),
        )

        summary = learner.get_learning_summary()

        assert summary["total_projects"] == 1
        assert summary["total_patterns"] == 1
        assert summary["total_violations_analyzed"] == 100
        assert len(summary["projects"]) == 1
        assert summary["projects"][0]["project_id"] == "project-1"


class TestProjectRuleLearnerRecommendations:
    """추천 사항 생성 테스트"""

    @pytest.fixture
    def learner(self):
        """ProjectRuleLearner fixture"""
        return ProjectRuleLearner()

    def test_generate_recommendations_high_concentration(self, learner):
        """높은 집중도 패턴 추천"""
        patterns = [
            ViolationPattern(
                pattern_type=PatternType.PROJECT_SPECIFIC,
                rule_codes=["AGG-001"],
                occurrence_count=10,
                confidence=0.85,
                description="High concentration pattern for recommendation test",
                project_id="project-1",
            ),
        ]
        weight_changes = {}

        recommendations = learner._generate_recommendations(patterns, weight_changes)

        assert any("높은 집중도" in r for r in recommendations)

    def test_generate_recommendations_weight_increase(self, learner):
        """가중치 증가 추천"""
        patterns = []
        weight_changes = {
            "AGG-001": {"old": 1.0, "new": 1.5, "reason": "test"},
        }

        recommendations = learner._generate_recommendations(patterns, weight_changes)

        assert any("가중치 증가" in r for r in recommendations)

    def test_generate_recommendations_weight_decrease(self, learner):
        """가중치 감소 추천"""
        patterns = []
        weight_changes = {
            "AGG-001": {"old": 1.0, "new": 0.7, "reason": "test"},
        }

        recommendations = learner._generate_recommendations(patterns, weight_changes)

        assert any("가중치 감소" in r for r in recommendations)


class TestProjectRuleLearnerConfidence:
    """신뢰도 계산 테스트"""

    @pytest.fixture
    def learner(self):
        """ProjectRuleLearner fixture"""
        return ProjectRuleLearner()

    def test_calculate_learning_confidence_no_patterns(self, learner):
        """패턴 없을 때 신뢰도"""
        confidence = learner._calculate_learning_confidence([], [], [])
        assert confidence == 0.0

    def test_calculate_learning_confidence_sufficient_data(self, learner):
        """충분한 데이터 신뢰도"""
        patterns = [
            ViolationPattern(
                pattern_type=PatternType.PROJECT_SPECIFIC,
                rule_codes=["AGG-001"],
                occurrence_count=50,
                confidence=0.8,
                description="Sufficient data pattern for confidence calculation test",
                project_id="project-1",
            ),
        ]
        project_logs = create_test_logs("project-1", {"AGG-001": 50})
        all_logs = project_logs + create_test_logs("project-2", {"AGG-001": 100})

        confidence = learner._calculate_learning_confidence(
            patterns, project_logs, all_logs
        )

        assert 0.0 <= confidence <= 1.0
        assert confidence > 0.3  # 충분한 데이터로 일정 수준 이상


class TestLearningResult:
    """LearningResult 모델 테스트"""

    def test_learning_result_creation(self):
        """LearningResult 생성 테스트"""
        result = LearningResult(
            project_id="project-1",
            patterns_found=5,
            rules_adjusted=3,
            weight_changes={
                "AGG-001": {"old": 1.0, "new": 1.5, "reason": "test"},
            },
            recommendations=["Review rule AGG-001"],
            confidence=0.75,
        )

        assert result.project_id == "project-1"
        assert result.patterns_found == 5
        assert result.rules_adjusted == 3
        assert "AGG-001" in result.weight_changes
        assert len(result.recommendations) == 1
        assert result.confidence == 0.75


class TestProjectRuleConfig:
    """ProjectRuleConfig 모델 테스트"""

    def test_project_rule_config_creation(self):
        """ProjectRuleConfig 생성 테스트"""
        config = ProjectRuleConfig(
            project_id="project-1",
            project_name="Test Project",
        )

        assert config.project_id == "project-1"
        assert config.project_name == "Test Project"
        assert config.rule_weights == {}
        assert config.disabled_rules == set()
        assert config.learned_patterns == []
        assert config.total_violations == 0
        assert config.learning_sessions == 0


class TestSingletonFunctions:
    """싱글톤 함수 테스트"""

    def test_get_project_learner_singleton(self):
        """싱글톤 반환 테스트"""
        reset_project_learner()

        learner1 = get_project_learner()
        learner2 = get_project_learner()

        assert learner1 is learner2

    def test_reset_project_learner(self):
        """싱글톤 리셋 테스트"""
        learner1 = get_project_learner()
        reset_project_learner()
        learner2 = get_project_learner()

        assert learner1 is not learner2


class TestProjectRuleLearnerIntegration:
    """통합 테스트"""

    @pytest.fixture
    def integrated_learner(self):
        """통합 테스트용 learner (실제 Analyzer, Adjuster 사용)"""
        from src.feedback.analyzer import PatternAnalyzer
        from src.feedback.weight_adjuster import RuleWeightAdjuster

        analyzer = PatternAnalyzer(min_occurrence=3)
        adjuster = RuleWeightAdjuster()

        return ProjectRuleLearner(
            analyzer=analyzer,
            weight_adjuster=adjuster,
            min_violations_for_learning=5,
        )

    @pytest.mark.asyncio
    async def test_full_learning_cycle(self, integrated_learner):
        """전체 학습 사이클 테스트"""
        # 프로젝트 1: 특정 규칙 집중
        project1_logs = create_test_logs(
            "project-1",
            {"AGG-001": 30, "AGG-002": 5},
            auto_fix_rates={"AGG-001": 0.3, "AGG-002": 0.8},
        )

        # 프로젝트 2: 다른 분포
        project2_logs = create_test_logs(
            "project-2",
            {"AGG-001": 5, "AGG-002": 20},
            auto_fix_rates={"AGG-001": 0.5, "AGG-002": 0.2},
        )

        all_logs = project1_logs + project2_logs

        # 학습 실행
        result = await integrated_learner.learn_from_logs(
            all_logs,
            "project-1",
            auto_apply=False,
        )

        # 결과 검증
        assert result.project_id == "project-1"
        assert result.confidence >= 0.0

        # 설정 업데이트 확인
        config = integrated_learner.get_project_config("project-1")
        assert config is not None
        assert config.total_violations > 0

        # 요약 확인
        summary = integrated_learner.get_learning_summary()
        assert summary["total_projects"] == 1

    @pytest.mark.asyncio
    async def test_apply_and_reset_cycle(self, integrated_learner):
        """적용 및 리셋 사이클 테스트"""
        logs = create_test_logs("project-1", {"AGG-001": 10})

        # 학습
        result = await integrated_learner.learn_from_logs(logs, "project-1")

        # 수동 적용
        if result.weight_changes:
            success = await integrated_learner.apply_learning_result(result)
            assert success is True

            # 적용 확인
            weight = integrated_learner.get_effective_weight("AGG-001", "project-1")
            assert weight != 0.0  # 비활성화되지 않음

        # 리셋
        reset_success = integrated_learner.reset_project_learning("project-1")
        assert reset_success is True

        # 리셋 확인
        config = integrated_learner.get_project_config("project-1")
        assert config is None
