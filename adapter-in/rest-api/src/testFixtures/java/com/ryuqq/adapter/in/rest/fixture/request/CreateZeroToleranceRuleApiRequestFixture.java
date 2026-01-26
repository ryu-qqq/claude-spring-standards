package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.CreateZeroToleranceRuleApiRequest;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;

/**
 * CreateZeroToleranceRuleApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateZeroToleranceRuleApiRequestFixture {

    private CreateZeroToleranceRuleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateZeroToleranceRuleApiRequest valid() {
        return new CreateZeroToleranceRuleApiRequest(
                1L,
                "ARCHITECTURE",
                "@(Data|Getter|Setter|Builder|Value|NoArgsConstructor|AllArgsConstructor|RequiredArgsConstructor)",
                DetectionType.REGEX,
                true,
                "Lombok 어노테이션 사용 금지 - Domain Layer에서 Lombok 사용은 허용되지 않습니다.");
    }

    public static CreateZeroToleranceRuleApiRequest invalidWithNullRuleId() {
        return new CreateZeroToleranceRuleApiRequest(
                null, "ARCHITECTURE", "@Data", DetectionType.REGEX, true, "Lombok 금지");
    }

    public static CreateZeroToleranceRuleApiRequest invalidWithBlankType() {
        return new CreateZeroToleranceRuleApiRequest(
                1L, "", "@Data", DetectionType.REGEX, true, "Lombok 금지");
    }

    public static CreateZeroToleranceRuleApiRequest invalidWithBlankDetectionPattern() {
        return new CreateZeroToleranceRuleApiRequest(
                1L, "ARCHITECTURE", "", DetectionType.REGEX, true, "Lombok 금지");
    }

    public static CreateZeroToleranceRuleApiRequest invalidWithNullDetectionType() {
        return new CreateZeroToleranceRuleApiRequest(
                1L, "ARCHITECTURE", "@Data", null, true, "Lombok 금지");
    }

    public static CreateZeroToleranceRuleApiRequest invalidWithNullAutoRejectPr() {
        return new CreateZeroToleranceRuleApiRequest(
                1L, "ARCHITECTURE", "@Data", DetectionType.REGEX, null, "Lombok 금지");
    }

    public static CreateZeroToleranceRuleApiRequest invalidWithBlankErrorMessage() {
        return new CreateZeroToleranceRuleApiRequest(
                1L, "ARCHITECTURE", "@Data", DetectionType.REGEX, true, "");
    }

    public static CreateZeroToleranceRuleApiRequest withRuleId(Long ruleId) {
        return new CreateZeroToleranceRuleApiRequest(
                ruleId,
                "ARCHITECTURE",
                "@(Data|Getter|Setter)",
                DetectionType.REGEX,
                true,
                "Lombok 금지");
    }

    public static CreateZeroToleranceRuleApiRequest withDetectionType(DetectionType detectionType) {
        return new CreateZeroToleranceRuleApiRequest(
                1L, "ARCHITECTURE", "@Data", detectionType, true, "Lombok 금지");
    }
}
