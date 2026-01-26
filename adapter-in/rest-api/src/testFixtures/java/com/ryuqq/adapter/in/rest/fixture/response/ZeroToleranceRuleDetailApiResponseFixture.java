package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemApiResponse;
import com.ryuqq.adapter.in.rest.ruleexample.dto.response.RuleExampleApiResponse;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.response.ZeroToleranceRuleDetailApiResponse;
import java.util.List;

/**
 * ZeroToleranceRuleDetailApiResponse Test Fixture
 *
 * <p>REST API 단위 테스트에서 사용하는 Response 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ZeroToleranceRuleDetailApiResponseFixture {

    private ZeroToleranceRuleDetailApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 응답 - 기본 케이스 */
    public static ZeroToleranceRuleDetailApiResponse valid() {
        return new ZeroToleranceRuleDetailApiResponse(
                1L,
                "AGG-001",
                "Lombok 사용 금지",
                "BLOCKER",
                "ANNOTATION",
                "Domain 레이어에서 Lombok 어노테이션 사용을 금지합니다.",
                "Lombok은 바이트코드 조작으로 예측 불가능한 동작을 유발할 수 있습니다.",
                false,
                List.of("AGGREGATE", "ENTITY", "VALUE_OBJECT"),
                List.of(RuleExampleApiResponseFixture.valid()),
                List.of(createChecklistItem(1L)),
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 정상 응답 - 예시와 체크리스트 없음 */
    public static ZeroToleranceRuleDetailApiResponse validWithoutExamples() {
        return new ZeroToleranceRuleDetailApiResponse(
                1L,
                "AGG-001",
                "Lombok 사용 금지",
                "BLOCKER",
                "ANNOTATION",
                "Domain 레이어에서 Lombok 어노테이션 사용을 금지합니다.",
                "Lombok은 바이트코드 조작으로 예측 불가능한 동작을 유발할 수 있습니다.",
                false,
                List.of("AGGREGATE", "ENTITY", "VALUE_OBJECT"),
                List.of(),
                List.of(),
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 정상 응답 - 여러 예시와 체크리스트 포함 */
    public static ZeroToleranceRuleDetailApiResponse validWithMultipleItems() {
        return new ZeroToleranceRuleDetailApiResponse(
                1L,
                "AGG-001",
                "Lombok 사용 금지",
                "BLOCKER",
                "ANNOTATION",
                "Domain 레이어에서 Lombok 어노테이션 사용을 금지합니다.",
                "Lombok은 바이트코드 조작으로 예측 불가능한 동작을 유발할 수 있습니다.",
                false,
                List.of("AGGREGATE", "ENTITY", "VALUE_OBJECT"),
                List.of(
                        RuleExampleApiResponseFixture.valid(),
                        RuleExampleApiResponseFixture.badExample()),
                List.of(createChecklistItem(1L), createChecklistItem(2L)),
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 커스텀 응답 생성 */
    public static ZeroToleranceRuleDetailApiResponse custom(
            Long id,
            String code,
            String name,
            String severity,
            String category,
            String description,
            String rationale,
            boolean autoFixable,
            List<String> appliesTo,
            List<RuleExampleApiResponse> examples,
            List<ChecklistItemApiResponse> checklistItems,
            String createdAt,
            String updatedAt) {
        return new ZeroToleranceRuleDetailApiResponse(
                id,
                code,
                name,
                severity,
                category,
                description,
                rationale,
                autoFixable,
                appliesTo,
                examples,
                checklistItems,
                createdAt,
                updatedAt);
    }

    private static ChecklistItemApiResponse createChecklistItem(Long id) {
        return new ChecklistItemApiResponse(
                id,
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
}
