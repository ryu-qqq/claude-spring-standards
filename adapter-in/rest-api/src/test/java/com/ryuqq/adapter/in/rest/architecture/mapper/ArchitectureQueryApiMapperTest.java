package com.ryuqq.adapter.in.rest.architecture.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.architecture.dto.request.SearchArchitecturesCursorApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.response.ArchitectureApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchArchitecturesCursorApiRequestFixture;
import com.ryuqq.application.architecture.dto.query.ArchitectureSearchParams;
import com.ryuqq.application.architecture.dto.response.ArchitectureResult;
import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ArchitectureQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → SearchParams 변환
 *   <li>Result DTO → Response DTO 변환
 *   <li>SliceResult → SliceApiResponse 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ArchitectureQueryApiMapper 단위 테스트")
class ArchitectureQueryApiMapperTest {

    private ArchitectureQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArchitectureQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchArchitecturesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchArchitecturesCursorApiRequest request =
                    SearchArchitecturesCursorApiRequestFixture.valid();

            // When
            ArchitectureSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams()).isNotNull();
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchArchitecturesCursorApiRequest request =
                    new SearchArchitecturesCursorApiRequest(null, null, Collections.emptyList());

            // When
            ArchitectureSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("techStackIds가 있으면 필터에 포함")
        void withTechStackIds_ShouldIncludeInFilter() {
            // Given
            SearchArchitecturesCursorApiRequest request =
                    SearchArchitecturesCursorApiRequestFixture.validWithTechStackIds(
                            List.of(1L, 2L));

            // When
            ArchitectureSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.techStackIds()).containsExactly(1L, 2L);
        }
    }

    @Nested
    @DisplayName("toResponse(ArchitectureResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new ArchitectureResult(
                            1L,
                            1L,
                            "Hexagonal Architecture",
                            "HEXAGONAL",
                            "Ports and Adapters 패턴",
                            List.of("의존성 역전", "계층 분리"),
                            List.of(),
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            ArchitectureApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.techStackId()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Hexagonal Architecture");
            assertThat(response.patternType()).isEqualTo("HEXAGONAL");
            assertThat(response.patternDescription()).isEqualTo("Ports and Adapters 패턴");
            assertThat(response.patternPrinciples()).containsExactly("의존성 역전", "계층 분리");
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<ArchitectureResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new ArchitectureResult(
                            1L,
                            1L,
                            "Architecture 1",
                            "HEXAGONAL",
                            "Description 1",
                            List.of(),
                            List.of(),
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new ArchitectureResult(
                            2L,
                            1L,
                            "Architecture 2",
                            "LAYERED",
                            "Description 2",
                            List.of(),
                            List.of(),
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<ArchitectureApiResponse> responses = mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).id()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(ArchitectureSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new ArchitectureResult(
                            1L,
                            1L,
                            "Architecture",
                            "HEXAGONAL",
                            "Description",
                            List.of(),
                            List.of(),
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult =
                    new ArchitectureSliceResult(
                            List.of(result),
                            com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, true, 1));

            // When
            SliceApiResponse<ArchitectureApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.hasNext()).isTrue();
        }
    }
}
