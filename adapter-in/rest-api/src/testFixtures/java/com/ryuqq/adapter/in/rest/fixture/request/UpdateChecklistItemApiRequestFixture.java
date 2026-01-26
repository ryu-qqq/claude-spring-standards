package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.checklistitem.dto.request.UpdateChecklistItemApiRequest;

/**
 * UpdateChecklistItemApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateChecklistItemApiRequestFixture {

    private UpdateChecklistItemApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static UpdateChecklistItemApiRequest valid() {
        return new UpdateChecklistItemApiRequest(
                1, "Lombok 어노테이션 사용 여부 확인", "AUTOMATED", "ARCHUNIT", "AGG-001-CHECK-1", true);
    }

    /** 정상 요청 - 자동화 도구 없음 */
    public static UpdateChecklistItemApiRequest validWithoutAutomation() {
        return new UpdateChecklistItemApiRequest(1, "체크 설명", "MANUAL", "", "", false);
    }

    /** 잘못된 요청 - sequenceOrder 누락 (null) */
    public static UpdateChecklistItemApiRequest invalidWithNullSequenceOrder() {
        return new UpdateChecklistItemApiRequest(null, "체크 설명", "MANUAL", "", "", false);
    }

    /** 잘못된 요청 - sequenceOrder가 1 미만 */
    public static UpdateChecklistItemApiRequest invalidWithSequenceOrderTooSmall() {
        return new UpdateChecklistItemApiRequest(0, "체크 설명", "MANUAL", "", "", false);
    }

    /** 잘못된 요청 - checkDescription 누락 (빈 문자열) */
    public static UpdateChecklistItemApiRequest invalidWithBlankCheckDescription() {
        return new UpdateChecklistItemApiRequest(1, "", "MANUAL", "", "", false);
    }

    /** 잘못된 요청 - checkDescription 길이 초과 (500자 초과) */
    public static UpdateChecklistItemApiRequest invalidWithLongCheckDescription() {
        return new UpdateChecklistItemApiRequest(1, "A".repeat(501), "MANUAL", "", "", false);
    }

    /** 잘못된 요청 - checkType 누락 (빈 문자열) */
    public static UpdateChecklistItemApiRequest invalidWithBlankCheckType() {
        return new UpdateChecklistItemApiRequest(1, "체크 설명", "", "", "", false);
    }

    /** 잘못된 요청 - automationTool null */
    public static UpdateChecklistItemApiRequest invalidWithNullAutomationTool() {
        return new UpdateChecklistItemApiRequest(1, "체크 설명", "AUTOMATED", null, "", false);
    }

    /** 잘못된 요청 - isCritical null */
    public static UpdateChecklistItemApiRequest invalidWithNullIsCritical() {
        return new UpdateChecklistItemApiRequest(1, "체크 설명", "MANUAL", "", "", null);
    }

    /** 커스텀 요청 생성 */
    public static UpdateChecklistItemApiRequest custom(
            Integer sequenceOrder,
            String checkDescription,
            String checkType,
            String automationTool,
            String automationRuleId,
            Boolean isCritical) {
        return new UpdateChecklistItemApiRequest(
                sequenceOrder,
                checkDescription,
                checkType,
                automationTool,
                automationRuleId,
                isCritical);
    }
}
