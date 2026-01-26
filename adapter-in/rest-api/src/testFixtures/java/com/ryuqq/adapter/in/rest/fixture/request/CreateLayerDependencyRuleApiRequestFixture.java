package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.layerdependency.dto.request.CreateLayerDependencyRuleApiRequest;

/**
 * CreateLayerDependencyRuleApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateLayerDependencyRuleApiRequestFixture {

    private CreateLayerDependencyRuleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static CreateLayerDependencyRuleApiRequest valid() {
        return new CreateLayerDependencyRuleApiRequest("DOMAIN", "APPLICATION", "ALLOWED", null);
    }

    /** 정상 요청 - CONDITIONAL 타입 */
    public static CreateLayerDependencyRuleApiRequest validConditional() {
        return new CreateLayerDependencyRuleApiRequest(
                "DOMAIN", "APPLICATION", "CONDITIONAL", "특정 조건에서만 허용");
    }

    /** 정상 요청 - FORBIDDEN 타입 */
    public static CreateLayerDependencyRuleApiRequest validForbidden() {
        return new CreateLayerDependencyRuleApiRequest("APPLICATION", "DOMAIN", "FORBIDDEN", null);
    }

    /** 잘못된 요청 - fromLayer 누락 (빈 문자열) */
    public static CreateLayerDependencyRuleApiRequest invalidWithBlankFromLayer() {
        return new CreateLayerDependencyRuleApiRequest("", "APPLICATION", "ALLOWED", null);
    }

    /** 잘못된 요청 - fromLayer null */
    public static CreateLayerDependencyRuleApiRequest invalidWithNullFromLayer() {
        return new CreateLayerDependencyRuleApiRequest(null, "APPLICATION", "ALLOWED", null);
    }

    /** 잘못된 요청 - toLayer 누락 (빈 문자열) */
    public static CreateLayerDependencyRuleApiRequest invalidWithBlankToLayer() {
        return new CreateLayerDependencyRuleApiRequest("DOMAIN", "", "ALLOWED", null);
    }

    /** 잘못된 요청 - toLayer null */
    public static CreateLayerDependencyRuleApiRequest invalidWithNullToLayer() {
        return new CreateLayerDependencyRuleApiRequest("DOMAIN", null, "ALLOWED", null);
    }

    /** 잘못된 요청 - dependencyType 누락 (빈 문자열) */
    public static CreateLayerDependencyRuleApiRequest invalidWithBlankDependencyType() {
        return new CreateLayerDependencyRuleApiRequest("DOMAIN", "APPLICATION", "", null);
    }

    /** 잘못된 요청 - dependencyType null */
    public static CreateLayerDependencyRuleApiRequest invalidWithNullDependencyType() {
        return new CreateLayerDependencyRuleApiRequest("DOMAIN", "APPLICATION", null, null);
    }

    /** 잘못된 요청 - conditionDescription 길이 초과 (2000자 초과) */
    public static CreateLayerDependencyRuleApiRequest invalidWithLongConditionDescription() {
        String longDescription = "a".repeat(2001);
        return new CreateLayerDependencyRuleApiRequest(
                "DOMAIN", "APPLICATION", "CONDITIONAL", longDescription);
    }

    /** 커스텀 요청 생성 */
    public static CreateLayerDependencyRuleApiRequest custom(
            String fromLayer, String toLayer, String dependencyType, String conditionDescription) {
        return new CreateLayerDependencyRuleApiRequest(
                fromLayer, toLayer, dependencyType, conditionDescription);
    }
}
