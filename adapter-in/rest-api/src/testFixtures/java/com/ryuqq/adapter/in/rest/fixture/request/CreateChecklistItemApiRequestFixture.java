package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.checklistitem.dto.request.CreateChecklistItemApiRequest;

/**
 * CreateChecklistItemApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateChecklistItemApiRequestFixture {

    private CreateChecklistItemApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static CreateChecklistItemApiRequest valid() {
        return new CreateChecklistItemApiRequest(
                1L, 1, "Lombok 어노테이션 사용 여부 확인", "AUTOMATED", "ARCHUNIT", "AGG-001-CHECK-1", false);
    }

    /** 정상 요청 - 최소 필드만 */
    public static CreateChecklistItemApiRequest validMinimal() {
        return new CreateChecklistItemApiRequest(1L, 1, "체크 설명", "MANUAL", null, null, false);
    }

    /** 정상 요청 - critical 포함 */
    public static CreateChecklistItemApiRequest validWithCritical() {
        return new CreateChecklistItemApiRequest(
                1L, 1, "Lombok 어노테이션 사용 여부 확인", "AUTOMATED", "ARCHUNIT", "AGG-001-CHECK-1", true);
    }

    /** 잘못된 요청 - ruleId 누락 (null) */
    public static CreateChecklistItemApiRequest invalidWithNullRuleId() {
        return new CreateChecklistItemApiRequest(null, 1, "체크 설명", "MANUAL", null, null, false);
    }

    /** 잘못된 요청 - sequenceOrder 누락 (null) */
    public static CreateChecklistItemApiRequest invalidWithNullSequenceOrder() {
        return new CreateChecklistItemApiRequest(1L, null, "체크 설명", "MANUAL", null, null, false);
    }

    /** 잘못된 요청 - sequenceOrder가 1 미만 */
    public static CreateChecklistItemApiRequest invalidWithSequenceOrderTooSmall() {
        return new CreateChecklistItemApiRequest(1L, 0, "체크 설명", "MANUAL", null, null, false);
    }

    /** 잘못된 요청 - checkDescription 누락 (빈 문자열) */
    public static CreateChecklistItemApiRequest invalidWithBlankCheckDescription() {
        return new CreateChecklistItemApiRequest(1L, 1, "", "MANUAL", null, null, false);
    }

    /** 잘못된 요청 - checkDescription 길이 초과 (500자 초과) */
    public static CreateChecklistItemApiRequest invalidWithLongCheckDescription() {
        return new CreateChecklistItemApiRequest(
                1L, 1, "A".repeat(501), "MANUAL", null, null, false);
    }

    /** 잘못된 요청 - checkType 누락 (빈 문자열) */
    public static CreateChecklistItemApiRequest invalidWithBlankCheckType() {
        return new CreateChecklistItemApiRequest(1L, 1, "체크 설명", "", null, null, false);
    }

    /** 잘못된 요청 - checkType 길이 초과 (20자 초과) */
    public static CreateChecklistItemApiRequest invalidWithLongCheckType() {
        return new CreateChecklistItemApiRequest(1L, 1, "체크 설명", "A".repeat(21), null, null, false);
    }

    /** 커스텀 요청 생성 */
    public static CreateChecklistItemApiRequest custom(
            Long ruleId,
            Integer sequenceOrder,
            String checkDescription,
            String checkType,
            String automationTool,
            String automationRuleId,
            Boolean isCritical) {
        return new CreateChecklistItemApiRequest(
                ruleId,
                sequenceOrder,
                checkDescription,
                checkType,
                automationTool,
                automationRuleId,
                isCritical);
    }
}
