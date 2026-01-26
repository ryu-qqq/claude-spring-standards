package com.ryuqq.adapter.in.rest.codingrule.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.codingrule.dto.request.CodingRuleIndexApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.request.SearchCodingRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleApiResponse;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleIndexApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.CodingRuleIndexApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.SearchCodingRulesCursorApiRequestFixture;
import com.ryuqq.application.codingrule.dto.query.CodingRuleIndexSearchParams;
import com.ryuqq.application.codingrule.dto.query.CodingRuleSearchParams;
import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * CodingRuleQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Query DTO 변환
 *   <li>Result DTO → Response DTO 변환
 *   <li>SliceResult → SliceApiResponse 변환
 *   <li>null 처리 및 기본값 설정
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("CodingRuleQueryApiMapper 단위 테스트")
class CodingRuleQueryApiMapperTest {

    private CodingRuleQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CodingRuleQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchCodingRulesCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchCodingRulesCursorApiRequest request =
                    SearchCodingRulesCursorApiRequestFixture.valid();

            // When
            CodingRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams()).isNotNull();
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchCodingRulesCursorApiRequest request =
                    new SearchCodingRulesCursorApiRequest(null, null, null, null, null, null);

            // When
            CodingRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursorParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("categories 파라미터가 있으면 SearchParams에 전달")
        void withCategories_ShouldPassCategoriesToSearchParams() {
            // Given
            SearchCodingRulesCursorApiRequest request =
                    SearchCodingRulesCursorApiRequestFixture.validWithCategories();

            // When
            CodingRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.categories()).containsExactly("ANNOTATION", "BEHAVIOR");
            assertThat(searchParams.hasCategories()).isTrue();
        }

        @Test
        @DisplayName("severities 파라미터가 있으면 SearchParams에 전달")
        void withSeverities_ShouldPassSeveritiesToSearchParams() {
            // Given
            SearchCodingRulesCursorApiRequest request =
                    SearchCodingRulesCursorApiRequestFixture.validWithSeverities();

            // When
            CodingRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.severities()).containsExactly("BLOCKER", "CRITICAL");
            assertThat(searchParams.hasSeverities()).isTrue();
        }

        @Test
        @DisplayName("searchField와 searchWord가 있으면 SearchParams에 전달")
        void withSearch_ShouldPassSearchToSearchParams() {
            // Given
            SearchCodingRulesCursorApiRequest request =
                    SearchCodingRulesCursorApiRequestFixture.validWithSearch();

            // When
            CodingRuleSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.searchField()).isEqualTo("CODE");
            assertThat(searchParams.searchWord()).isEqualTo("CTR-001");
            assertThat(searchParams.hasSearch()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponse(CodingRuleResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Lombok 사용 금지",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            "Rationale",
                            false,
                            List.of("AGGREGATE", "VALUE_OBJECT"),
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            CodingRuleApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.codingRuleId()).isEqualTo(1L);
            assertThat(response.conventionId()).isEqualTo(1L);
            assertThat(response.structureId()).isNull();
            assertThat(response.code()).isEqualTo("AGG-001");
            assertThat(response.name()).isEqualTo("Lombok 사용 금지");
            assertThat(response.severity()).isEqualTo("BLOCKER");
            assertThat(response.category()).isEqualTo("ANNOTATION");
            assertThat(response.description()).isEqualTo("Description");
            assertThat(response.rationale()).isEqualTo("Rationale");
            assertThat(response.autoFixable()).isFalse();
            assertThat(response.appliesTo()).containsExactly("AGGREGATE", "VALUE_OBJECT");
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }

        @Test
        @DisplayName("빈 리스트 처리")
        void emptyAppliesTo_ShouldHandleGracefully() {
            // Given
            var result =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Name",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            null,
                            false,
                            List.of(),
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            CodingRuleApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.appliesTo()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toResponses(List<CodingRuleResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Name 1",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description 1",
                            null,
                            false,
                            List.of(),
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new CodingRuleResult(
                            2L,
                            1L,
                            null,
                            "AGG-002",
                            "Name 2",
                            RuleSeverity.CRITICAL,
                            RuleCategory.BEHAVIOR,
                            "Description 2",
                            null,
                            false,
                            List.of(),
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<CodingRuleApiResponse> responses = mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).code()).isEqualTo("AGG-001");
            assertThat(responses.get(1).code()).isEqualTo("AGG-002");
        }
    }

    @Nested
    @DisplayName("toSliceResponse(CodingRuleSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Name",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            null,
                            false,
                            List.of(),
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = CodingRuleSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<CodingRuleApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("1");
        }

        @Test
        @DisplayName("다음 페이지 없음")
        void withoutNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new CodingRuleResult(
                            1L,
                            1L,
                            null,
                            "AGG-001",
                            "Name",
                            RuleSeverity.BLOCKER,
                            RuleCategory.ANNOTATION,
                            "Description",
                            null,
                            false,
                            List.of(),
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = CodingRuleSliceResult.of(List.of(result), false);

            // When
            SliceApiResponse<CodingRuleApiResponse> response = mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }
    }

    @Nested
    @DisplayName("toIndexSearchParams(CodingRuleIndexApiRequest)")
    class ToIndexSearchParams {

        @Test
        @DisplayName("전체 조회 요청 변환")
        void allRequest_ShouldMapCorrectly() {
            // Given
            CodingRuleIndexApiRequest request = CodingRuleIndexApiRequestFixture.valid();

            // When
            CodingRuleIndexSearchParams searchParams = mapper.toIndexSearchParams(request);

            // Then
            assertThat(searchParams.conventionId()).isNull();
            assertThat(searchParams.severities()).isNull();
            assertThat(searchParams.categories()).isNull();
        }

        @Test
        @DisplayName("conventionId 필터 변환")
        void withConventionId_ShouldPassToSearchParams() {
            // Given
            CodingRuleIndexApiRequest request =
                    CodingRuleIndexApiRequestFixture.withConventionId(1L);

            // When
            CodingRuleIndexSearchParams searchParams = mapper.toIndexSearchParams(request);

            // Then
            assertThat(searchParams.conventionId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("severities 필터 변환")
        void withSeverities_ShouldPassToSearchParams() {
            // Given
            CodingRuleIndexApiRequest request =
                    CodingRuleIndexApiRequestFixture.withSeverities(List.of("BLOCKER", "CRITICAL"));

            // When
            CodingRuleIndexSearchParams searchParams = mapper.toIndexSearchParams(request);

            // Then
            assertThat(searchParams.severities()).containsExactly("BLOCKER", "CRITICAL");
            assertThat(searchParams.hasSeverities()).isTrue();
        }

        @Test
        @DisplayName("categories 필터 변환")
        void withCategories_ShouldPassToSearchParams() {
            // Given
            CodingRuleIndexApiRequest request =
                    CodingRuleIndexApiRequestFixture.withCategories(
                            List.of("ANNOTATION", "STRUCTURE"));

            // When
            CodingRuleIndexSearchParams searchParams = mapper.toIndexSearchParams(request);

            // Then
            assertThat(searchParams.categories()).containsExactly("ANNOTATION", "STRUCTURE");
            assertThat(searchParams.hasCategories()).isTrue();
        }
    }

    @Nested
    @DisplayName("toIndexResponse(CodingRuleIndexItem)")
    class ToIndexResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validItem_ShouldMapAllFields() {
            // Given
            var item = CodingRuleIndexItem.of("AGG-001", "Lombok 사용 금지", "BLOCKER", "ANNOTATION");

            // When
            CodingRuleIndexApiResponse response = mapper.toIndexResponse(item);

            // Then
            assertThat(response.code()).isEqualTo("AGG-001");
            assertThat(response.name()).isEqualTo("Lombok 사용 금지");
            assertThat(response.severity()).isEqualTo("BLOCKER");
            assertThat(response.category()).isEqualTo("ANNOTATION");
        }
    }

    @Nested
    @DisplayName("toIndexResponses(List<CodingRuleIndexItem>)")
    class ToIndexResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var item1 = CodingRuleIndexItem.of("AGG-001", "Rule 1", "BLOCKER", "ANNOTATION");
            var item2 = CodingRuleIndexItem.of("AGG-002", "Rule 2", "CRITICAL", "BEHAVIOR");
            var items = List.of(item1, item2);

            // When
            List<CodingRuleIndexApiResponse> responses = mapper.toIndexResponses(items);

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).code()).isEqualTo("AGG-001");
            assertThat(responses.get(1).code()).isEqualTo("AGG-002");
        }

        @Test
        @DisplayName("빈 목록 처리")
        void emptyList_ShouldReturnEmptyList() {
            // Given
            List<CodingRuleIndexItem> items = List.of();

            // When
            List<CodingRuleIndexApiResponse> responses = mapper.toIndexResponses(items);

            // Then
            assertThat(responses).isEmpty();
        }
    }
}
