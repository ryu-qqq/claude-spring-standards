package com.ryuqq.adapter.in.rest.zerotolerance.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.SearchZeroToleranceRulesCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.SearchZeroToleranceRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.response.ZeroToleranceRuleDetailApiResponse;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.response.ZeroToleranceRuleSliceApiResponse;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.zerotolerance.dto.query.ZeroToleranceRuleSearchParams;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleDetailResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ZeroToleranceRuleQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Query DTO 변환
 *   <li>DetailResult → DetailApiResponse 변환
 *   <li>SliceResult → SliceApiResponse 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ZeroToleranceRuleQueryApiMapper 단위 테스트")
class ZeroToleranceRuleQueryApiMapperTest {

    private ZeroToleranceRuleQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ZeroToleranceRuleQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchZeroToleranceRulesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchZeroToleranceRulesCursorApiRequest request =
                    SearchZeroToleranceRulesCursorApiRequestFixture.valid();

            // When
            ZeroToleranceRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.conventionIds()).containsExactly(1L, 2L);
            assertThat(searchParams.detectionTypes()).containsExactly("REGEX", "AST");
            assertThat(searchParams.searchField()).isEqualTo("TYPE");
            assertThat(searchParams.searchWord()).isEqualTo("LOMBOK_IN_DOMAIN");
            assertThat(searchParams.autoRejectPr()).isTrue();
            assertThat(searchParams.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchZeroToleranceRulesCursorApiRequest request =
                    new SearchZeroToleranceRulesCursorApiRequest(
                            null, null, null, null, null, null, null);

            // When
            ZeroToleranceRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toApiResponse(ZeroToleranceRuleDetailResult)")
    class ToApiResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var codingRule =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Lombok 사용 금지",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            "Rationale",
                            false,
                            List.of("AGGREGATE"),
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var example =
                    new RuleExampleResult(
                            1L,
                            1L,
                            "GOOD",
                            "code",
                            "JAVA",
                            "explanation",
                            List.of(1),
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var checklistItem =
                    new ChecklistItemResult(
                            1L,
                            1L,
                            1,
                            "check",
                            "AUTOMATED",
                            "ARCHUNIT",
                            "rule-id",
                            true,
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result =
                    ZeroToleranceRuleDetailResult.of(
                            codingRule, List.of(example), List.of(checklistItem));

            // When
            ZeroToleranceRuleDetailApiResponse response = mapper.toApiResponse(result);

            // Then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo("AGG-001");
            assertThat(response.name()).isEqualTo("Lombok 사용 금지");
            assertThat(response.severity()).isEqualTo("BLOCKER");
            assertThat(response.category()).isEqualTo("ANNOTATION");
            assertThat(response.examples()).hasSize(1);
            assertThat(response.checklistItems()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("toSliceApiResponse(ZeroToleranceRuleSliceResult)")
    class ToSliceApiResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var codingRule =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Name",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            null,
                            false,
                            List.of(),
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result = ZeroToleranceRuleDetailResult.withRuleOnly(codingRule);
            var sliceResult = ZeroToleranceRuleSliceResult.of(List.of(result), true);

            // When
            ZeroToleranceRuleSliceApiResponse response = mapper.toSliceApiResponse(sliceResult);

            // Then
            assertThat(response.rules()).hasSize(1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursorId()).isEqualTo(1L);
        }
    }
}
