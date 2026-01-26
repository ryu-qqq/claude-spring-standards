package com.ryuqq.adapter.in.rest.resourcetemplate.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchResourceTemplatesCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.SearchResourceTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.response.ResourceTemplateApiResponse;
import com.ryuqq.application.resourcetemplate.dto.query.ResourceTemplateSearchParams;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateResult;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ResourceTemplateQueryApiMapper 단위 테스트
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
@DisplayName("ResourceTemplateQueryApiMapper 단위 테스트")
class ResourceTemplateQueryApiMapperTest {

    private ResourceTemplateQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ResourceTemplateQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchResourceTemplatesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            SearchResourceTemplatesCursorApiRequest request =
                    SearchResourceTemplatesCursorApiRequestFixture.valid();

            // When
            ResourceTemplateSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.moduleIds()).isNotNull();
            assertThat(searchParams.categories()).isNotNull();
            assertThat(searchParams.fileTypes()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResponse(ResourceTemplateResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new ResourceTemplateResult(
                            1L,
                            1L,
                            "DOMAIN",
                            "src/main/java/Order.java",
                            "JAVA",
                            "Order Aggregate",
                            "public class Order {}",
                            true,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            ResourceTemplateApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.resourceTemplateId()).isEqualTo(1L);
            assertThat(response.moduleId()).isEqualTo(1L);
            assertThat(response.category()).isEqualTo("DOMAIN");
            assertThat(response.filePath()).isEqualTo("src/main/java/Order.java");
            assertThat(response.fileType()).isEqualTo("JAVA");
            assertThat(response.description()).isEqualTo("Order Aggregate");
            assertThat(response.templateContent()).isEqualTo("public class Order {}");
            assertThat(response.required()).isTrue();
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<ResourceTemplateResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new ResourceTemplateResult(
                            1L,
                            1L,
                            "DOMAIN",
                            "path1",
                            "JAVA",
                            "Description 1",
                            "Content 1",
                            true,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new ResourceTemplateResult(
                            2L,
                            1L,
                            "APPLICATION",
                            "path2",
                            "JAVA",
                            "Description 2",
                            "Content 2",
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<ResourceTemplateApiResponse> responses =
                    mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).resourceTemplateId()).isEqualTo(1L);
            assertThat(responses.get(1).resourceTemplateId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(ResourceTemplateSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new ResourceTemplateResult(
                            1L,
                            1L,
                            "DOMAIN",
                            "path",
                            "JAVA",
                            "Description",
                            "Content",
                            true,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = ResourceTemplateSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<ResourceTemplateApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("1");
        }
    }
}
