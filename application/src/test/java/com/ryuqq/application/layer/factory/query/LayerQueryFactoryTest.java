package com.ryuqq.application.layer.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.layer.dto.query.LayerSearchParams;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LayerQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("LayerQueryFactory 단위 테스트")
class LayerQueryFactoryTest {

    private LayerQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new LayerQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            LayerSearchParams searchParams = new LayerSearchParams(cursorParams, null, null, null);

            // when
            LayerSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
            assertThat(result.cursorPageRequest().cursor()).isNull();
        }

        @Test
        @DisplayName("성공 - 커서 기반 페이징 Criteria 생성")
        void createSliceCriteria_WithCursor_ShouldReturnCriteriaWithCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("100", 20);
            LayerSearchParams searchParams = new LayerSearchParams(cursorParams, null, null, null);

            // when
            LayerSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("성공 - ArchitectureIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithArchitectureIds_ShouldReturnCriteriaWithArchitectureIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            LayerSearchParams searchParams =
                    new LayerSearchParams(cursorParams, List.of(1L, 2L), null, null);

            // when
            LayerSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.architectureIds())
                    .extracting(ArchitectureId::value)
                    .containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - 검색 조건 포함 Criteria 생성")
        void createSliceCriteria_WithSearchCondition_ShouldReturnCriteriaWithSearch() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            LayerSearchParams searchParams =
                    new LayerSearchParams(cursorParams, null, "CODE", "DOMAIN");

            // when
            LayerSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo("CODE");
            assertThat(result.searchWord()).isEqualTo("DOMAIN");
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            LayerSearchParams searchParams =
                    new LayerSearchParams(cursorParams, List.of(1L), "NAME", "Application");

            // when
            LayerSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.architectureIds())
                    .extracting(ArchitectureId::value)
                    .containsExactly(1L);
            assertThat(result.searchField()).isEqualTo("NAME");
            assertThat(result.searchWord()).isEqualTo("Application");
        }

        @Test
        @DisplayName("성공 - 빈 ArchitectureIds 리스트로 Criteria 생성")
        void
                createSliceCriteria_WithEmptyArchitectureIds_ShouldReturnCriteriaWithoutArchitectureIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            LayerSearchParams searchParams =
                    new LayerSearchParams(cursorParams, List.of(), null, null);

            // when
            LayerSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.architectureIds()).isNull();
        }
    }
}
