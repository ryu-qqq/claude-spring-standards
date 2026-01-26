package com.ryuqq.adapter.in.rest.layerdependency.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchLayerDependencyRulesCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.SearchLayerDependencyRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.response.LayerDependencyRuleApiResponse;
import com.ryuqq.application.layerdependency.dto.query.LayerDependencyRuleSearchParams;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleResult;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleSliceResult;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * LayerDependencyRuleQueryApiMapper 단위 테스트
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
@DisplayName("LayerDependencyRuleQueryApiMapper 단위 테스트")
class LayerDependencyRuleQueryApiMapperTest {

    private LayerDependencyRuleQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LayerDependencyRuleQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchLayerDependencyRulesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchLayerDependencyRulesCursorApiRequest request =
                    SearchLayerDependencyRulesCursorApiRequestFixture.valid();

            // When
            LayerDependencyRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.architectureIds()).containsExactly(1L, 2L);
            assertThat(searchParams.dependencyTypes()).containsExactly("ALLOWED", "FORBIDDEN");
            assertThat(searchParams.searchField()).isEqualTo("CONDITION_DESCRIPTION");
            assertThat(searchParams.searchWord()).isEqualTo("특정 조건");
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchLayerDependencyRulesCursorApiRequest request =
                    new SearchLayerDependencyRulesCursorApiRequest(
                            null, null, null, null, null, null);

            // When
            LayerDependencyRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toResponse(LayerDependencyRuleResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new LayerDependencyRuleResult(
                            1L,
                            1L,
                            LayerType.DOMAIN,
                            LayerType.APPLICATION,
                            DependencyType.ALLOWED,
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            LayerDependencyRuleApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.layerDependencyRuleId()).isEqualTo(1L);
            assertThat(response.architectureId()).isEqualTo(1L);
            assertThat(response.fromLayer()).isEqualTo("DOMAIN");
            assertThat(response.toLayer()).isEqualTo("APPLICATION");
            assertThat(response.dependencyType()).isEqualTo("ALLOWED");
            assertThat(response.conditionDescription()).isNull();
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }

        @Test
        @DisplayName("CONDITIONAL 타입 - conditionDescription 포함")
        void conditionalType_ShouldMapConditionDescription() {
            // Given
            var result =
                    new LayerDependencyRuleResult(
                            1L,
                            1L,
                            LayerType.DOMAIN,
                            LayerType.APPLICATION,
                            DependencyType.CONDITIONAL,
                            "특정 조건에서만 허용",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            LayerDependencyRuleApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.dependencyType()).isEqualTo("CONDITIONAL");
            assertThat(response.conditionDescription()).isEqualTo("특정 조건에서만 허용");
        }
    }

    @Nested
    @DisplayName("toResponses(List<LayerDependencyRuleResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new LayerDependencyRuleResult(
                            1L,
                            1L,
                            LayerType.DOMAIN,
                            LayerType.APPLICATION,
                            DependencyType.ALLOWED,
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new LayerDependencyRuleResult(
                            2L,
                            1L,
                            LayerType.APPLICATION,
                            LayerType.ADAPTER_OUT,
                            DependencyType.FORBIDDEN,
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<LayerDependencyRuleApiResponse> responses =
                    mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).layerDependencyRuleId()).isEqualTo(1L);
            assertThat(responses.get(1).layerDependencyRuleId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(LayerDependencyRuleSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validSliceResult_ShouldMapAllFields() {
            // Given
            var result1 =
                    new LayerDependencyRuleResult(
                            1L,
                            1L,
                            LayerType.DOMAIN,
                            LayerType.APPLICATION,
                            DependencyType.ALLOWED,
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = new LayerDependencyRuleSliceResult(List.of(result1), 20, true, 2L);

            // When
            SliceApiResponse<LayerDependencyRuleApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("2");
        }

        @Test
        @DisplayName("다음 페이지 없음 - nextCursor null")
        void noNextPage_ShouldReturnNullCursor() {
            // Given
            var result1 =
                    new LayerDependencyRuleResult(
                            1L,
                            1L,
                            LayerType.DOMAIN,
                            LayerType.APPLICATION,
                            DependencyType.ALLOWED,
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = new LayerDependencyRuleSliceResult(List.of(result1), 20, false, null);

            // When
            SliceApiResponse<LayerDependencyRuleApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }
    }
}
