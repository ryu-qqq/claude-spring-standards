package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.ruleexample.dto.response.RuleExampleApiResponse;
import java.util.List;

/**
 * RuleExampleApiResponse Test Fixture
 *
 * <p>REST API 단위 테스트에서 사용하는 Response 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RuleExampleApiResponseFixture {

    private RuleExampleApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 응답 - 기본 케이스 */
    public static RuleExampleApiResponse valid() {
        return new RuleExampleApiResponse(
                1L,
                1L,
                "GOOD",
                "public class Order {\n    private final OrderId id;\n}",
                "JAVA",
                "Aggregate 클래스 예시",
                List.of(1, 2),
                "MANUAL",
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 정상 응답 - 최소 필드만 */
    public static RuleExampleApiResponse validMinimal() {
        return new RuleExampleApiResponse(
                1L,
                1L,
                "BAD",
                "// BAD example",
                "JAVA",
                null,
                null,
                "MANUAL",
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 정상 응답 - BAD 예시 */
    public static RuleExampleApiResponse badExample() {
        return new RuleExampleApiResponse(
                2L,
                1L,
                "BAD",
                "@Data\npublic class Order {\n    private OrderId id;\n}",
                "JAVA",
                "Lombok 사용 금지 예시",
                List.of(1),
                "MANUAL",
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 커스텀 응답 생성 */
    public static RuleExampleApiResponse custom(
            Long ruleExampleId,
            Long ruleId,
            String exampleType,
            String code,
            String language,
            String explanation,
            List<Integer> highlightLines,
            String source,
            Long feedbackId,
            String createdAt,
            String updatedAt) {
        return new RuleExampleApiResponse(
                ruleExampleId,
                ruleId,
                exampleType,
                code,
                language,
                explanation,
                highlightLines,
                source,
                feedbackId,
                createdAt,
                updatedAt);
    }
}
