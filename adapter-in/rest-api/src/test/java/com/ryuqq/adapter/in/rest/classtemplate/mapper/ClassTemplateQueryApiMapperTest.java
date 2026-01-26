package com.ryuqq.adapter.in.rest.classtemplate.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.classtemplate.dto.request.SearchClassTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.response.ClassTemplateApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchClassTemplatesCursorApiRequestFixture;
import com.ryuqq.application.classtemplate.dto.query.ClassTemplateSearchParams;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateResult;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ClassTemplateQueryApiMapper 단위 테스트
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
@DisplayName("ClassTemplateQueryApiMapper 단위 테스트")
class ClassTemplateQueryApiMapperTest {

    private ClassTemplateQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClassTemplateQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchClassTemplatesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchClassTemplatesCursorApiRequest request =
                    SearchClassTemplatesCursorApiRequestFixture.valid();

            // When
            ClassTemplateSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams()).isNotNull();
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchClassTemplatesCursorApiRequest request =
                    new SearchClassTemplatesCursorApiRequest(null, null, null, null);

            // When
            ClassTemplateSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("structureIds 파라미터가 있으면 SearchParams에 전달")
        void withStructureIds_ShouldPassStructureIdsToSearchParams() {
            // Given
            SearchClassTemplatesCursorApiRequest request =
                    SearchClassTemplatesCursorApiRequestFixture.validWithStructureIds();

            // When
            ClassTemplateSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.structureIds()).containsExactly(1L, 2L);
            assertThat(searchParams.hasStructureIds()).isTrue();
        }

        @Test
        @DisplayName("classTypeIds 파라미터가 있으면 SearchParams에 전달")
        void withClassTypeIds_ShouldPassClassTypeIdsToSearchParams() {
            // Given
            SearchClassTemplatesCursorApiRequest request =
                    SearchClassTemplatesCursorApiRequestFixture.validWithClassTypeIds();

            // When
            ClassTemplateSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.classTypeIds()).containsExactly(1L, 2L);
            assertThat(searchParams.hasClassTypeIds()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponse(ClassTemplateResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new ClassTemplateResult(
                            1L,
                            1L,
                            1L, // classTypeId (AGGREGATE)
                            "public class {ClassName} { ... }",
                            ".*Aggregate",
                            "Aggregate Root 클래스 템플릿",
                            List.of("@Entity"),
                            List.of("@Data"),
                            List.of(),
                            List.of(),
                            List.of(),
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            ClassTemplateApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.classTemplateId()).isEqualTo(1L);
            assertThat(response.structureId()).isEqualTo(1L);
            assertThat(response.classTypeId()).isEqualTo(1L);
            assertThat(response.templateCode()).isEqualTo("public class {ClassName} { ... }");
            assertThat(response.namingPattern()).isEqualTo(".*Aggregate");
            assertThat(response.description()).isEqualTo("Aggregate Root 클래스 템플릿");
            assertThat(response.requiredAnnotations()).containsExactly("@Entity");
            assertThat(response.forbiddenAnnotations()).containsExactly("@Data");
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<ClassTemplateResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new ClassTemplateResult(
                            1L,
                            1L,
                            1L, // classTypeId (AGGREGATE)
                            "code1",
                            null,
                            null,
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new ClassTemplateResult(
                            2L,
                            1L,
                            2L, // classTypeId (VALUE_OBJECT)
                            "code2",
                            null,
                            null,
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<ClassTemplateApiResponse> responses =
                    mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).classTemplateId()).isEqualTo(1L);
            assertThat(responses.get(1).classTemplateId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(ClassTemplateSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new ClassTemplateResult(
                            1L,
                            1L,
                            1L, // classTypeId (AGGREGATE)
                            "code",
                            null,
                            null,
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = ClassTemplateSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<ClassTemplateApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
        }
    }
}
