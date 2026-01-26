package com.ryuqq.application.layerdependency.dto.response;

import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import java.time.Instant;

/**
 * LayerDependencyRuleResult - 레이어 의존성 규칙 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * @param id 레이어 의존성 규칙 ID
 * @param architectureId 아키텍처 ID
 * @param fromLayer 소스 레이어
 * @param toLayer 타겟 레이어
 * @param dependencyType 의존성 타입
 * @param conditionDescription 조건 설명
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record LayerDependencyRuleResult(
        Long id,
        Long architectureId,
        LayerType fromLayer,
        LayerType toLayer,
        DependencyType dependencyType,
        String conditionDescription,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * @param rule LayerDependencyRule 도메인 객체
     * @return LayerDependencyRuleResult
     */
    public static LayerDependencyRuleResult from(LayerDependencyRule rule) {
        return new LayerDependencyRuleResult(
                rule.id().value(),
                rule.architectureId().value(),
                rule.fromLayer(),
                rule.toLayer(),
                rule.dependencyType(),
                rule.conditionDescription().value(),
                rule.createdAt(),
                rule.updatedAt());
    }
}
