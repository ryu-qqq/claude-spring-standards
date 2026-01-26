package com.ryuqq.adapter.in.rest.convention.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.convention.dto.request.SearchConventionsCursorApiRequest;
import com.ryuqq.adapter.in.rest.convention.dto.response.ConventionApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchConventionsCursorApiRequestFixture;
import com.ryuqq.application.convention.dto.query.ConventionSearchParams;
import com.ryuqq.application.convention.dto.response.ConventionResult;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ConventionQueryApiMapper 단위 테스트
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
@DisplayName("ConventionQueryApiMapper 단위 테스트")
class ConventionQueryApiMapperTest {

    private ConventionQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ConventionQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchConventionsCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchConventionsCursorApiRequest request =
                    SearchConventionsCursorApiRequestFixture.valid();

            // When
            ConventionSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams()).isNotNull();
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchConventionsCursorApiRequest request =
                    new SearchConventionsCursorApiRequest(null, null, null);

            // When
            ConventionSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("moduleIds 파라미터가 있으면 Query에 전달")
        void withModuleIds_ShouldPassModuleIdsToQuery() {
            // Given
            SearchConventionsCursorApiRequest request =
                    SearchConventionsCursorApiRequestFixture.validWithModuleIds();

            // When
            ConventionSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.moduleIds()).containsExactly(1L, 2L);
            assertThat(searchParams.hasModuleIds()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponses(List<ConventionResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new ConventionResult(
                            1L,
                            1L,
                            "1.0.0",
                            "Description 1",
                            true,
                            false,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var result2 =
                    new ConventionResult(
                            2L,
                            1L,
                            "1.0.0",
                            "Description 2",
                            true,
                            false,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));

            // When
            List<ConventionApiResponse> responses = mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).id()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(ConventionSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new ConventionResult(
                            1L,
                            1L,
                            "1.0.0",
                            "Description",
                            true,
                            false,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var sliceResult =
                    new ConventionSliceResult(
                            List.of(result),
                            com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, true, 1));

            // When
            SliceApiResponse<ConventionApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.hasNext()).isTrue();
        }
    }
}
