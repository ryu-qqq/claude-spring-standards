package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.layerdependency.dto.request.UpdateLayerDependencyRuleApiRequest;

/**
 * UpdateLayerDependencyRuleApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateLayerDependencyRuleApiRequestFixture {

    private UpdateLayerDependencyRuleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static UpdateLayerDependencyRuleApiRequest valid() {
        return new UpdateLayerDependencyRuleApiRequest(
                "DOMAIN", "APPLICATION", "FORBIDDEN", "조건 설명");
    }

    /** 정상 요청 - CONDITIONAL 타입 */
    public static UpdateLayerDependencyRuleApiRequest validConditional() {
        return new UpdateLayerDependencyRuleApiRequest(
                "DOMAIN", "APPLICATION", "CONDITIONAL", "수정된 조건 설명");
    }

    /** 잘못된 요청 - fromLayer 누락 (빈 문자열) */
    public static UpdateLayerDependencyRuleApiRequest invalidWithBlankFromLayer() {
        return new UpdateLayerDependencyRuleApiRequest("", "APPLICATION", "ALLOWED", null);
    }

    /** 잘못된 요청 - toLayer 누락 (빈 문자열) */
    public static UpdateLayerDependencyRuleApiRequest invalidWithBlankToLayer() {
        return new UpdateLayerDependencyRuleApiRequest("DOMAIN", "", "ALLOWED", null);
    }

    /** 잘못된 요청 - conditionDescription 길이 초과 (2000자 초과) */
    public static UpdateLayerDependencyRuleApiRequest invalidWithLongConditionDescription() {
        String longDescription = "a".repeat(2001);
        return new UpdateLayerDependencyRuleApiRequest(
                "DOMAIN", "APPLICATION", "CONDITIONAL", longDescription);
    }

    /** 커스텀 요청 생성 */
    public static UpdateLayerDependencyRuleApiRequest custom(
            String fromLayer, String toLayer, String dependencyType, String conditionDescription) {
        return new UpdateLayerDependencyRuleApiRequest(
                fromLayer, toLayer, dependencyType, conditionDescription);
    }
}
