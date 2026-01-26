package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.ruleexample.dto.request.CreateRuleExampleApiRequest;
import java.util.List;

/**
 * CreateRuleExampleApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateRuleExampleApiRequestFixture {

    private CreateRuleExampleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static CreateRuleExampleApiRequest valid() {
        return new CreateRuleExampleApiRequest(
                1L,
                "GOOD",
                "public class Order {\n    private final OrderId id;\n}",
                "JAVA",
                "Aggregate 클래스 예시",
                List.of(1, 2));
    }

    /** 정상 요청 - 최소 필수 필드만 */
    public static CreateRuleExampleApiRequest validMinimal() {
        return new CreateRuleExampleApiRequest(1L, "BAD", "// BAD example", "JAVA", null, null);
    }

    /** 잘못된 요청 - ruleId 누락 (null) */
    public static CreateRuleExampleApiRequest invalidWithNullRuleId() {
        return new CreateRuleExampleApiRequest(null, "GOOD", "code", "JAVA", null, null);
    }

    /** 잘못된 요청 - exampleType 누락 (빈 문자열) */
    public static CreateRuleExampleApiRequest invalidWithBlankExampleType() {
        return new CreateRuleExampleApiRequest(1L, "", "code", "JAVA", null, null);
    }

    /** 잘못된 요청 - exampleType 길이 초과 (20자 초과) */
    public static CreateRuleExampleApiRequest invalidWithLongExampleType() {
        return new CreateRuleExampleApiRequest(1L, "A".repeat(21), "code", "JAVA", null, null);
    }

    /** 잘못된 요청 - code 누락 (빈 문자열) */
    public static CreateRuleExampleApiRequest invalidWithBlankCode() {
        return new CreateRuleExampleApiRequest(1L, "GOOD", "", "JAVA", null, null);
    }

    /** 잘못된 요청 - code 길이 초과 (10000자 초과) */
    public static CreateRuleExampleApiRequest invalidWithLongCode() {
        return new CreateRuleExampleApiRequest(1L, "GOOD", "A".repeat(10001), "JAVA", null, null);
    }

    /** 잘못된 요청 - language 누락 (빈 문자열) */
    public static CreateRuleExampleApiRequest invalidWithBlankLanguage() {
        return new CreateRuleExampleApiRequest(1L, "GOOD", "code", "", null, null);
    }

    /** 잘못된 요청 - language 길이 초과 (30자 초과) */
    public static CreateRuleExampleApiRequest invalidWithLongLanguage() {
        return new CreateRuleExampleApiRequest(1L, "GOOD", "code", "A".repeat(31), null, null);
    }

    /** 잘못된 요청 - explanation 길이 초과 (2000자 초과) */
    public static CreateRuleExampleApiRequest invalidWithLongExplanation() {
        return new CreateRuleExampleApiRequest(1L, "GOOD", "code", "JAVA", "A".repeat(2001), null);
    }

    /** 커스텀 요청 생성 */
    public static CreateRuleExampleApiRequest custom(
            Long ruleId,
            String exampleType,
            String code,
            String language,
            String explanation,
            List<Integer> highlightLines) {
        return new CreateRuleExampleApiRequest(
                ruleId, exampleType, code, language, explanation, highlightLines);
    }
}
