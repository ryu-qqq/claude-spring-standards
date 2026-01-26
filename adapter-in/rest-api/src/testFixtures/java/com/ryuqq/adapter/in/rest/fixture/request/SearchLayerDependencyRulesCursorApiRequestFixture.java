package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.layerdependency.dto.request.SearchLayerDependencyRulesCursorApiRequest;
import java.util.List;

/**
 * SearchLayerDependencyRulesCursorApiRequestFixture - SearchLayerDependencyRulesCursorApiRequest
 * 테스트 픽스처
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SearchLayerDependencyRulesCursorApiRequestFixture {

    /**
     * 유효한 요청 생성
     *
     * @return SearchLayerDependencyRulesCursorApiRequest
     */
    public static SearchLayerDependencyRulesCursorApiRequest valid() {
        return new SearchLayerDependencyRulesCursorApiRequest(
                null, // cursor
                20, // size
                List.of(1L, 2L), // architectureIds
                List.of("ALLOWED", "FORBIDDEN"), // dependencyTypes
                "CONDITION_DESCRIPTION", // searchField
                "특정 조건"); // searchWord
    }

    /**
     * 첫 페이지 요청 생성
     *
     * @return SearchLayerDependencyRulesCursorApiRequest
     */
    public static SearchLayerDependencyRulesCursorApiRequest firstPage() {
        return new SearchLayerDependencyRulesCursorApiRequest(null, null, null, null, null, null);
    }

    /**
     * 커서가 있는 요청 생성
     *
     * @return SearchLayerDependencyRulesCursorApiRequest
     */
    public static SearchLayerDependencyRulesCursorApiRequest withCursor() {
        return new SearchLayerDependencyRulesCursorApiRequest(
                "100", // cursor
                20, // size
                null, null, null, null);
    }
}
