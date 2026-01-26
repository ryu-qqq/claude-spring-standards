package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.UpdateZeroToleranceRuleApiRequest;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;

/**
 * UpdateZeroToleranceRuleApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateZeroToleranceRuleApiRequestFixture {

    private UpdateZeroToleranceRuleApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateZeroToleranceRuleApiRequest valid() {
        return new UpdateZeroToleranceRuleApiRequest(
                "ARCHITECTURE",
                "@(Data|Getter|Setter|Builder|Value|NoArgsConstructor|AllArgsConstructor|RequiredArgsConstructor)",
                DetectionType.REGEX,
                true,
                "Lombok 어노테이션 사용 금지 - Domain Layer에서 Lombok 사용은 허용되지 않습니다.");
    }

    public static UpdateZeroToleranceRuleApiRequest invalidWithBlankType() {
        return new UpdateZeroToleranceRuleApiRequest(
                "", "@Data", DetectionType.REGEX, true, "Lombok 금지");
    }

    public static UpdateZeroToleranceRuleApiRequest invalidWithBlankDetectionPattern() {
        return new UpdateZeroToleranceRuleApiRequest(
                "ARCHITECTURE", "", DetectionType.REGEX, true, "Lombok 금지");
    }

    public static UpdateZeroToleranceRuleApiRequest invalidWithNullDetectionType() {
        return new UpdateZeroToleranceRuleApiRequest(
                "ARCHITECTURE", "@Data", null, true, "Lombok 금지");
    }

    public static UpdateZeroToleranceRuleApiRequest invalidWithNullAutoRejectPr() {
        return new UpdateZeroToleranceRuleApiRequest(
                "ARCHITECTURE", "@Data", DetectionType.REGEX, null, "Lombok 금지");
    }

    public static UpdateZeroToleranceRuleApiRequest invalidWithBlankErrorMessage() {
        return new UpdateZeroToleranceRuleApiRequest(
                "ARCHITECTURE", "@Data", DetectionType.REGEX, true, "");
    }

    public static UpdateZeroToleranceRuleApiRequest withType(String type) {
        return new UpdateZeroToleranceRuleApiRequest(
                type, "@Data", DetectionType.REGEX, true, "Lombok 금지");
    }

    public static UpdateZeroToleranceRuleApiRequest withDetectionType(DetectionType detectionType) {
        return new UpdateZeroToleranceRuleApiRequest(
                "ARCHITECTURE", "@Data", detectionType, true, "Lombok 금지");
    }
}
