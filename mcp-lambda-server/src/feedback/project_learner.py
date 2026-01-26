"""
Project Rule Learner
AESA-129 Task 5.4: 프로젝트별 커스텀 규칙 학습

프로젝트별 위반 패턴을 학습하고 맞춤형 규칙 설정을 관리합니다.
"""

import logging
from datetime import datetime
from typing import Optional
from collections import defaultdict
from pydantic import BaseModel, Field

from .models import ViolationLog, ViolationPattern, PatternType
from .analyzer import PatternAnalyzer
from .weight_adjuster import RuleWeightAdjuster
from .storage import ViolationStorage

logger = logging.getLogger(__name__)


class ProjectRuleConfig(BaseModel):
    """프로젝트별 규칙 설정"""

    project_id: str = Field(..., description="프로젝트 ID")
    project_name: Optional[str] = Field(None, description="프로젝트 이름")

    # 규칙별 가중치 오버라이드
    rule_weights: dict[str, float] = Field(
        default_factory=dict,
        description="규칙별 커스텀 가중치 {rule_code: weight}",
    )

    # 비활성화된 규칙
    disabled_rules: set[str] = Field(
        default_factory=set,
        description="비활성화된 규칙 코드 목록",
    )

    # 학습된 패턴
    learned_patterns: list[ViolationPattern] = Field(
        default_factory=list,
        description="학습된 프로젝트 특화 패턴",
    )

    # 메타데이터
    total_violations: int = Field(0, description="총 위반 수")
    learning_sessions: int = Field(0, description="학습 세션 수")
    last_learned: Optional[datetime] = Field(None, description="마지막 학습 시각")
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Config:
        arbitrary_types_allowed = True


class LearningResult(BaseModel):
    """학습 결과"""

    project_id: str
    patterns_found: int = 0
    rules_adjusted: int = 0
    weight_changes: dict[str, dict] = Field(
        default_factory=dict,
        description="{rule_code: {old: float, new: float, reason: str}}",
    )
    recommendations: list[str] = Field(default_factory=list)
    confidence: float = Field(0.0, ge=0.0, le=1.0)


class ProjectRuleLearner:
    """프로젝트별 커스텀 규칙 학습기

    위반 패턴을 분석하여 프로젝트별 맞춤형 규칙 설정을 학습합니다.
    """

    def __init__(
        self,
        analyzer: Optional[PatternAnalyzer] = None,
        weight_adjuster: Optional[RuleWeightAdjuster] = None,
        storage: Optional[ViolationStorage] = None,
        min_violations_for_learning: int = 10,
        auto_apply_threshold: float = 0.7,
    ):
        """초기화

        Args:
            analyzer: 패턴 분석기
            weight_adjuster: 가중치 조정기
            storage: 위반 저장소
            min_violations_for_learning: 학습에 필요한 최소 위반 수
            auto_apply_threshold: 자동 적용 신뢰도 임계값
        """
        self._analyzer = analyzer
        self._weight_adjuster = weight_adjuster
        self._storage = storage
        self._min_violations = min_violations_for_learning
        self._auto_apply_threshold = auto_apply_threshold

        # 프로젝트별 설정 캐시
        self._project_configs: dict[str, ProjectRuleConfig] = {}

    # ==================== 학습 ====================

    async def learn_from_logs(
        self,
        logs: list[ViolationLog],
        project_id: str,
        auto_apply: bool = False,
    ) -> LearningResult:
        """위반 로그로부터 프로젝트별 규칙 학습

        Args:
            logs: 전체 위반 로그 (프로젝트 비교를 위해 전체 필요)
            project_id: 학습 대상 프로젝트 ID
            auto_apply: 학습 결과 자동 적용 여부

        Returns:
            학습 결과
        """
        result = LearningResult(project_id=project_id)

        # 프로젝트 로그 필터링
        project_logs = [
            log for log in logs if log.context and log.context.project_id == project_id
        ]

        if len(project_logs) < self._min_violations:
            result.recommendations.append(
                f"학습에 필요한 최소 위반 수({self._min_violations})에 미달: "
                f"{len(project_logs)}개"
            )
            return result

        # 1. 패턴 분석
        patterns = await self._analyze_patterns(logs, project_id)
        result.patterns_found = len(patterns)

        if not patterns:
            result.recommendations.append("프로젝트 특화 패턴이 발견되지 않음")
            return result

        # 2. 가중치 변경 계산
        weight_changes = self._calculate_weight_changes(patterns, project_logs)
        result.weight_changes = weight_changes
        result.rules_adjusted = len(weight_changes)

        # 3. 신뢰도 계산
        result.confidence = self._calculate_learning_confidence(
            patterns, project_logs, logs
        )

        # 4. 추천 사항 생성
        result.recommendations = self._generate_recommendations(
            patterns, weight_changes
        )

        # 5. 자동 적용 (조건 충족 시)
        if auto_apply and result.confidence >= self._auto_apply_threshold:
            await self._apply_learning(project_id, weight_changes, patterns)
            logger.info(
                "Auto-applied learning for project %s: %d rules adjusted",
                project_id,
                result.rules_adjusted,
            )

        # 6. 프로젝트 설정 업데이트
        self._update_project_config(project_id, patterns, project_logs)

        return result

    async def _analyze_patterns(
        self,
        logs: list[ViolationLog],
        project_id: str,
    ) -> list[ViolationPattern]:
        """패턴 분석"""
        if not self._analyzer:
            from .analyzer import get_pattern_analyzer

            self._analyzer = get_pattern_analyzer()

        return self._analyzer.detect_project_specific_patterns(logs, project_id)

    def _calculate_weight_changes(
        self,
        patterns: list[ViolationPattern],
        project_logs: list[ViolationLog],
    ) -> dict[str, dict]:
        """가중치 변경 계산

        패턴 분석 결과를 바탕으로 규칙별 가중치 변경을 계산합니다.
        """
        changes: dict[str, dict] = {}

        # 규칙별 통계
        rule_stats = self._aggregate_rule_stats(project_logs)

        for pattern in patterns:
            if pattern.pattern_type != PatternType.PROJECT_SPECIFIC:
                continue

            for rule_code in pattern.rule_codes:
                stats = rule_stats.get(rule_code, {})
                auto_fix_rate = stats.get("auto_fix_rate", 0.0)

                # 현재 가중치
                current_weight = 1.0
                if self._weight_adjuster:
                    current_weight = self._weight_adjuster.get_weight(rule_code)

                # 새 가중치 계산
                # - 빈도 높음 → 가중치 증가
                # - 자동 수정률 높음 → 가중치 감소
                # - 신뢰도 높음 → 변화 폭 증가
                frequency_factor = 1 + (pattern.confidence * 0.5)  # 최대 1.5x
                auto_fix_factor = 1 - (auto_fix_rate * 0.3)  # 최대 0.7x

                new_weight = current_weight * frequency_factor * auto_fix_factor
                new_weight = max(0.1, min(3.0, new_weight))  # 범위 제한

                if abs(new_weight - current_weight) > 0.05:  # 유의미한 변화만
                    changes[rule_code] = {
                        "old": round(current_weight, 3),
                        "new": round(new_weight, 3),
                        "reason": f"Project-specific: concentration={pattern.confidence:.2f}, "
                        f"auto_fix_rate={auto_fix_rate:.2f}",
                    }

        return changes

    def _aggregate_rule_stats(
        self,
        logs: list[ViolationLog],
    ) -> dict[str, dict]:
        """규칙별 통계 집계"""
        stats: dict[str, dict] = defaultdict(lambda: {"count": 0, "auto_fixed": 0})

        for log in logs:
            stats[log.rule_code]["count"] += 1
            if log.was_auto_fixed:
                stats[log.rule_code]["auto_fixed"] += 1

        # 자동 수정률 계산
        result = {}
        for rule_code, data in stats.items():
            count = data["count"]
            auto_fix_rate = data["auto_fixed"] / count if count > 0 else 0.0
            result[rule_code] = {
                "count": count,
                "auto_fix_rate": auto_fix_rate,
            }

        return result

    def _calculate_learning_confidence(
        self,
        patterns: list[ViolationPattern],
        project_logs: list[ViolationLog],
        all_logs: list[ViolationLog],
    ) -> float:
        """학습 신뢰도 계산"""
        if not patterns:
            return 0.0

        # 요소별 신뢰도
        factors = []

        # 1. 데이터 충분성 (로그 수 기반)
        data_sufficiency = min(1.0, len(project_logs) / 100)
        factors.append(data_sufficiency * 0.3)

        # 2. 패턴 신뢰도 평균
        pattern_confidence = sum(p.confidence for p in patterns) / len(patterns)
        factors.append(pattern_confidence * 0.4)

        # 3. 프로젝트 특이성 (전체 대비 비율)
        project_ratio = len(project_logs) / len(all_logs) if all_logs else 0
        # 너무 많은 비율을 차지하면 오히려 일반적인 패턴
        specificity = 1 - abs(project_ratio - 0.3) / 0.7  # 30% 근처가 최적
        specificity = max(0, specificity)
        factors.append(specificity * 0.3)

        return round(sum(factors), 3)

    def _generate_recommendations(
        self,
        patterns: list[ViolationPattern],
        weight_changes: dict[str, dict],
    ) -> list[str]:
        """추천 사항 생성"""
        recommendations = []

        # 높은 집중도 패턴
        high_concentration = [p for p in patterns if p.confidence >= 0.7]
        if high_concentration:
            rules = [p.rule_codes[0] for p in high_concentration]
            recommendations.append(
                f"높은 집중도 규칙 ({len(rules)}개): {', '.join(rules[:3])}"
                f"{' 외' if len(rules) > 3 else ''} - 프로젝트 특화 검토 권장"
            )

        # 가중치 증가 규칙
        increased = [
            (code, data)
            for code, data in weight_changes.items()
            if data["new"] > data["old"]
        ]
        if increased:
            recommendations.append(
                f"가중치 증가 규칙 ({len(increased)}개): "
                f"자주 위반되는 규칙에 더 높은 우선순위 부여"
            )

        # 가중치 감소 규칙 (자동 수정률 높음)
        decreased = [
            (code, data)
            for code, data in weight_changes.items()
            if data["new"] < data["old"]
        ]
        if decreased:
            recommendations.append(
                f"가중치 감소 규칙 ({len(decreased)}개): "
                f"자동 수정률이 높아 우선순위 하향 가능"
            )

        return recommendations

    # ==================== 적용 ====================

    async def _apply_learning(
        self,
        project_id: str,
        weight_changes: dict[str, dict],
        patterns: list[ViolationPattern],
    ) -> None:
        """학습 결과 적용"""
        if not self._weight_adjuster:
            from .weight_adjuster import get_weight_adjuster

            self._weight_adjuster = get_weight_adjuster()

        for rule_code, change in weight_changes.items():
            self._weight_adjuster.set_project_override(
                rule_code=rule_code,
                project_id=project_id,
                weight=change["new"],
            )

        logger.info(
            "Applied %d weight changes for project %s", len(weight_changes), project_id
        )

    async def apply_learning_result(
        self,
        result: LearningResult,
    ) -> bool:
        """학습 결과 수동 적용

        Args:
            result: 학습 결과

        Returns:
            적용 성공 여부
        """
        if not result.weight_changes:
            return False

        config = self.get_project_config(result.project_id)

        await self._apply_learning(
            result.project_id,
            result.weight_changes,
            config.learned_patterns if config else [],
        )

        return True

    # ==================== 설정 관리 ====================

    def _update_project_config(
        self,
        project_id: str,
        patterns: list[ViolationPattern],
        project_logs: list[ViolationLog],
    ) -> None:
        """프로젝트 설정 업데이트"""
        if project_id not in self._project_configs:
            self._project_configs[project_id] = ProjectRuleConfig(project_id=project_id)

        config = self._project_configs[project_id]
        config.learned_patterns = patterns
        config.total_violations = len(project_logs)
        config.learning_sessions += 1
        config.last_learned = datetime.utcnow()
        config.updated_at = datetime.utcnow()

        # 프로젝트 이름 추출 (로그에서)
        if project_logs and not config.project_name:
            for log in project_logs:
                if log.context and log.context.project_name:
                    config.project_name = log.context.project_name
                    break

    def get_project_config(self, project_id: str) -> Optional[ProjectRuleConfig]:
        """프로젝트 설정 조회"""
        return self._project_configs.get(project_id)

    def get_all_project_configs(self) -> dict[str, ProjectRuleConfig]:
        """모든 프로젝트 설정 반환"""
        return self._project_configs.copy()

    def set_rule_disabled(
        self,
        project_id: str,
        rule_code: str,
        disabled: bool = True,
    ) -> None:
        """규칙 비활성화 설정

        Args:
            project_id: 프로젝트 ID
            rule_code: 규칙 코드
            disabled: 비활성화 여부
        """
        if project_id not in self._project_configs:
            self._project_configs[project_id] = ProjectRuleConfig(project_id=project_id)

        config = self._project_configs[project_id]
        if disabled:
            config.disabled_rules.add(rule_code)
        else:
            config.disabled_rules.discard(rule_code)
        config.updated_at = datetime.utcnow()

    def is_rule_disabled(
        self,
        project_id: str,
        rule_code: str,
    ) -> bool:
        """규칙 비활성화 여부 확인"""
        config = self._project_configs.get(project_id)
        if not config:
            return False
        return rule_code in config.disabled_rules

    def get_effective_weight(
        self,
        rule_code: str,
        project_id: Optional[str] = None,
    ) -> float:
        """유효 가중치 조회 (비활성화 고려)

        Args:
            rule_code: 규칙 코드
            project_id: 프로젝트 ID

        Returns:
            유효 가중치 (비활성화 시 0.0)
        """
        # 비활성화된 규칙
        if project_id and self.is_rule_disabled(project_id, rule_code):
            return 0.0

        # 가중치 조정기 사용
        if self._weight_adjuster:
            return self._weight_adjuster.get_weight(rule_code, project_id)

        return 1.0

    # ==================== 리셋 ====================

    def reset_project_learning(self, project_id: str) -> bool:
        """프로젝트 학습 리셋

        Args:
            project_id: 프로젝트 ID

        Returns:
            리셋 성공 여부
        """
        if project_id not in self._project_configs:
            return False

        # 가중치 오버라이드 제거
        if self._weight_adjuster:
            overrides = self._weight_adjuster.get_project_overrides(project_id)
            for rule_code in overrides:
                self._weight_adjuster.remove_project_override(rule_code, project_id)

        # 설정 제거
        del self._project_configs[project_id]

        logger.info("Reset learning for project %s", project_id)
        return True

    def reset_all(self) -> None:
        """모든 학습 리셋"""
        project_ids = list(self._project_configs.keys())
        for project_id in project_ids:
            self.reset_project_learning(project_id)

        logger.info("Reset all project learning")

    # ==================== 통계 ====================

    def get_learning_summary(self) -> dict:
        """학습 요약 정보"""
        if not self._project_configs:
            return {
                "total_projects": 0,
                "total_patterns": 0,
                "total_violations_analyzed": 0,
                "projects": [],
            }

        projects_summary = []
        total_patterns = 0
        total_violations = 0

        for project_id, config in self._project_configs.items():
            total_patterns += len(config.learned_patterns)
            total_violations += config.total_violations

            projects_summary.append(
                {
                    "project_id": project_id,
                    "project_name": config.project_name,
                    "patterns_count": len(config.learned_patterns),
                    "violations_count": config.total_violations,
                    "disabled_rules_count": len(config.disabled_rules),
                    "learning_sessions": config.learning_sessions,
                    "last_learned": config.last_learned.isoformat()
                    if config.last_learned
                    else None,
                }
            )

        return {
            "total_projects": len(self._project_configs),
            "total_patterns": total_patterns,
            "total_violations_analyzed": total_violations,
            "projects": projects_summary,
        }


# ==================== 싱글톤 편의 함수 ====================

_project_learner: Optional[ProjectRuleLearner] = None


def get_project_learner(
    analyzer: Optional[PatternAnalyzer] = None,
    weight_adjuster: Optional[RuleWeightAdjuster] = None,
    storage: Optional[ViolationStorage] = None,
) -> ProjectRuleLearner:
    """싱글톤 ProjectRuleLearner 반환"""
    global _project_learner

    if _project_learner is None:
        _project_learner = ProjectRuleLearner(
            analyzer=analyzer,
            weight_adjuster=weight_adjuster,
            storage=storage,
        )

    return _project_learner


def reset_project_learner() -> None:
    """싱글톤 인스턴스 리셋 (테스트용)"""
    global _project_learner
    _project_learner = None
