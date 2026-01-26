package com.ryuqq.adapter.in.rest.module.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchModulesCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.module.dto.request.SearchModulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.response.ModuleApiResponse;
import com.ryuqq.adapter.in.rest.module.dto.response.ModuleTreeApiResponse;
import com.ryuqq.application.module.dto.query.ModuleSearchParams;
import com.ryuqq.application.module.dto.response.ModuleResult;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.application.module.dto.response.ModuleTreeResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ModuleQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → SearchParams 변환
 *   <li>Result DTO → Response DTO 변환
 *   <li>SliceResult → SliceApiResponse 변환
 *   <li>TreeResult → TreeApiResponse 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ModuleQueryApiMapper 단위 테스트")
class ModuleQueryApiMapperTest {

    private ModuleQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ModuleQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchModulesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchModulesCursorApiRequest request = SearchModulesCursorApiRequestFixture.valid();

            // When
            ModuleSearchParams params = mapper.toSearchParams(request);

            // Then
            assertThat(params.cursorParams().size()).isEqualTo(20);
            assertThat(params.cursorParams().cursor()).isNull();
            assertThat(params.layerIds()).isNull();
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchModulesCursorApiRequest request =
                    new SearchModulesCursorApiRequest(null, null, null);

            // When
            ModuleSearchParams params = mapper.toSearchParams(request);

            // Then
            assertThat(params.cursorParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("layerIds 필터 포함 변환")
        void withLayerIds_ShouldMapLayerIds() {
            // Given
            SearchModulesCursorApiRequest request =
                    SearchModulesCursorApiRequestFixture.validWithLayerIds();

            // When
            ModuleSearchParams params = mapper.toSearchParams(request);

            // Then
            assertThat(params.layerIds()).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("커서 포함 변환")
        void withCursor_ShouldMapCursor() {
            // Given
            SearchModulesCursorApiRequest request =
                    SearchModulesCursorApiRequestFixture.validWithCursor();

            // When
            ModuleSearchParams params = mapper.toSearchParams(request);

            // Then
            assertThat(params.cursorParams().cursor()).isEqualTo("100");
        }
    }

    @Nested
    @DisplayName("toResponse(ModuleResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new ModuleResult(
                            1L,
                            1L,
                            null,
                            "adapter-in-rest-api",
                            "REST API Adapter",
                            "adapter-in/rest-api",
                            ":adapter-in:rest-api",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));

            // When
            ModuleApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.moduleId()).isEqualTo(1L);
            assertThat(response.layerId()).isEqualTo(1L);
            assertThat(response.parentModuleId()).isNull();
            assertThat(response.name()).isEqualTo("adapter-in-rest-api");
            assertThat(response.description()).isEqualTo("REST API Adapter");
            assertThat(response.modulePath()).isEqualTo("adapter-in/rest-api");
            assertThat(response.buildIdentifier()).isEqualTo(":adapter-in:rest-api");
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<ModuleResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new ModuleResult(
                            1L,
                            1L,
                            null,
                            "Module 1",
                            "Description 1",
                            "module1",
                            ":module1",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var result2 =
                    new ModuleResult(
                            2L,
                            1L,
                            null,
                            "Module 2",
                            "Description 2",
                            "module2",
                            ":module2",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));

            // When
            List<ModuleApiResponse> responses = mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).moduleId()).isEqualTo(1L);
            assertThat(responses.get(1).moduleId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(ModuleSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new ModuleResult(
                            1L,
                            1L,
                            null,
                            "Module",
                            "Description",
                            "module",
                            ":module",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var sliceResult = ModuleSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<ModuleApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
        }

        @Test
        @DisplayName("다음 페이지 없음")
        void withoutNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new ModuleResult(
                            1L,
                            1L,
                            null,
                            "Module",
                            "Description",
                            "module",
                            ":module",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var sliceResult = ModuleSliceResult.of(List.of(result), false);

            // When
            SliceApiResponse<ModuleApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }
    }

    @Nested
    @DisplayName("toTreeResponse(ModuleTreeResult)")
    class ToTreeResponse {

        @Test
        @DisplayName("트리 구조 변환 - 자식 포함")
        void withChildren_ShouldMapRecursively() {
            // Given
            var childResult =
                    ModuleTreeResult.of(
                            2L,
                            1L,
                            1L,
                            "Child Module",
                            "Child Description",
                            "child",
                            ":child",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var parentResult =
                    ModuleTreeResult.withChildren(
                            1L,
                            1L,
                            null,
                            "Parent Module",
                            "Parent Description",
                            "parent",
                            ":parent",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"),
                            List.of(childResult));

            // When
            ModuleTreeApiResponse response = mapper.toTreeResponse(parentResult);

            // Then
            assertThat(response.moduleId()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Parent Module");
            assertThat(response.children()).hasSize(1);
            assertThat(response.children().get(0).moduleId()).isEqualTo(2L);
            assertThat(response.children().get(0).name()).isEqualTo("Child Module");
        }
    }
}
