package com.ryuqq.adapter.in.rest.layer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchLayersCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.layer.dto.request.SearchLayersCursorApiRequest;
import com.ryuqq.adapter.in.rest.layer.dto.response.LayerApiResponse;
import com.ryuqq.application.layer.dto.query.LayerSearchParams;
import com.ryuqq.application.layer.dto.response.LayerResult;
import com.ryuqq.application.layer.dto.response.LayerSliceResult;
import com.ryuqq.domain.common.vo.SliceMeta;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * LayerQueryApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("LayerQueryApiMapper 단위 테스트")
class LayerQueryApiMapperTest {

    private LayerQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LayerQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchLayersCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("성공 - 모든 값이 있는 요청 변환")
        void shouldConvertRequestWithAllValues() {
            // Given
            SearchLayersCursorApiRequest request =
                    SearchLayersCursorApiRequestFixture.validWithArchitectureIds(List.of(1L, 2L));

            // When
            LayerSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams).isNotNull();
            assertThat(searchParams.architectureIds()).containsExactly(1L, 2L);
            assertThat(searchParams.size()).isEqualTo(request.size());
        }

        @Test
        @DisplayName("성공 - 커서가 있는 경우")
        void shouldHandleCursor() {
            // Given
            SearchLayersCursorApiRequest request =
                    SearchLayersCursorApiRequestFixture.validWithCursor("123");

            // When
            LayerSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams).isNotNull();
            assertThat(searchParams.cursor()).isEqualTo("123");
        }

        @Test
        @DisplayName("성공 - 검색 필드와 검색어가 있는 경우")
        void shouldHandleSearchFieldAndWord() {
            // Given
            SearchLayersCursorApiRequest request =
                    SearchLayersCursorApiRequestFixture.validWithSearch("CODE", "DOMAIN");

            // When
            LayerSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams).isNotNull();
            assertThat(searchParams.searchField()).isEqualTo("CODE");
            assertThat(searchParams.searchWord()).isEqualTo("DOMAIN");
            assertThat(searchParams.hasSearch()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponses(List<LayerResult>)")
    class ToResponses {

        @Test
        @DisplayName("성공 - LayerResult 목록을 LayerApiResponse 목록으로 변환")
        void shouldConvertResultsToResponses() {
            // Given
            Instant now = Instant.parse("2024-01-01T00:00:00Z");
            List<LayerResult> results =
                    List.of(
                            new LayerResult(1L, 1L, "DOMAIN", "Domain Layer", "Desc1", 1, now, now),
                            new LayerResult(
                                    2L,
                                    1L,
                                    "APPLICATION",
                                    "Application Layer",
                                    "Desc2",
                                    2,
                                    now,
                                    now));

            // When
            List<LayerApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).code()).isEqualTo("DOMAIN");
            assertThat(responses.get(1).code()).isEqualTo("APPLICATION");
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void shouldHandleEmptyList() {
            // Given
            List<LayerResult> results = List.of();

            // When
            List<LayerApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSliceResponse(LayerSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("성공 - LayerSliceResult를 SliceApiResponse로 변환")
        void shouldConvertSliceResultToSliceResponse() {
            // Given
            Instant now = Instant.parse("2024-01-01T00:00:00Z");
            List<LayerResult> content =
                    List.of(
                            new LayerResult(1L, 1L, "DOMAIN", "Domain Layer", "Desc1", 1, now, now),
                            new LayerResult(
                                    2L,
                                    1L,
                                    "APPLICATION",
                                    "Application Layer",
                                    "Desc2",
                                    2,
                                    now,
                                    now));
            SliceMeta sliceMeta = SliceMeta.withCursor("2", 20, true);
            LayerSliceResult sliceResult = new LayerSliceResult(content, sliceMeta);

            // When
            SliceApiResponse<LayerApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(2);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("2");
        }

        @Test
        @DisplayName("성공 - 빈 결과 변환")
        void shouldConvertEmptySliceResult() {
            // Given
            LayerSliceResult sliceResult = LayerSliceResult.empty(20);

            // When
            SliceApiResponse<LayerApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.hasNext()).isFalse();
        }
    }
}
