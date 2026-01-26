package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.ruleexample.dto.request.SearchRuleExamplesCursorApiRequest;
import java.util.List;

/**
 * SearchRuleExamplesCursorApiRequestFixture Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SearchRuleExamplesCursorApiRequestFixture {

    private SearchRuleExamplesCursorApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static SearchRuleExamplesCursorApiRequest valid() {
        return new SearchRuleExamplesCursorApiRequest(
                null, 20, List.of(1L), List.of("GOOD"), List.of("JAVA"));
    }

    /** 정상 요청 - 필터 없이 조회 */
    public static SearchRuleExamplesCursorApiRequest validWithoutFilters() {
        return new SearchRuleExamplesCursorApiRequest(null, 20, null, null, null);
    }

    /** 정상 요청 - 커서 포함 */
    public static SearchRuleExamplesCursorApiRequest validWithCursor() {
        return new SearchRuleExamplesCursorApiRequest(
                "100", 20, List.of(1L), List.of("GOOD"), List.of("JAVA"));
    }

    /** 커스텀 요청 생성 */
    public static SearchRuleExamplesCursorApiRequest custom(
            String cursor,
            Integer size,
            List<Long> ruleIds,
            List<String> exampleTypes,
            List<String> languages) {
        return new SearchRuleExamplesCursorApiRequest(
                cursor, size, ruleIds, exampleTypes, languages);
    }
}
