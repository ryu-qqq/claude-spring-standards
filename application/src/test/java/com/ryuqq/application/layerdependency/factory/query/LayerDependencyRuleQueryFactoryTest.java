package com.ryuqq.application.layerdependency.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.layerdependency.dto.query.LayerDependencyRuleSearchParams;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.query.LayerDependencyRuleSliceCriteria;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerDependencyRuleSearchField;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LayerDependencyRuleQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("LayerDependencyRuleQueryFactory 단위 테스트")
class LayerDependencyRuleQueryFactoryTest {

    private LayerDependencyRuleQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new LayerDependencyRuleQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            LayerDependencyRuleSearchParams searchParams =
                    LayerDependencyRuleSearchParams.of(cursorParams);

            // when
            LayerDependencyRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            LayerDependencyRuleSearchParams searchParams =
                    LayerDependencyRuleSearchParams.of(cursorParams);

            // when
            LayerDependencyRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - ArchitectureIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithArchitectureIds_ShouldReturnCriteriaWithArchitectureIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            LayerDependencyRuleSearchParams searchParams =
                    LayerDependencyRuleSearchParams.of(
                            cursorParams, List.of(1L, 2L), null, null, null);

            // when
            LayerDependencyRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.architectureIds())
                    .extracting(ArchitectureId::value)
                    .containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - DependencyTypes 필터 포함 Criteria 생성")
        void createSliceCriteria_WithDependencyTypes_ShouldReturnCriteriaWithDependencyTypes() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            LayerDependencyRuleSearchParams searchParams =
                    LayerDependencyRuleSearchParams.of(
                            cursorParams, null, List.of("ALLOWED", "FORBIDDEN"), null, null);

            // when
            LayerDependencyRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.dependencyTypes())
                    .containsExactlyInAnyOrder(DependencyType.ALLOWED, DependencyType.FORBIDDEN);
        }

        @Test
        @DisplayName("성공 - 검색 조건 포함 Criteria 생성")
        void createSliceCriteria_WithSearch_ShouldReturnCriteriaWithSearch() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            LayerDependencyRuleSearchParams searchParams =
                    LayerDependencyRuleSearchParams.of(
                            cursorParams, null, null, "CONDITION_DESCRIPTION", "Port");

            // when
            LayerDependencyRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField())
                    .isEqualTo(LayerDependencyRuleSearchField.CONDITION_DESCRIPTION);
            assertThat(result.searchWord()).isEqualTo("Port");
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            LayerDependencyRuleSearchParams searchParams =
                    LayerDependencyRuleSearchParams.of(
                            cursorParams,
                            List.of(1L),
                            List.of("CONDITIONAL"),
                            "CONDITION_DESCRIPTION",
                            "Port");

            // when
            LayerDependencyRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.architectureIds()).hasSize(1);
            assertThat(result.dependencyTypes()).containsExactly(DependencyType.CONDITIONAL);
            assertThat(result.searchField())
                    .isEqualTo(LayerDependencyRuleSearchField.CONDITION_DESCRIPTION);
            assertThat(result.searchWord()).isEqualTo("Port");
        }
    }

    @Nested
    @DisplayName("toArchitectureId 메서드")
    class ToArchitectureId {

        @Test
        @DisplayName("성공 - Long을 ArchitectureId로 변환")
        void toArchitectureId_WithValidId_ShouldReturnArchitectureId() {
            // given
            Long architectureId = 1L;

            // when
            ArchitectureId result = sut.toArchitectureId(architectureId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(architectureId);
        }

        @Test
        @DisplayName("성공 - null ID는 null 반환")
        void toArchitectureId_WithNullId_ShouldReturnNull() {
            // given
            Long architectureId = null;

            // when
            ArchitectureId result = sut.toArchitectureId(architectureId);

            // then
            assertThat(result).isNull();
        }
    }
}
