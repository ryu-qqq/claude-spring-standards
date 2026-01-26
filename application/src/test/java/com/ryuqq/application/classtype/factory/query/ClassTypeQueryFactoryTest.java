package com.ryuqq.application.classtype.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.classtype.dto.query.ClassTypeSearchParams;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ClassTypeQueryFactory 단위 테스트")
class ClassTypeQueryFactoryTest {

    private ClassTypeQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            ClassTypeSearchParams searchParams =
                    ClassTypeSearchParams.of(null, null, null, null, null, null, 20);

            // when
            ClassTypeSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
            assertThat(result.cursorPageRequest().cursor()).isNull();
        }

        @Test
        @DisplayName("성공 - 카테고리 필터 포함 Criteria 생성")
        void createSliceCriteria_WithCategoryIds_ShouldReturnCriteriaWithCategoryIds() {
            // given
            ClassTypeSearchParams searchParams =
                    ClassTypeSearchParams.of(null, List.of(1L, 2L), null, null, null, null, 20);

            // when
            ClassTypeSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.categoryIds()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 커서 기반 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithCursor_ShouldReturnCriteriaWithCursor() {
            // given
            ClassTypeSearchParams searchParams =
                    ClassTypeSearchParams.of(null, null, null, null, null, 10L, 20);

            // when
            ClassTypeSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(10L);
        }

        @Test
        @DisplayName("성공 - 모든 필터 조건 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            ClassTypeSearchParams searchParams =
                    ClassTypeSearchParams.of(
                            List.of(1L, 2L), List.of(1L), List.of(1L), "CODE", "AGGREGATE", 5L, 10);

            // when
            ClassTypeSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.ids()).hasSize(2);
            assertThat(result.categoryIds()).hasSize(1);
            assertThat(result.architectureIds()).hasSize(1);
            assertThat(result.searchField()).isEqualTo("CODE");
            assertThat(result.searchWord()).isEqualTo("AGGREGATE");
        }
    }
}
