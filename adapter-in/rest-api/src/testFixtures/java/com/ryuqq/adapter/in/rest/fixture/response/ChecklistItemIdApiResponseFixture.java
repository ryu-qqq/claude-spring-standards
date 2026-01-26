package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemIdApiResponse;

/**
 * ChecklistItemIdApiResponse Test Fixture
 *
 * <p>REST API 단위 테스트에서 사용하는 Response 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ChecklistItemIdApiResponseFixture {

    private ChecklistItemIdApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 응답 - 기본 케이스 */
    public static ChecklistItemIdApiResponse valid() {
        return ChecklistItemIdApiResponse.of(1L);
    }

    /** 커스텀 응답 생성 */
    public static ChecklistItemIdApiResponse custom(Long id) {
        return ChecklistItemIdApiResponse.of(id);
    }
}
