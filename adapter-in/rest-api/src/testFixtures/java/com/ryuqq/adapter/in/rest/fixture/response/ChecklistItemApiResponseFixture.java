package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemApiResponse;

/**
 * ChecklistItemApiResponse Test Fixture
 *
 * <p>REST API 단위 테스트에서 사용하는 Response 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ChecklistItemApiResponseFixture {

    private ChecklistItemApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 응답 - 기본 케이스 */
    public static ChecklistItemApiResponse valid() {
        return new ChecklistItemApiResponse(
                1L,
                1L,
                1,
                "Lombok 어노테이션 사용 여부 확인",
                "AUTOMATED",
                "ARCHUNIT",
                "AGG-001-CHECK-1",
                true,
                "MANUAL",
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 정상 응답 - 최소 필드만 */
    public static ChecklistItemApiResponse validMinimal() {
        return new ChecklistItemApiResponse(
                1L,
                1L,
                1,
                "체크 설명",
                "MANUAL",
                null,
                null,
                false,
                "MANUAL",
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 정상 응답 - 자동화 도구 없음 */
    public static ChecklistItemApiResponse validWithoutAutomation() {
        return new ChecklistItemApiResponse(
                1L,
                1L,
                1,
                "체크 설명",
                "MANUAL",
                null,
                null,
                false,
                "MANUAL",
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 커스텀 응답 생성 */
    public static ChecklistItemApiResponse custom(
            Long id,
            Long ruleId,
            Integer sequenceOrder,
            String checkDescription,
            String checkType,
            String automationTool,
            String automationRuleId,
            Boolean critical,
            String source,
            Long feedbackId,
            String createdAt,
            String updatedAt) {
        return new ChecklistItemApiResponse(
                id,
                ruleId,
                sequenceOrder,
                checkDescription,
                checkType,
                automationTool,
                automationRuleId,
                critical,
                source,
                feedbackId,
                createdAt,
                updatedAt);
    }
}
