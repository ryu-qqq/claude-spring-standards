package com.ryuqq.adapter.in.rest.techstack.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchTechStacksCursorApiRequestFixture;
import com.ryuqq.adapter.in.rest.techstack.dto.request.SearchTechStacksCursorApiRequest;
import com.ryuqq.adapter.in.rest.techstack.dto.response.TechStackApiResponse;
import com.ryuqq.application.techstack.dto.query.TechStackSearchParams;
import com.ryuqq.application.techstack.dto.response.TechStackResult;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TechStackQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → SearchParams DTO 변환
 *   <li>Result DTO → Response DTO 변환
 *   <li>SliceResult → SliceApiResponse 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("TechStackQueryApiMapper 단위 테스트")
class TechStackQueryApiMapperTest {

    private TechStackQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TechStackQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchTechStacksCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchTechStacksCursorApiRequest request =
                    SearchTechStacksCursorApiRequestFixture.valid();

            // When
            TechStackSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams()).isNotNull();
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchTechStacksCursorApiRequest request =
                    new SearchTechStacksCursorApiRequest(null, null, null, null);

            // When
            TechStackSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams().size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toResponse(TechStackResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new TechStackResult(
                            1L,
                            "Spring Boot 3.5",
                            "ACTIVE",
                            "JAVA",
                            "21",
                            List.of("records"),
                            "SPRING_BOOT",
                            "3.5.0",
                            List.of("spring-web"),
                            "JVM",
                            "JVM",
                            "GRADLE",
                            "build.gradle",
                            List.of(),
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            TechStackApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Spring Boot 3.5");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.languageType()).isEqualTo("JAVA");
            assertThat(response.languageVersion()).isEqualTo("21");
            assertThat(response.languageFeatures()).containsExactly("records");
            assertThat(response.frameworkType()).isEqualTo("SPRING_BOOT");
            assertThat(response.frameworkVersion()).isEqualTo("3.5.0");
            assertThat(response.frameworkModules()).containsExactly("spring-web");
            assertThat(response.platformType()).isEqualTo("JVM");
            assertThat(response.runtimeEnvironment()).isEqualTo("JVM");
            assertThat(response.buildToolType()).isEqualTo("GRADLE");
            assertThat(response.buildConfigFile()).isEqualTo("build.gradle");
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<TechStackResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new TechStackResult(
                            1L,
                            "TechStack 1",
                            "ACTIVE",
                            "JAVA",
                            "21",
                            List.of(),
                            "SPRING_BOOT",
                            "3.5.0",
                            List.of(),
                            "JVM",
                            "JVM",
                            "GRADLE",
                            "build.gradle",
                            List.of(),
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new TechStackResult(
                            2L,
                            "TechStack 2",
                            "ACTIVE",
                            "KOTLIN",
                            "1.9",
                            List.of(),
                            "SPRING_BOOT",
                            "3.5.0",
                            List.of(),
                            "JVM",
                            "JVM",
                            "GRADLE",
                            "build.gradle",
                            List.of(),
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<TechStackApiResponse> responses = mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).id()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(TechStackSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new TechStackResult(
                            1L,
                            "TechStack",
                            "ACTIVE",
                            "JAVA",
                            "21",
                            List.of(),
                            "SPRING_BOOT",
                            "3.5.0",
                            List.of(),
                            "JVM",
                            "JVM",
                            "GRADLE",
                            "build.gradle",
                            List.of(),
                            false,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult =
                    new TechStackSliceResult(
                            List.of(result),
                            com.ryuqq.domain.common.vo.SliceMeta.withCursor(2L, 20, true, 1));

            // When
            SliceApiResponse<TechStackApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.hasNext()).isTrue();
        }
    }
}
