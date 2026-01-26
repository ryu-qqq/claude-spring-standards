package com.ryuqq.adapter.in.rest.packagestructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchPackageStructuresCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagestructure.dto.request.SearchPackageStructuresCursorApiRequest;
import com.ryuqq.adapter.in.rest.packagestructure.dto.response.PackageStructureApiResponse;
import com.ryuqq.application.packagestructure.dto.query.PackageStructureSearchParams;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureResult;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PackageStructureQueryApiMapper 단위 테스트
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
@DisplayName("PackageStructureQueryApiMapper 단위 테스트")
class PackageStructureQueryApiMapperTest {

    private PackageStructureQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PackageStructureQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchPackageStructuresCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            SearchPackageStructuresCursorApiRequest request =
                    SearchPackageStructuresCursorApiRequestFixture.valid();

            // When
            PackageStructureSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams()).isNotNull();
        }

        @Test
        @DisplayName("moduleIds 파라미터가 있으면 SearchParams에 전달")
        void withModuleIds_ShouldPassModuleIdsToSearchParams() {
            // Given
            SearchPackageStructuresCursorApiRequest request =
                    SearchPackageStructuresCursorApiRequestFixture.validWithModuleIds();

            // When
            PackageStructureSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.moduleIds()).containsExactly(1L, 2L);
            assertThat(searchParams.hasModuleIds()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponse(PackageStructureResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new PackageStructureResult(
                            1L,
                            1L,
                            "{base}.domain.{bc}.aggregate",
                            List.of("CLASS", "RECORD"),
                            ".*Aggregate",
                            "Aggregate",
                            "Aggregate Root 패키지",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            PackageStructureApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.packageStructureId()).isEqualTo(1L);
            assertThat(response.moduleId()).isEqualTo(1L);
            assertThat(response.pathPattern()).isEqualTo("{base}.domain.{bc}.aggregate");
            assertThat(response.allowedClassTypes()).containsExactly("CLASS", "RECORD");
            assertThat(response.namingPattern()).isEqualTo(".*Aggregate");
            assertThat(response.namingSuffix()).isEqualTo("Aggregate");
            assertThat(response.description()).isEqualTo("Aggregate Root 패키지");
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<PackageStructureResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new PackageStructureResult(
                            1L,
                            1L,
                            "path1",
                            List.of("CLASS"),
                            "pattern1",
                            "suffix1",
                            "description1",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new PackageStructureResult(
                            2L,
                            1L,
                            "path2",
                            List.of("RECORD"),
                            "pattern2",
                            "suffix2",
                            "description2",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<PackageStructureApiResponse> responses =
                    mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).packageStructureId()).isEqualTo(1L);
            assertThat(responses.get(1).packageStructureId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(PackageStructureSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new PackageStructureResult(
                            1L,
                            1L,
                            "path",
                            List.of("CLASS"),
                            "pattern",
                            "suffix",
                            "description",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = PackageStructureSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<PackageStructureApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("1");
        }
    }
}
