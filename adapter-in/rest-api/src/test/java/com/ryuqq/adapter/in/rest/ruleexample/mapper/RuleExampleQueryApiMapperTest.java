package com.ryuqq.adapter.in.rest.ruleexample.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchRuleExamplesCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.ruleexample.dto.request.SearchRuleExamplesCursorApiRequest;
import com.ryuqq.adapter.in.rest.ruleexample.dto.response.RuleExampleApiResponse;
import com.ryuqq.application.ruleexample.dto.query.RuleExampleSearchParams;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * RuleExampleQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Query DTO 변환
 *   <li>Result DTO → Response DTO 변환
 *   <li>SliceResult → SliceApiResponse 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("RuleExampleQueryApiMapper 단위 테스트")
class RuleExampleQueryApiMapperTest {

    private RuleExampleQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RuleExampleQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchRuleExamplesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            SearchRuleExamplesCursorApiRequest request =
                    SearchRuleExamplesCursorApiRequestFixture.valid();

            // When
            RuleExampleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.ruleIds()).containsExactly(1L);
            assertThat(searchParams.exampleTypes()).containsExactly("GOOD");
            assertThat(searchParams.cursorParams()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResponse(RuleExampleResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new RuleExampleResult(
                            1L,
                            1L,
                            "GOOD",
                            "public class Order {\n    private final OrderId id;\n}",
                            "JAVA",
                            "Aggregate 클래스 예시",
                            List.of(1, 2),
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            RuleExampleApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.ruleExampleId()).isEqualTo(1L);
            assertThat(response.ruleId()).isEqualTo(1L);
            assertThat(response.exampleType()).isEqualTo("GOOD");
            assertThat(response.code())
                    .isEqualTo("public class Order {\n    private final OrderId id;\n}");
            assertThat(response.language()).isEqualTo("JAVA");
            assertThat(response.explanation()).isEqualTo("Aggregate 클래스 예시");
            assertThat(response.highlightLines()).containsExactly(1, 2);
            assertThat(response.source()).isEqualTo("MANUAL");
            assertThat(response.feedbackId()).isNull();
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<RuleExampleResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new RuleExampleResult(
                            1L,
                            1L,
                            "GOOD",
                            "code1",
                            "JAVA",
                            "explanation1",
                            List.of(),
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new RuleExampleResult(
                            2L,
                            1L,
                            "BAD",
                            "code2",
                            "JAVA",
                            "explanation2",
                            List.of(),
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<RuleExampleApiResponse> responses = mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).ruleExampleId()).isEqualTo(1L);
            assertThat(responses.get(1).ruleExampleId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(RuleExampleSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new RuleExampleResult(
                            1L,
                            1L,
                            "GOOD",
                            "code",
                            "JAVA",
                            "explanation",
                            List.of(),
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = RuleExampleSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<RuleExampleApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("1");
        }
    }
}
