"""
Pattern Analyzer
AESA-129 Task 5.2: 반복 위반 탐지 및 패턴 분석

ViolationLog 데이터를 분석하여 패턴을 탐지하는 분석 엔진
"""

import logging
from collections import Counter, defaultdict
from itertools import combinations
from typing import Optional

from .models import ViolationLog, ViolationPattern, PatternType

logger = logging.getLogger(__name__)


class PatternAnalyzer:
    """위반 패턴 분석 엔진

    ViolationLog 데이터를 분석하여 다양한 패턴을 탐지합니다:
    - RECURRING: 동일 규칙이 반복적으로 위반됨
    - CORRELATED: 특정 규칙들이 함께 위반되는 경향
    - TIME_BASED: 특정 시간대에 위반이 집중됨
    - PROJECT_SPECIFIC: 특정 프로젝트에서 특화된 패턴
    - USER_SPECIFIC: 특정 사용자에 특화된 패턴
    """

    def __init__(
        self,
        min_occurrence: int = 3,
        min_confidence: float = 0.5,
        correlation_threshold: float = 0.3,
        time_window_hours: int = 24,
    ):
        """패턴 분석기 초기화

        Args:
            min_occurrence: 패턴으로 인정할 최소 발생 횟수
            min_confidence: 패턴으로 인정할 최소 신뢰도
            correlation_threshold: 상관 관계 판단 임계값 (co-occurrence ratio)
            time_window_hours: 시간 기반 분석 윈도우 (시간 단위)
        """
        self._min_occurrence = min_occurrence
        self._min_confidence = min_confidence
        self._correlation_threshold = correlation_threshold
        self._time_window_hours = time_window_hours

    def analyze(
        self,
        logs: list[ViolationLog],
        project_id: Optional[str] = None,
        user_id: Optional[str] = None,
    ) -> list[ViolationPattern]:
        """전체 패턴 분석 수행

        Args:
            logs: 분석할 위반 로그 목록
            project_id: 특정 프로젝트 필터 (선택)
            user_id: 특정 사용자 필터 (선택)

        Returns:
            발견된 패턴 목록
        """
        if not logs:
            return []

        # 필터 적용
        filtered_logs = self._filter_logs(logs, project_id, user_id)

        if len(filtered_logs) < self._min_occurrence:
            return []

        patterns: list[ViolationPattern] = []

        # 패턴 탐지 수행
        patterns.extend(self.detect_recurring_patterns(filtered_logs))
        patterns.extend(self.detect_correlated_patterns(filtered_logs))
        patterns.extend(self.detect_time_based_patterns(filtered_logs))

        # 프로젝트/사용자별 분석
        if project_id:
            patterns.extend(
                self.detect_project_specific_patterns(filtered_logs, project_id)
            )
        if user_id:
            patterns.extend(self.detect_user_specific_patterns(filtered_logs, user_id))

        # 중복 패턴 제거 및 정렬
        patterns = self._deduplicate_patterns(patterns)
        patterns.sort(key=lambda p: (p.confidence, p.occurrence_count), reverse=True)

        logger.info(
            "Analyzed %d logs, found %d patterns", len(filtered_logs), len(patterns)
        )
        return patterns

    def detect_recurring_patterns(
        self,
        logs: list[ViolationLog],
    ) -> list[ViolationPattern]:
        """반복 위반 패턴 탐지

        동일한 규칙이 최소 횟수 이상 발생하는 패턴을 찾습니다.

        Args:
            logs: 분석할 위반 로그 목록

        Returns:
            발견된 RECURRING 패턴 목록
        """
        patterns: list[ViolationPattern] = []

        # 규칙별 발생 횟수 집계
        rule_counts = Counter(log.rule_code for log in logs)
        total_logs = len(logs)

        for rule_code, count in rule_counts.items():
            if count < self._min_occurrence:
                continue

            # 신뢰도 = 전체 대비 발생 비율 (정규화)
            confidence = min(count / max(total_logs * 0.1, 1), 1.0)

            if confidence >= self._min_confidence:
                # 해당 규칙의 레이어 정보 추출
                layers = set(
                    log.layer
                    for log in logs
                    if log.rule_code == rule_code and log.layer
                )
                layer = layers.pop() if len(layers) == 1 else None

                patterns.append(
                    ViolationPattern(
                        pattern_type=PatternType.RECURRING,
                        rule_codes=[rule_code],
                        occurrence_count=count,
                        confidence=round(confidence, 3),
                        description=f"규칙 '{rule_code}'이(가) {count}회 반복 위반됨",
                        layer=layer,
                        recommended_action=self._get_recurring_recommendation(
                            rule_code, count
                        ),
                    )
                )

        return patterns

    def detect_correlated_patterns(
        self,
        logs: list[ViolationLog],
    ) -> list[ViolationPattern]:
        """상관 관계 패턴 탐지

        특정 규칙들이 함께 위반되는 경향을 분석합니다.
        같은 세션/파일에서 함께 발생하는 규칙 쌍을 찾습니다.

        Args:
            logs: 분석할 위반 로그 목록

        Returns:
            발견된 CORRELATED 패턴 목록
        """
        patterns: list[ViolationPattern] = []

        # 세션별 규칙 그룹화 (session_id가 없으면 file_path 사용)
        session_rules: dict[str, set[str]] = defaultdict(set)

        for log in logs:
            key = log.context.session_id or log.context.file_path or "default"
            session_rules[key].add(log.rule_code)

        # 규칙 쌍별 co-occurrence 계산
        pair_counts: Counter = Counter()
        rule_counts: Counter = Counter()

        for rules in session_rules.values():
            if len(rules) < 2:
                continue

            for rule in rules:
                rule_counts[rule] += 1

            for pair in combinations(sorted(rules), 2):
                pair_counts[pair] += 1

        # 상관 관계 분석
        for (rule_a, rule_b), co_count in pair_counts.items():
            if co_count < self._min_occurrence:
                continue

            # Jaccard 유사도 기반 상관도 계산
            union_count = rule_counts[rule_a] + rule_counts[rule_b] - co_count
            correlation = co_count / union_count if union_count > 0 else 0

            if correlation >= self._correlation_threshold:
                patterns.append(
                    ViolationPattern(
                        pattern_type=PatternType.CORRELATED,
                        rule_codes=[rule_a, rule_b],
                        occurrence_count=co_count,
                        confidence=round(correlation, 3),
                        description=f"규칙 '{rule_a}'와 '{rule_b}'가 함께 위반되는 경향 (상관도: {correlation:.1%})",
                        recommended_action=self._get_correlation_recommendation(
                            rule_a, rule_b
                        ),
                    )
                )

        return patterns

    def detect_time_based_patterns(
        self,
        logs: list[ViolationLog],
    ) -> list[ViolationPattern]:
        """시간 기반 패턴 탐지

        특정 시간대에 위반이 집중되는 패턴을 분석합니다.

        Args:
            logs: 분석할 위반 로그 목록

        Returns:
            발견된 TIME_BASED 패턴 목록
        """
        patterns: list[ViolationPattern] = []

        # 시간대별 위반 집계 (시간 단위)
        hour_counts: Counter = Counter()
        hour_rules: dict[int, set[str]] = defaultdict(set)

        for log in logs:
            hour = log.timestamp.hour
            hour_counts[hour] += 1
            hour_rules[hour].add(log.rule_code)

        total_logs = len(logs)
        avg_per_hour = total_logs / 24

        # 평균 대비 3배 이상 집중되는 시간대 탐지
        for hour, count in hour_counts.items():
            if count < self._min_occurrence:
                continue

            concentration = count / avg_per_hour if avg_per_hour > 0 else 0

            if concentration >= 3.0:  # 평균 대비 3배 이상
                confidence = min(concentration / 5.0, 1.0)  # 5배면 신뢰도 100%

                patterns.append(
                    ViolationPattern(
                        pattern_type=PatternType.TIME_BASED,
                        rule_codes=list(hour_rules[hour]),
                        occurrence_count=count,
                        confidence=round(confidence, 3),
                        description=f"{hour}시에 위반이 집중됨 (평균 대비 {concentration:.1f}배)",
                        recommended_action=f"{hour}시 작업 시 코드 리뷰 강화 권장",
                    )
                )

        return patterns

    def detect_project_specific_patterns(
        self,
        logs: list[ViolationLog],
        project_id: str,
    ) -> list[ViolationPattern]:
        """프로젝트 특화 패턴 탐지

        특정 프로젝트에서만 자주 발생하는 위반 패턴을 분석합니다.

        Args:
            logs: 분석할 위반 로그 목록
            project_id: 프로젝트 ID

        Returns:
            발견된 PROJECT_SPECIFIC 패턴 목록
        """
        patterns: list[ViolationPattern] = []

        # 프로젝트별 규칙 집계
        project_logs = [log for log in logs if log.context.project_id == project_id]

        if len(project_logs) < self._min_occurrence:
            return patterns

        rule_counts = Counter(log.rule_code for log in project_logs)
        total_project_logs = len(project_logs)

        for rule_code, count in rule_counts.items():
            if count < self._min_occurrence:
                continue

            # 프로젝트 내 비율
            ratio_in_project = count / total_project_logs

            # 전체 대비 프로젝트 집중도
            total_rule_count = sum(1 for log in logs if log.rule_code == rule_code)
            concentration = count / total_rule_count if total_rule_count > 0 else 0

            if (
                concentration >= 0.5 and ratio_in_project >= 0.1
            ):  # 50% 이상이 이 프로젝트에 집중
                patterns.append(
                    ViolationPattern(
                        pattern_type=PatternType.PROJECT_SPECIFIC,
                        rule_codes=[rule_code],
                        occurrence_count=count,
                        confidence=round(concentration, 3),
                        description=f"프로젝트 '{project_id}'에서 규칙 '{rule_code}' 위반이 집중됨 ({concentration:.1%})",
                        project_id=project_id,
                        recommended_action=f"프로젝트 '{project_id}'에 대한 '{rule_code}' 규칙 교육 권장",
                    )
                )

        return patterns

    def detect_user_specific_patterns(
        self,
        logs: list[ViolationLog],
        user_id: str,
    ) -> list[ViolationPattern]:
        """사용자 특화 패턴 탐지

        특정 사용자에게서 자주 발생하는 위반 패턴을 분석합니다.

        Args:
            logs: 분석할 위반 로그 목록
            user_id: 사용자 ID

        Returns:
            발견된 USER_SPECIFIC 패턴 목록
        """
        patterns: list[ViolationPattern] = []

        # 사용자별 규칙 집계
        user_logs = [log for log in logs if log.context.user_id == user_id]

        if len(user_logs) < self._min_occurrence:
            return patterns

        rule_counts = Counter(log.rule_code for log in user_logs)
        total_user_logs = len(user_logs)

        for rule_code, count in rule_counts.items():
            if count < self._min_occurrence:
                continue

            # 사용자 내 비율
            ratio_in_user = count / total_user_logs

            # 전체 대비 사용자 집중도
            total_rule_count = sum(1 for log in logs if log.rule_code == rule_code)
            concentration = count / total_rule_count if total_rule_count > 0 else 0

            if (
                concentration >= 0.3 and ratio_in_user >= 0.1
            ):  # 30% 이상이 이 사용자에 집중
                patterns.append(
                    ViolationPattern(
                        pattern_type=PatternType.USER_SPECIFIC,
                        rule_codes=[rule_code],
                        occurrence_count=count,
                        confidence=round(concentration, 3),
                        description=f"사용자 '{user_id}'에서 규칙 '{rule_code}' 위반이 집중됨 ({concentration:.1%})",
                        user_id=user_id,
                        recommended_action=f"사용자 '{user_id}'에 대한 '{rule_code}' 규칙 가이드 제공 권장",
                    )
                )

        return patterns

    def get_summary(
        self,
        patterns: list[ViolationPattern],
    ) -> dict:
        """패턴 분석 결과 요약

        Args:
            patterns: 분석된 패턴 목록

        Returns:
            패턴 유형별 통계 및 요약
        """
        if not patterns:
            return {
                "total_patterns": 0,
                "by_type": {},
                "top_patterns": [],
                "recommendations": [],
            }

        by_type = defaultdict(int)
        for pattern in patterns:
            by_type[pattern.pattern_type.value] += 1

        # 상위 패턴 (신뢰도 기준)
        top_patterns = sorted(patterns, key=lambda p: p.confidence, reverse=True)[:5]

        # 고유 권장 조치
        recommendations = list(
            {p.recommended_action for p in patterns if p.recommended_action}
        )[:10]

        return {
            "total_patterns": len(patterns),
            "by_type": dict(by_type),
            "top_patterns": [
                {
                    "type": p.pattern_type.value,
                    "rules": p.rule_codes,
                    "confidence": p.confidence,
                    "description": p.description,
                }
                for p in top_patterns
            ],
            "recommendations": recommendations,
        }

    def _filter_logs(
        self,
        logs: list[ViolationLog],
        project_id: Optional[str],
        user_id: Optional[str],
    ) -> list[ViolationLog]:
        """로그 필터링"""
        filtered = logs

        if project_id:
            filtered = [log for log in filtered if log.context.project_id == project_id]

        if user_id:
            filtered = [log for log in filtered if log.context.user_id == user_id]

        return filtered

    def _deduplicate_patterns(
        self,
        patterns: list[ViolationPattern],
    ) -> list[ViolationPattern]:
        """중복 패턴 제거"""
        seen = set()
        unique_patterns = []

        for pattern in patterns:
            key = (
                pattern.pattern_type,
                tuple(sorted(pattern.rule_codes)),
                pattern.project_id,
                pattern.user_id,
            )

            if key not in seen:
                seen.add(key)
                unique_patterns.append(pattern)

        return unique_patterns

    def _get_recurring_recommendation(self, rule_code: str, count: int) -> str:
        """반복 위반에 대한 권장 조치 생성"""
        if count >= 10:
            return f"규칙 '{rule_code}'에 대한 팀 전체 교육 및 자동화 검사 강화 권장"
        elif count >= 5:
            return f"규칙 '{rule_code}'에 대한 코드 리뷰 체크리스트 추가 권장"
        else:
            return f"규칙 '{rule_code}'에 대한 개발자 가이드 공유 권장"

    def _get_correlation_recommendation(self, rule_a: str, rule_b: str) -> str:
        """상관 위반에 대한 권장 조치 생성"""
        return f"규칙 '{rule_a}'와 '{rule_b}'의 관계 분석 및 통합 가이드 작성 권장"


# ==================== 싱글톤 접근 ====================

_analyzer: Optional[PatternAnalyzer] = None


def get_pattern_analyzer(
    min_occurrence: int = 3,
    min_confidence: float = 0.5,
) -> PatternAnalyzer:
    """싱글톤 PatternAnalyzer 반환

    Args:
        min_occurrence: 패턴으로 인정할 최소 발생 횟수
        min_confidence: 패턴으로 인정할 최소 신뢰도

    Returns:
        PatternAnalyzer 인스턴스
    """
    global _analyzer

    if _analyzer is None:
        _analyzer = PatternAnalyzer(
            min_occurrence=min_occurrence,
            min_confidence=min_confidence,
        )

    return _analyzer
