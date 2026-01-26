package com.ryuqq.application.layerdependency.dto.command;

/**
 * CreateLayerDependencyRuleCommand - 레이어 의존성 규칙 생성 커맨드
 *
 * <p>레이어 의존성 규칙 생성에 필요한 데이터를 전달합니다.
 *
 * @param architectureId 아키텍처 ID
 * @param fromLayer 소스 레이어 (DOMAIN, APPLICATION, ADAPTER_IN, ADAPTER_OUT, COMMON, INFRASTRUCTURE)
 * @param toLayer 타겟 레이어
 * @param dependencyType 의존성 타입 (ALLOWED, FORBIDDEN, CONDITIONAL)
 * @param conditionDescription 조건 설명 (CONDITIONAL인 경우)
 * @author ryu-qqq
 */
public record CreateLayerDependencyRuleCommand(
        Long architectureId,
        String fromLayer,
        String toLayer,
        String dependencyType,
        String conditionDescription) {}
