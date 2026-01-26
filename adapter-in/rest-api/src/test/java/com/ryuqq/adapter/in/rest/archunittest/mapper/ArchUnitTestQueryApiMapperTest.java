package com.ryuqq.adapter.in.rest.archunittest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.archunittest.dto.request.SearchArchUnitTestsCursorApiRequest;
import com.ryuqq.adapter.in.rest.archunittest.dto.response.ArchUnitTestApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchArchUnitTestsCursorApiRequestFixture;
import com.ryuqq.application.archunittest.dto.query.ArchUnitTestSearchParams;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestResult;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ArchUnitTestQueryApiMapper 단위 테스트
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
@DisplayName("ArchUnitTestQueryApiMapper 단위 테스트")
class ArchUnitTestQueryApiMapperTest {

    private ArchUnitTestQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArchUnitTestQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchArchUnitTestsCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchArchUnitTestsCursorApiRequest request =
                    SearchArchUnitTestsCursorApiRequestFixture.valid();

            // When
            ArchUnitTestSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.structureIds()).containsExactly(1L, 2L);
            assertThat(searchParams.searchField()).isEqualTo("CODE");
            assertThat(searchParams.searchWord()).isEqualTo("AGG-001");
            assertThat(searchParams.severities()).containsExactly("BLOCKER", "CRITICAL");
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchArchUnitTestsCursorApiRequest request =
                    new SearchArchUnitTestsCursorApiRequest(null, null, null, null, null, null);

            // When
            ArchUnitTestSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toResponse(ArchUnitTestResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new ArchUnitTestResult(
                            1L,
                            1L,
                            "ARCH-001",
                            "Lombok 사용 금지 테스트",
                            "Description",
                            "DomainLayerArchUnitTest",
                            "shouldNotUseLombok",
                            "testCode",
                            "BLOCKER",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            ArchUnitTestApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.archUnitTestId()).isEqualTo(1L);
            assertThat(response.structureId()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo("ARCH-001");
            assertThat(response.name()).isEqualTo("Lombok 사용 금지 테스트");
            assertThat(response.description()).isEqualTo("Description");
            assertThat(response.testClassName()).isEqualTo("DomainLayerArchUnitTest");
            assertThat(response.testMethodName()).isEqualTo("shouldNotUseLombok");
            assertThat(response.testCode()).isEqualTo("testCode");
            assertThat(response.severity()).isEqualTo("BLOCKER");
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<ArchUnitTestResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new ArchUnitTestResult(
                            1L,
                            1L,
                            "ARCH-001",
                            "Test 1",
                            "Description 1",
                            "TestClass1",
                            "testMethod1",
                            "code1",
                            "BLOCKER",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new ArchUnitTestResult(
                            2L,
                            1L,
                            "ARCH-002",
                            "Test 2",
                            "Description 2",
                            "TestClass2",
                            "testMethod2",
                            "code2",
                            "CRITICAL",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<ArchUnitTestApiResponse> responses = mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).archUnitTestId()).isEqualTo(1L);
            assertThat(responses.get(1).archUnitTestId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(ArchUnitTestSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new ArchUnitTestResult(
                            1L,
                            1L,
                            "ARCH-001",
                            "Test",
                            "Description",
                            "TestClass",
                            "testMethod",
                            "code",
                            "BLOCKER",
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = ArchUnitTestSliceResult.of(List.of(result), 20, true);

            // When
            SliceApiResponse<ArchUnitTestApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("1");
        }
    }
}
