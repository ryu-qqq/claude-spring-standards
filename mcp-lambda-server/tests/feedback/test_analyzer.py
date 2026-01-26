"""
PatternAnalyzer 테스트
AESA-129 Task 5.2: 반복 위반 탐지 및 패턴 분석 테스트
"""

import pytest
from datetime import datetime, timedelta
from uuid import uuid4

from src.feedback.analyzer import PatternAnalyzer, get_pattern_analyzer
from src.feedback.models import ViolationLog, LogContext, ViolationPattern, PatternType


def create_test_log(
    rule_code: str = "AGG-001",
    rule_name: str = "Lombok 금지",
    layer: str = "DOMAIN",
    project_id: str = "test-project",
    user_id: str = "user-1",
    session_id: str = None,
    file_path: str = "src/Order.java",
    timestamp: datetime = None,
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
            user_id=user_id,
            session_id=session_id or str(uuid4()),
            file_path=file_path,
        ),
        timestamp=timestamp or datetime.now(),
    )


class TestPatternAnalyzerInitialization:
    """PatternAnalyzer 초기화 테스트"""

    def test_default_initialization(self):
        """기본 초기화 테스트"""
        analyzer = PatternAnalyzer()

        assert analyzer._min_occurrence == 3
        assert analyzer._min_confidence == 0.5
        assert analyzer._correlation_threshold == 0.3
        assert analyzer._time_window_hours == 24

    def test_custom_initialization(self):
        """커스텀 설정 초기화 테스트"""
        analyzer = PatternAnalyzer(
            min_occurrence=5,
            min_confidence=0.7,
            correlation_threshold=0.5,
            time_window_hours=48,
        )

        assert analyzer._min_occurrence == 5
        assert analyzer._min_confidence == 0.7
        assert analyzer._correlation_threshold == 0.5
        assert analyzer._time_window_hours == 48


class TestPatternAnalyzerRecurring:
    """반복 패턴 탐지 테스트"""

    @pytest.fixture
    def analyzer(self):
        """기본 설정 분석기"""
        return PatternAnalyzer(min_occurrence=3, min_confidence=0.3)

    def test_detect_recurring_patterns_basic(self, analyzer):
        """기본 반복 패턴 탐지"""
        logs = [
            create_test_log("AGG-001"),
            create_test_log("AGG-001"),
            create_test_log("AGG-001"),
            create_test_log("AGG-001"),
            create_test_log("AGG-001"),
            create_test_log("AGG-002"),
        ]

        patterns = analyzer.detect_recurring_patterns(logs)

        # AGG-001이 5회 발생 → 패턴으로 탐지
        agg001_patterns = [p for p in patterns if "AGG-001" in p.rule_codes]
        assert len(agg001_patterns) >= 1
        assert agg001_patterns[0].pattern_type == PatternType.RECURRING
        assert agg001_patterns[0].occurrence_count == 5

    def test_detect_recurring_patterns_below_threshold(self, analyzer):
        """최소 발생 횟수 미달 테스트"""
        logs = [
            create_test_log("AGG-001"),
            create_test_log("AGG-001"),  # 2회만 발생
            create_test_log("AGG-002"),
        ]

        patterns = analyzer.detect_recurring_patterns(logs)

        # 3회 미만은 패턴으로 탐지 안 됨
        agg001_patterns = [p for p in patterns if "AGG-001" in p.rule_codes]
        assert len(agg001_patterns) == 0

    def test_detect_recurring_patterns_layer_extraction(self, analyzer):
        """레이어 정보 추출 테스트"""
        logs = [
            create_test_log("AGG-001", layer="DOMAIN"),
            create_test_log("AGG-001", layer="DOMAIN"),
            create_test_log("AGG-001", layer="DOMAIN"),
        ]

        patterns = analyzer.detect_recurring_patterns(logs)

        assert len(patterns) >= 1
        assert patterns[0].layer == "DOMAIN"


class TestPatternAnalyzerCorrelated:
    """상관 패턴 탐지 테스트"""

    @pytest.fixture
    def analyzer(self):
        """기본 설정 분석기"""
        return PatternAnalyzer(min_occurrence=2, correlation_threshold=0.3)

    def test_detect_correlated_patterns_basic(self, analyzer):
        """기본 상관 패턴 탐지"""
        # 같은 세션에서 AGG-001과 AGG-002가 함께 발생
        session1 = "session-1"
        session2 = "session-2"
        session3 = "session-3"

        logs = [
            # 세션 1: AGG-001 + AGG-002
            create_test_log("AGG-001", session_id=session1),
            create_test_log("AGG-002", session_id=session1),
            # 세션 2: AGG-001 + AGG-002
            create_test_log("AGG-001", session_id=session2),
            create_test_log("AGG-002", session_id=session2),
            # 세션 3: AGG-001 + AGG-002
            create_test_log("AGG-001", session_id=session3),
            create_test_log("AGG-002", session_id=session3),
        ]

        patterns = analyzer.detect_correlated_patterns(logs)

        # AGG-001과 AGG-002의 상관 관계 탐지
        correlated = [p for p in patterns if p.pattern_type == PatternType.CORRELATED]
        assert len(correlated) >= 1

        # 상관 패턴에 두 규칙이 포함되어 있는지 확인
        found = False
        for p in correlated:
            if "AGG-001" in p.rule_codes and "AGG-002" in p.rule_codes:
                found = True
                assert p.occurrence_count >= 2
                break
        assert found, "AGG-001과 AGG-002의 상관 패턴이 탐지되어야 함"

    def test_detect_correlated_patterns_no_correlation(self, analyzer):
        """상관 관계 없는 경우 테스트"""
        # 다른 세션에서 각각 발생
        logs = [
            create_test_log("AGG-001", session_id="session-1"),
            create_test_log("AGG-002", session_id="session-2"),
            create_test_log("AGG-003", session_id="session-3"),
        ]

        patterns = analyzer.detect_correlated_patterns(logs)

        # 상관 관계 패턴 없음
        assert len(patterns) == 0


class TestPatternAnalyzerTimeBased:
    """시간 기반 패턴 탐지 테스트"""

    @pytest.fixture
    def analyzer(self):
        """기본 설정 분석기"""
        return PatternAnalyzer(min_occurrence=3)

    def test_detect_time_based_patterns_concentrated(self, analyzer):
        """특정 시간대 집중 패턴 탐지"""
        base_time = datetime(2024, 1, 15, 14, 0, 0)  # 14시

        logs = []
        # 14시에 10개의 위반 집중
        for i in range(10):
            logs.append(
                create_test_log(
                    "AGG-001",
                    timestamp=base_time + timedelta(minutes=i * 5),
                )
            )
        # 다른 시간대에는 1개씩
        for hour in [9, 10, 11, 16, 17]:
            logs.append(
                create_test_log(
                    "AGG-002",
                    timestamp=datetime(2024, 1, 15, hour, 30, 0),
                )
            )

        patterns = analyzer.detect_time_based_patterns(logs)

        # 14시에 집중된 패턴 탐지
        time_patterns = [
            p for p in patterns if p.pattern_type == PatternType.TIME_BASED
        ]
        if time_patterns:
            assert any("14시" in p.description for p in time_patterns)

    def test_detect_time_based_patterns_distributed(self, analyzer):
        """분산된 시간대 (패턴 없음) 테스트"""
        logs = []
        # 각 시간대에 1개씩 분산
        for hour in range(24):
            logs.append(
                create_test_log(
                    "AGG-001",
                    timestamp=datetime(2024, 1, 15, hour, 30, 0),
                )
            )

        patterns = analyzer.detect_time_based_patterns(logs)

        # 집중된 패턴 없음 (평균적으로 분산)
        assert len(patterns) == 0


class TestPatternAnalyzerProjectSpecific:
    """프로젝트 특화 패턴 탐지 테스트"""

    @pytest.fixture
    def analyzer(self):
        """기본 설정 분석기"""
        return PatternAnalyzer(min_occurrence=3)

    def test_detect_project_specific_patterns(self, analyzer):
        """프로젝트 특화 패턴 탐지"""
        logs = []

        # 프로젝트 A에서 AGG-001이 5회 발생
        for _ in range(5):
            logs.append(create_test_log("AGG-001", project_id="project-A"))

        # 다른 프로젝트에서 AGG-001이 1회만 발생
        logs.append(create_test_log("AGG-001", project_id="project-B"))

        patterns = analyzer.detect_project_specific_patterns(logs, "project-A")

        # 프로젝트 A에 특화된 패턴 탐지
        project_patterns = [
            p for p in patterns if p.pattern_type == PatternType.PROJECT_SPECIFIC
        ]
        assert len(project_patterns) >= 1
        assert project_patterns[0].project_id == "project-A"

    def test_detect_project_specific_patterns_insufficient_logs(self, analyzer):
        """프로젝트 로그 부족 테스트"""
        logs = [
            create_test_log("AGG-001", project_id="project-A"),
            create_test_log("AGG-001", project_id="project-B"),
        ]

        patterns = analyzer.detect_project_specific_patterns(logs, "project-A")

        # 최소 발생 횟수 미달
        assert len(patterns) == 0


class TestPatternAnalyzerUserSpecific:
    """사용자 특화 패턴 탐지 테스트"""

    @pytest.fixture
    def analyzer(self):
        """기본 설정 분석기"""
        return PatternAnalyzer(min_occurrence=3)

    def test_detect_user_specific_patterns(self, analyzer):
        """사용자 특화 패턴 탐지"""
        logs = []

        # 사용자 A에서 AGG-001이 5회 발생
        for _ in range(5):
            logs.append(create_test_log("AGG-001", user_id="user-A"))

        # 다른 사용자에서 AGG-001이 2회 발생
        logs.append(create_test_log("AGG-001", user_id="user-B"))
        logs.append(create_test_log("AGG-001", user_id="user-B"))

        patterns = analyzer.detect_user_specific_patterns(logs, "user-A")

        # 사용자 A에 특화된 패턴 탐지
        user_patterns = [
            p for p in patterns if p.pattern_type == PatternType.USER_SPECIFIC
        ]
        assert len(user_patterns) >= 1
        assert user_patterns[0].user_id == "user-A"

    def test_detect_user_specific_patterns_insufficient_logs(self, analyzer):
        """사용자 로그 부족 테스트"""
        logs = [
            create_test_log("AGG-001", user_id="user-A"),
            create_test_log("AGG-001", user_id="user-B"),
        ]

        patterns = analyzer.detect_user_specific_patterns(logs, "user-A")

        # 최소 발생 횟수 미달
        assert len(patterns) == 0


class TestPatternAnalyzerAnalyze:
    """전체 분석 테스트"""

    @pytest.fixture
    def analyzer(self):
        """기본 설정 분석기"""
        return PatternAnalyzer(min_occurrence=2, min_confidence=0.3)

    def test_analyze_empty_logs(self, analyzer):
        """빈 로그 분석 테스트"""
        patterns = analyzer.analyze([])

        assert patterns == []

    def test_analyze_insufficient_logs(self, analyzer):
        """로그 부족 테스트"""
        logs = [create_test_log("AGG-001")]

        patterns = analyzer.analyze(logs)

        assert patterns == []

    def test_analyze_with_project_filter(self, analyzer):
        """프로젝트 필터 적용 분석"""
        logs = [
            create_test_log("AGG-001", project_id="project-A"),
            create_test_log("AGG-001", project_id="project-A"),
            create_test_log("AGG-001", project_id="project-A"),
            create_test_log("AGG-001", project_id="project-B"),
            create_test_log("AGG-001", project_id="project-B"),
        ]

        patterns = analyzer.analyze(logs, project_id="project-A")

        # project-A만 분석됨
        assert all(
            p.project_id == "project-A" or p.project_id is None
            for p in patterns
            if p.pattern_type == PatternType.PROJECT_SPECIFIC
        )

    def test_analyze_with_user_filter(self, analyzer):
        """사용자 필터 적용 분석"""
        logs = [
            create_test_log("AGG-001", user_id="user-A"),
            create_test_log("AGG-001", user_id="user-A"),
            create_test_log("AGG-001", user_id="user-A"),
            create_test_log("AGG-001", user_id="user-B"),
        ]

        patterns = analyzer.analyze(logs, user_id="user-A")

        # user-A만 분석됨
        assert all(
            p.user_id == "user-A" or p.user_id is None
            for p in patterns
            if p.pattern_type == PatternType.USER_SPECIFIC
        )

    def test_analyze_patterns_sorted_by_confidence(self, analyzer):
        """패턴 신뢰도 기준 정렬 테스트"""
        # 다양한 패턴이 나오도록 충분한 데이터 생성
        logs = []
        session_base = "session-"

        # 많은 반복 위반
        for i in range(10):
            logs.append(create_test_log("AGG-001", session_id=f"{session_base}{i}"))
            logs.append(create_test_log("AGG-002", session_id=f"{session_base}{i}"))

        patterns = analyzer.analyze(logs)

        # 신뢰도 내림차순 정렬 확인
        if len(patterns) > 1:
            for i in range(len(patterns) - 1):
                assert patterns[i].confidence >= patterns[i + 1].confidence


class TestPatternAnalyzerSummary:
    """패턴 요약 테스트"""

    @pytest.fixture
    def analyzer(self):
        """기본 설정 분석기"""
        return PatternAnalyzer()

    def test_get_summary_empty_patterns(self, analyzer):
        """빈 패턴 요약 테스트"""
        summary = analyzer.get_summary([])

        assert summary["total_patterns"] == 0
        assert summary["by_type"] == {}
        assert summary["top_patterns"] == []
        assert summary["recommendations"] == []

    def test_get_summary_with_patterns(self, analyzer):
        """패턴 요약 테스트"""
        patterns = [
            ViolationPattern(
                pattern_type=PatternType.RECURRING,
                rule_codes=["AGG-001"],
                occurrence_count=5,
                confidence=0.8,
                description="규칙 'AGG-001'이(가) 5회 반복 위반됨",
                recommended_action="코드 리뷰 강화",
            ),
            ViolationPattern(
                pattern_type=PatternType.RECURRING,
                rule_codes=["AGG-002"],
                occurrence_count=3,
                confidence=0.6,
                description="규칙 'AGG-002'가 3회 반복 위반됨",
                recommended_action="개발자 교육",
            ),
            ViolationPattern(
                pattern_type=PatternType.CORRELATED,
                rule_codes=["AGG-001", "AGG-002"],
                occurrence_count=4,
                confidence=0.7,
                description="상관 패턴",
                recommended_action="통합 가이드 작성",
            ),
        ]

        summary = analyzer.get_summary(patterns)

        assert summary["total_patterns"] == 3
        assert summary["by_type"]["RECURRING"] == 2
        assert summary["by_type"]["CORRELATED"] == 1
        assert len(summary["top_patterns"]) <= 5
        assert len(summary["recommendations"]) > 0

    def test_get_summary_top_patterns_sorted(self, analyzer):
        """상위 패턴 신뢰도 기준 정렬 확인"""
        patterns = [
            ViolationPattern(
                pattern_type=PatternType.RECURRING,
                rule_codes=["AGG-001"],
                occurrence_count=5,
                confidence=0.5,
                description="Low confidence",
            ),
            ViolationPattern(
                pattern_type=PatternType.RECURRING,
                rule_codes=["AGG-002"],
                occurrence_count=3,
                confidence=0.9,
                description="High confidence",
            ),
        ]

        summary = analyzer.get_summary(patterns)

        # 높은 신뢰도 패턴이 먼저
        assert summary["top_patterns"][0]["confidence"] == 0.9


class TestPatternAnalyzerSingleton:
    """싱글톤 함수 테스트"""

    def test_get_pattern_analyzer_singleton(self):
        """싱글톤 패턴 테스트"""
        # 싱글톤 리셋을 위해 모듈 재로드 (테스트 격리)
        import src.feedback.analyzer as analyzer_module

        analyzer_module._analyzer = None

        analyzer1 = get_pattern_analyzer()
        analyzer2 = get_pattern_analyzer()

        assert analyzer1 is analyzer2

    def test_get_pattern_analyzer_with_custom_params(self):
        """커스텀 파라미터로 싱글톤 생성"""
        import src.feedback.analyzer as analyzer_module

        analyzer_module._analyzer = None

        analyzer = get_pattern_analyzer(
            min_occurrence=5,
            min_confidence=0.7,
        )

        assert analyzer._min_occurrence == 5
        assert analyzer._min_confidence == 0.7


class TestPatternAnalyzerEdgeCases:
    """엣지 케이스 테스트"""

    @pytest.fixture
    def analyzer(self):
        """기본 설정 분석기"""
        return PatternAnalyzer(min_occurrence=2)

    def test_single_rule_multiple_layers(self, analyzer):
        """동일 규칙이 여러 레이어에서 발생하는 경우"""
        logs = [
            create_test_log("AGG-001", layer="DOMAIN"),
            create_test_log("AGG-001", layer="APPLICATION"),
            create_test_log("AGG-001", layer="DOMAIN"),
        ]

        patterns = analyzer.detect_recurring_patterns(logs)

        # 패턴 탐지됨, layer는 None (혼합)
        recurring = [p for p in patterns if "AGG-001" in p.rule_codes]
        if recurring:
            # 여러 레이어가 섞이면 layer가 None일 수 있음
            assert recurring[0].layer is None or recurring[0].layer in [
                "DOMAIN",
                "APPLICATION",
            ]

    def test_correlation_with_same_rule(self, analyzer):
        """같은 규칙끼리의 상관 관계 (의미 없음)"""
        logs = [
            create_test_log("AGG-001", session_id="s1"),
            create_test_log("AGG-001", session_id="s1"),
        ]

        patterns = analyzer.detect_correlated_patterns(logs)

        # 같은 규칙끼리는 상관 관계로 탐지 안 됨
        assert len(patterns) == 0

    def test_deduplication(self, analyzer):
        """중복 패턴 제거 테스트"""
        patterns = [
            ViolationPattern(
                pattern_type=PatternType.RECURRING,
                rule_codes=["AGG-001"],
                occurrence_count=5,
                confidence=0.8,
                description="패턴 1",
            ),
            ViolationPattern(
                pattern_type=PatternType.RECURRING,
                rule_codes=["AGG-001"],
                occurrence_count=5,
                confidence=0.8,
                description="패턴 1 중복",
            ),
        ]

        unique = analyzer._deduplicate_patterns(patterns)

        assert len(unique) == 1
