package com.ryuqq.adapter.in.rest.packagepurpose.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.fixture.request.SearchPackagePurposesCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.SearchPackagePurposesCursorApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.response.PackagePurposeApiResponse;
import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeResult;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PackagePurposeQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Long → Query 변환
 *   <li>Result → Response 변환
 *   <li>날짜 포맷팅 (ISO 8601)
 *   <li>리스트 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("PackagePurposeQueryApiMapper 단위 테스트")
class PackagePurposeQueryApiMapperTest {

    private PackagePurposeQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PackagePurposeQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchPackagePurposesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            SearchPackagePurposesCursorApiRequest request =
                    SearchPackagePurposesCursorApiRequestFixture.valid();

            // When
            PackagePurposeSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams()).isNotNull();
        }

        @Test
        @DisplayName("structureIds 파라미터가 있으면 SearchParams에 전달")
        void withStructureIds_ShouldPassStructureIdsToSearchParams() {
            // Given
            SearchPackagePurposesCursorApiRequest request =
                    SearchPackagePurposesCursorApiRequestFixture.validWithStructureIds();

            // When
            PackagePurposeSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.structureIds()).containsExactly(1L, 2L);
            assertThat(searchParams.hasStructureIds()).isTrue();
        }

        @Test
        @DisplayName("searchField와 searchWord 파라미터가 있으면 SearchParams에 전달")
        void withSearch_ShouldPassSearchToSearchParams() {
            // Given
            SearchPackagePurposesCursorApiRequest request =
                    SearchPackagePurposesCursorApiRequestFixture.validWithSearch();

            // When
            PackagePurposeSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.searchField()).isEqualTo("CODE");
            assertThat(searchParams.searchWord()).isEqualTo("AGGREGATE");
            assertThat(searchParams.hasSearch()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponse(PackagePurposeResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑 및 날짜 포맷팅")
        void validResult_ShouldMapAllFields() {
            // Given
            Instant createdAt = Instant.parse("2024-01-01T09:00:00+09:00");
            Instant updatedAt = Instant.parse("2024-01-02T00:00:00Z");
            Long structureId = 1L;
            PackagePurposeResult result =
                    new PackagePurposeResult(
                            1L,
                            structureId,
                            "AGGREGATE",
                            "Aggregate Root",
                            "DDD Aggregate Root 패키지",
                            List.of("CLASS", "RECORD"),
                            "^[A-Z][a-zA-Z0-9]*$",
                            "Aggregate",
                            createdAt,
                            updatedAt);

            // When
            PackagePurposeApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.packagePurposeId()).isEqualTo(1L);
            assertThat(response.structureId()).isEqualTo(structureId);
            assertThat(response.code()).isEqualTo("AGGREGATE");
            assertThat(response.name()).isEqualTo("Aggregate Root");
            assertThat(response.description()).isEqualTo("DDD Aggregate Root 패키지");
            assertThat(response.defaultAllowedClassTypes()).containsExactly("CLASS", "RECORD");
            assertThat(response.defaultNamingPattern()).isEqualTo("^[A-Z][a-zA-Z0-9]*$");
            assertThat(response.defaultNamingSuffix()).isEqualTo("Aggregate");
            assertThat(response.createdAt())
                    .isEqualTo(DateTimeFormatUtils.formatIso8601(createdAt));
            assertThat(response.updatedAt())
                    .isEqualTo(DateTimeFormatUtils.formatIso8601(updatedAt));
        }

        @Test
        @DisplayName("null 필드 처리 - description, defaultAllowedClassTypes 등 null 허용")
        void nullFields_ShouldHandleGracefully() {
            // Given
            Instant now = Instant.now();
            Long structureId = 1L;
            PackagePurposeResult result =
                    new PackagePurposeResult(
                            1L,
                            structureId,
                            "VALUE_OBJECT",
                            "Value Object",
                            null,
                            null,
                            null,
                            null,
                            now,
                            now);

            // When
            PackagePurposeApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.description()).isNull();
            assertThat(response.defaultAllowedClassTypes()).isNull();
            assertThat(response.defaultNamingPattern()).isNull();
            assertThat(response.defaultNamingSuffix()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponses(List<PackagePurposeResult>)")
    class ToResponses {

        @Test
        @DisplayName("정상 변환 - 리스트 변환")
        void validList_ShouldMapAllItems() {
            // Given
            Instant now = Instant.now();
            Long structureId = 1L;
            List<PackagePurposeResult> results =
                    List.of(
                            new PackagePurposeResult(
                                    1L,
                                    structureId,
                                    "AGGREGATE",
                                    "Aggregate Root",
                                    null,
                                    null,
                                    null,
                                    null,
                                    now,
                                    now),
                            new PackagePurposeResult(
                                    2L,
                                    structureId,
                                    "VALUE_OBJECT",
                                    "Value Object",
                                    null,
                                    null,
                                    null,
                                    null,
                                    now,
                                    now));

            // When
            List<PackagePurposeApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).packagePurposeId()).isEqualTo(1L);
            assertThat(responses.get(1).packagePurposeId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 리스트 처리")
        void emptyList_ShouldReturnEmptyList() {
            // Given
            List<PackagePurposeResult> results = List.of();

            // When
            List<PackagePurposeApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSliceResponse(PackagePurposeSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new PackagePurposeResult(
                            1L,
                            1L,
                            "AGGREGATE",
                            "Aggregate",
                            "Description",
                            List.of("CLASS"),
                            "pattern",
                            "suffix",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = PackagePurposeSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<PackagePurposeApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("1");
        }
    }
}
