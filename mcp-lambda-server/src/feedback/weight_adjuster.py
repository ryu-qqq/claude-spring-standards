"""
Rule Weight Adjuster
AESA-129 Task 5.3: 위반 빈도 기반 규칙 가중치 동적 조정

위반 로그 분석 결과를 바탕으로 규칙의 가중치를 동적으로 조정합니다.
"""

import math
import logging
from datetime import datetime
from typing import Optional
from collections import defaultdict

from .models import RuleWeight, ViolationLog, ViolationPattern, PatternType
from .storage import ViolationStorage

logger = logging.getLogger(__name__)


class WeightCalculationConfig:
    """가중치 계산 설정"""

    # 기본 가중치 범위
    MIN_WEIGHT: float = 0.1
    MAX_WEIGHT: float = 3.0
    DEFAULT_WEIGHT: float = 1.0

    # 빈도 기반 조정 계수
    FREQUENCY_MULTIPLIER: float = 0.1  # 빈도당 가중치 증가
    FREQUENCY_CAP: int = 50  # 최대 고려 빈도

    # 자동 수정 비율 영향
    AUTO_FIX_DISCOUNT: float = 0.3  # 자동 수정 가능 시 가중치 감소

    # 시간 감쇠
    TIME_DECAY_HALF_LIFE_DAYS: int = 30  # 30일마다 영향력 반감

    # 패턴 가중치 부스트
    RECURRING_PATTERN_BOOST: float = 0.2
    CORRELATED_PATTERN_BOOST: float = 0.1

    # 프로젝트 특화
    PROJECT_SPECIFIC_ADJUSTMENT: float = 0.15


class RuleWeightAdjuster:
    """규칙 가중치 동적 조정기

    위반 빈도, 자동 수정 비율, 시간 감쇠, 패턴 분석을 종합하여
    규칙의 가중치를 동적으로 조정합니다.
    """

    def __init__(
        self,
        storage: Optional[ViolationStorage] = None,
        config: Optional[WeightCalculationConfig] = None,
    ):
        """초기화

        Args:
            storage: 위반 저장소 (통계 조회용)
            config: 가중치 계산 설정
        """
        self._storage = storage
        self._config = config or WeightCalculationConfig()
        self._rule_weights: dict[str, RuleWeight] = {}
        self._last_update: Optional[datetime] = None

    # ==================== 가중치 계산 ====================

    def calculate_weight(
        self,
        rule_code: str,
        violation_count: int,
        auto_fix_rate: float = 0.0,
        last_violation_time: Optional[datetime] = None,
        patterns: Optional[list[ViolationPattern]] = None,
    ) -> float:
        """규칙 가중치 계산

        Args:
            rule_code: 규칙 코드
            violation_count: 위반 횟수
            auto_fix_rate: 자동 수정 비율 (0.0 ~ 1.0)
            last_violation_time: 마지막 위반 시각
            patterns: 관련 패턴 목록

        Returns:
            계산된 가중치 (0.1 ~ 3.0)
        """
        config = self._config

        # 1. 기본 가중치
        weight = config.DEFAULT_WEIGHT

        # 2. 빈도 기반 조정 (로그 스케일)
        capped_count = min(violation_count, config.FREQUENCY_CAP)
        if capped_count > 0:
            frequency_adjustment = config.FREQUENCY_MULTIPLIER * math.log1p(
                capped_count
            )
            weight += frequency_adjustment

        # 3. 자동 수정 비율 영향 (수정 가능 = 덜 중요)
        if auto_fix_rate > 0:
            auto_fix_adjustment = -config.AUTO_FIX_DISCOUNT * auto_fix_rate
            weight += auto_fix_adjustment

        # 4. 시간 감쇠 (오래된 위반은 영향력 감소)
        if last_violation_time:
            time_decay = self._calculate_time_decay(last_violation_time)
            # 최근 위반일수록 가중치 증가
            recency_boost = (1 - time_decay) * 0.2
            weight += recency_boost

        # 5. 패턴 기반 조정
        if patterns:
            pattern_adjustment = self._calculate_pattern_adjustment(rule_code, patterns)
            weight += pattern_adjustment

        # 6. 범위 제한
        weight = max(config.MIN_WEIGHT, min(config.MAX_WEIGHT, weight))

        return round(weight, 3)

    def _calculate_time_decay(self, timestamp: datetime) -> float:
        """시간 감쇠 계수 계산

        Args:
            timestamp: 이벤트 시각

        Returns:
            감쇠 계수 (0.0 ~ 1.0, 0 = 최근, 1 = 오래됨)
        """
        now = datetime.utcnow()
        age_days = (now - timestamp).total_seconds() / 86400
        half_life = self._config.TIME_DECAY_HALF_LIFE_DAYS

        # 지수 감쇠: decay = 1 - 2^(-age/half_life)
        decay = 1 - math.pow(2, -age_days / half_life)
        return min(1.0, max(0.0, decay))

    def _calculate_pattern_adjustment(
        self,
        rule_code: str,
        patterns: list[ViolationPattern],
    ) -> float:
        """패턴 기반 가중치 조정 계산

        Args:
            rule_code: 규칙 코드
            patterns: 관련 패턴 목록

        Returns:
            패턴 기반 조정값
        """
        adjustment = 0.0
        config = self._config

        for pattern in patterns:
            if rule_code not in pattern.rule_codes:
                continue

            # 패턴 유형별 가중치 부스트
            if pattern.pattern_type == PatternType.RECURRING:
                adjustment += config.RECURRING_PATTERN_BOOST * pattern.confidence
            elif pattern.pattern_type == PatternType.CORRELATED:
                adjustment += config.CORRELATED_PATTERN_BOOST * pattern.confidence
            elif pattern.pattern_type == PatternType.PROJECT_SPECIFIC:
                adjustment += config.PROJECT_SPECIFIC_ADJUSTMENT * pattern.confidence

        return adjustment

    # ==================== 가중치 업데이트 ====================

    async def update_weights_from_logs(
        self,
        logs: list[ViolationLog],
        patterns: Optional[list[ViolationPattern]] = None,
    ) -> dict[str, RuleWeight]:
        """위반 로그로부터 가중치 업데이트

        Args:
            logs: 위반 로그 목록
            patterns: 분석된 패턴 목록

        Returns:
            업데이트된 규칙 가중치 딕셔너리
        """
        if not logs:
            return self._rule_weights

        # 규칙별 통계 집계
        rule_stats = self._aggregate_rule_stats(logs)

        # 각 규칙의 가중치 계산
        for rule_code, stats in rule_stats.items():
            weight = self.calculate_weight(
                rule_code=rule_code,
                violation_count=stats["count"],
                auto_fix_rate=stats["auto_fix_rate"],
                last_violation_time=stats["last_violation"],
                patterns=patterns,
            )

            # RuleWeight 객체 생성/업데이트
            if rule_code in self._rule_weights:
                rule_weight = self._rule_weights[rule_code]
                rule_weight.adjusted_weight = weight
                rule_weight.violation_count = stats["count"]
                rule_weight.auto_fix_rate = stats["auto_fix_rate"]
                rule_weight.last_updated = datetime.utcnow()
            else:
                rule_weight = RuleWeight(
                    rule_code=rule_code,
                    adjusted_weight=weight,
                    violation_count=stats["count"],
                    auto_fix_rate=stats["auto_fix_rate"],
                    adjustment_reason=f"Auto-adjusted: count={stats['count']}, "
                    f"auto_fix_rate={stats['auto_fix_rate']:.2f}",
                )
                self._rule_weights[rule_code] = rule_weight

            logger.debug(
                "Updated weight for %s: %.3f (count=%d, auto_fix=%.2f)",
                rule_code,
                weight,
                stats["count"],
                stats["auto_fix_rate"],
            )

        self._last_update = datetime.utcnow()
        return self._rule_weights

    def _aggregate_rule_stats(self, logs: list[ViolationLog]) -> dict[str, dict]:
        """규칙별 통계 집계

        Args:
            logs: 위반 로그 목록

        Returns:
            규칙별 통계 딕셔너리
        """
        stats: dict[str, dict] = defaultdict(
            lambda: {
                "count": 0,
                "auto_fixed": 0,
                "last_violation": None,
            }
        )

        for log in logs:
            rule_code = log.rule_code
            stats[rule_code]["count"] += 1

            if log.was_auto_fixed:
                stats[rule_code]["auto_fixed"] += 1

            # 가장 최근 위반 시각 추적
            if (
                stats[rule_code]["last_violation"] is None
                or log.timestamp > stats[rule_code]["last_violation"]
            ):
                stats[rule_code]["last_violation"] = log.timestamp

        # 자동 수정 비율 계산
        result = {}
        for rule_code, data in stats.items():
            count = data["count"]
            auto_fix_rate = data["auto_fixed"] / count if count > 0 else 0.0
            result[rule_code] = {
                "count": count,
                "auto_fix_rate": auto_fix_rate,
                "last_violation": data["last_violation"],
            }

        return result

    # ==================== 가중치 조회 ====================

    def get_weight(self, rule_code: str, project_id: Optional[str] = None) -> float:
        """규칙 가중치 조회

        Args:
            rule_code: 규칙 코드
            project_id: 프로젝트 ID (프로젝트별 오버라이드 적용)

        Returns:
            가중치 값 (기본값: 1.0)
        """
        if rule_code not in self._rule_weights:
            return self._config.DEFAULT_WEIGHT

        rule_weight = self._rule_weights[rule_code]
        return rule_weight.get_weight_for_project(project_id)

    def get_all_weights(self) -> dict[str, RuleWeight]:
        """모든 규칙 가중치 반환"""
        return self._rule_weights.copy()

    def get_weight_summary(self) -> dict:
        """가중치 요약 정보"""
        if not self._rule_weights:
            return {
                "total_rules": 0,
                "average_weight": self._config.DEFAULT_WEIGHT,
                "weight_distribution": {},
                "last_update": None,
            }

        weights = [rw.adjusted_weight for rw in self._rule_weights.values()]

        # 가중치 분포 계산
        distribution = {
            "low": sum(1 for w in weights if w < 0.8),
            "normal": sum(1 for w in weights if 0.8 <= w < 1.2),
            "high": sum(1 for w in weights if 1.2 <= w < 2.0),
            "critical": sum(1 for w in weights if w >= 2.0),
        }

        return {
            "total_rules": len(self._rule_weights),
            "average_weight": round(sum(weights) / len(weights), 3),
            "min_weight": round(min(weights), 3),
            "max_weight": round(max(weights), 3),
            "weight_distribution": distribution,
            "last_update": self._last_update.isoformat() if self._last_update else None,
        }

    # ==================== 프로젝트별 오버라이드 ====================

    def set_project_override(
        self,
        rule_code: str,
        project_id: str,
        weight: float,
    ) -> None:
        """프로젝트별 가중치 오버라이드 설정

        Args:
            rule_code: 규칙 코드
            project_id: 프로젝트 ID
            weight: 오버라이드 가중치
        """
        config = self._config
        weight = max(config.MIN_WEIGHT, min(config.MAX_WEIGHT, weight))

        if rule_code not in self._rule_weights:
            self._rule_weights[rule_code] = RuleWeight(rule_code=rule_code)

        self._rule_weights[rule_code].project_overrides[project_id] = weight
        self._rule_weights[rule_code].last_updated = datetime.utcnow()

        logger.info(
            "Set project override: rule=%s, project=%s, weight=%.3f",
            rule_code,
            project_id,
            weight,
        )

    def remove_project_override(
        self,
        rule_code: str,
        project_id: str,
    ) -> bool:
        """프로젝트별 오버라이드 제거

        Args:
            rule_code: 규칙 코드
            project_id: 프로젝트 ID

        Returns:
            제거 성공 여부
        """
        if rule_code not in self._rule_weights:
            return False

        rule_weight = self._rule_weights[rule_code]
        if project_id in rule_weight.project_overrides:
            del rule_weight.project_overrides[project_id]
            rule_weight.last_updated = datetime.utcnow()
            return True

        return False

    def get_project_overrides(self, project_id: str) -> dict[str, float]:
        """프로젝트의 모든 오버라이드 조회

        Args:
            project_id: 프로젝트 ID

        Returns:
            {rule_code: weight} 딕셔너리
        """
        overrides = {}
        for rule_code, rule_weight in self._rule_weights.items():
            if project_id in rule_weight.project_overrides:
                overrides[rule_code] = rule_weight.project_overrides[project_id]
        return overrides

    # ==================== 저장소 통합 ====================

    async def load_from_storage(self, days: int = 30) -> None:
        """저장소에서 가중치 데이터 로드

        Args:
            days: 분석할 기간 (일)
        """
        if not self._storage:
            logger.warning("No storage configured, cannot load weights")
            return

        try:
            # 저장소에서 통계 조회
            stats = await self._storage.get_violation_count_by_rule(days=days)

            for rule_code, count in stats.items():
                if rule_code not in self._rule_weights:
                    weight = self.calculate_weight(
                        rule_code=rule_code,
                        violation_count=count,
                    )
                    self._rule_weights[rule_code] = RuleWeight(
                        rule_code=rule_code,
                        adjusted_weight=weight,
                        violation_count=count,
                    )

            self._last_update = datetime.utcnow()
            logger.info("Loaded weights for %d rules from storage", len(stats))

        except Exception as e:
            logger.error("Failed to load weights from storage: %s", e)

    async def save_to_storage(self) -> None:
        """가중치 데이터를 저장소에 저장

        Note: 저장소에 별도 테이블이 필요할 수 있음
        """
        if not self._storage:
            logger.warning("No storage configured, cannot save weights")
            return

        # TODO: Task 5.4에서 구현 - 가중치 데이터 영구 저장
        logger.debug("Weight persistence not yet implemented")


# ==================== 싱글톤 편의 함수 ====================

_weight_adjuster: Optional[RuleWeightAdjuster] = None


def get_weight_adjuster(
    storage: Optional[ViolationStorage] = None,
    config: Optional[WeightCalculationConfig] = None,
) -> RuleWeightAdjuster:
    """싱글톤 RuleWeightAdjuster 반환

    Args:
        storage: ViolationStorage 인스턴스
        config: 계산 설정

    Returns:
        RuleWeightAdjuster 인스턴스
    """
    global _weight_adjuster

    if _weight_adjuster is None:
        _weight_adjuster = RuleWeightAdjuster(storage=storage, config=config)

    return _weight_adjuster


def reset_weight_adjuster() -> None:
    """싱글톤 인스턴스 리셋 (테스트용)"""
    global _weight_adjuster
    _weight_adjuster = None
