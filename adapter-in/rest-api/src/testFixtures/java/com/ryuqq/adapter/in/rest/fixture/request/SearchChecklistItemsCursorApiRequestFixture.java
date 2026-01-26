package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.checklistitem.dto.request.SearchChecklistItemsCursorApiRequest;
import java.util.List;

/**
 * SearchChecklistItemsCursorApiRequestFixture Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchChecklistItemsCursorApiRequestFixture {

    private SearchChecklistItemsCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static SearchChecklistItemsCursorApiRequest valid() {
        return new SearchChecklistItemsCursorApiRequest(null, 20, null, null, null, null);
    }

    /** 정상 요청 - 필터 없이 조회 */
    public static SearchChecklistItemsCursorApiRequest validWithoutFilters() {
        return new SearchChecklistItemsCursorApiRequest(null, 20, null, null, null, null);
    }

    /** 정상 요청 - 커서 포함 */
    public static SearchChecklistItemsCursorApiRequest validWithCursor() {
        return new SearchChecklistItemsCursorApiRequest("100", 20, null, null, null, null);
    }

    /** 정상 요청 - ruleIds 필터 포함 */
    public static SearchChecklistItemsCursorApiRequest validWithRuleIds() {
        return new SearchChecklistItemsCursorApiRequest(null, 20, List.of(1L), null, null, null);
    }

    /** 정상 요청 - checkTypes 필터 포함 */
    public static SearchChecklistItemsCursorApiRequest validWithCheckTypes() {
        return new SearchChecklistItemsCursorApiRequest(
                null, 20, null, List.of("AUTOMATED"), null, null);
    }

    /** 정상 요청 - automationTools 필터 포함 */
    public static SearchChecklistItemsCursorApiRequest validWithAutomationTools() {
        return new SearchChecklistItemsCursorApiRequest(
                null, 20, null, null, List.of("ARCHUNIT"), null);
    }

    /** 정상 요청 - isCritical 필터 포함 */
    public static SearchChecklistItemsCursorApiRequest validWithIsCritical() {
        return new SearchChecklistItemsCursorApiRequest(null, 20, null, null, null, true);
    }

    /** 커스텀 요청 생성 */
    public static SearchChecklistItemsCursorApiRequest custom(
            String cursor,
            Integer size,
            List<Long> ruleIds,
            List<String> checkTypes,
            List<String> automationTools,
            Boolean isCritical) {
        return new SearchChecklistItemsCursorApiRequest(
                cursor, size, ruleIds, checkTypes, automationTools, isCritical);
    }
}
