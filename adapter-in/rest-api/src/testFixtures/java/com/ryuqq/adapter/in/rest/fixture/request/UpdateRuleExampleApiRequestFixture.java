package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.ruleexample.dto.request.UpdateRuleExampleApiRequest;
import java.util.List;

/**
 * UpdateRuleExampleApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateRuleExampleApiRequestFixture {

    private UpdateRuleExampleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static UpdateRuleExampleApiRequest valid() {
        return new UpdateRuleExampleApiRequest(
                "GOOD",
                "public class Order {\n    private final OrderId id;\n}",
                "JAVA",
                "Aggregate 클래스 예시",
                List.of(1, 2));
    }

    /** 정상 요청 - 최소 필수 필드만 */
    public static UpdateRuleExampleApiRequest validMinimal() {
        return new UpdateRuleExampleApiRequest(
                "BAD", "// BAD example", "JAVA", "Explanation", List.of());
    }

    /** 잘못된 요청 - exampleType 누락 (빈 문자열) */
    public static UpdateRuleExampleApiRequest invalidWithBlankExampleType() {
        return new UpdateRuleExampleApiRequest("", "code", "JAVA", "explanation", List.of());
    }

    /** 잘못된 요청 - exampleType 길이 초과 (20자 초과) */
    public static UpdateRuleExampleApiRequest invalidWithLongExampleType() {
        return new UpdateRuleExampleApiRequest(
                "A".repeat(21), "code", "JAVA", "explanation", List.of());
    }

    /** 잘못된 요청 - code 누락 (빈 문자열) */
    public static UpdateRuleExampleApiRequest invalidWithBlankCode() {
        return new UpdateRuleExampleApiRequest("GOOD", "", "JAVA", "explanation", List.of());
    }

    /** 잘못된 요청 - code 길이 초과 (10000자 초과) */
    public static UpdateRuleExampleApiRequest invalidWithLongCode() {
        return new UpdateRuleExampleApiRequest(
                "GOOD", "A".repeat(10001), "JAVA", "explanation", List.of());
    }

    /** 잘못된 요청 - language 누락 (빈 문자열) */
    public static UpdateRuleExampleApiRequest invalidWithBlankLanguage() {
        return new UpdateRuleExampleApiRequest("GOOD", "code", "", "explanation", List.of());
    }

    /** 잘못된 요청 - language 길이 초과 (30자 초과) */
    public static UpdateRuleExampleApiRequest invalidWithLongLanguage() {
        return new UpdateRuleExampleApiRequest(
                "GOOD", "code", "A".repeat(31), "explanation", List.of());
    }

    /** 잘못된 요청 - explanation 누락 (null) */
    public static UpdateRuleExampleApiRequest invalidWithNullExplanation() {
        return new UpdateRuleExampleApiRequest("GOOD", "code", "JAVA", null, List.of());
    }

    /** 잘못된 요청 - explanation 빈 문자열 */
    public static UpdateRuleExampleApiRequest invalidWithBlankExplanation() {
        return new UpdateRuleExampleApiRequest("GOOD", "code", "JAVA", "", List.of());
    }

    /** 잘못된 요청 - explanation 길이 초과 (2000자 초과) */
    public static UpdateRuleExampleApiRequest invalidWithLongExplanation() {
        return new UpdateRuleExampleApiRequest("GOOD", "code", "JAVA", "A".repeat(2001), List.of());
    }

    /** 잘못된 요청 - highlightLines 누락 (null) */
    public static UpdateRuleExampleApiRequest invalidWithNullHighlightLines() {
        return new UpdateRuleExampleApiRequest("GOOD", "code", "JAVA", "explanation", null);
    }

    /** 커스텀 요청 생성 */
    public static UpdateRuleExampleApiRequest custom(
            String exampleType,
            String code,
            String language,
            String explanation,
            List<Integer> highlightLines) {
        return new UpdateRuleExampleApiRequest(
                exampleType, code, language, explanation, highlightLines);
    }
}
