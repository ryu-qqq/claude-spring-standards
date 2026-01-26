package com.ryuqq.application.classtypecategory.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.classtypecategory.dto.query.ClassTypeCategorySearchParams;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ClassTypeCategoryQueryFactory 단위 테스트")
class ClassTypeCategoryQueryFactoryTest {

    private ClassTypeCategoryQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeCategoryQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            ClassTypeCategorySearchParams searchParams =
                    ClassTypeCategorySearchParams.of(null, null, null, null, null, 20);

            // when
            ClassTypeCategorySliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
            assertThat(result.cursorPageRequest().cursor()).isNull();
        }

        @Test
        @DisplayName("성공 - 아키텍처 필터 포함 Criteria 생성")
        void createSliceCriteria_WithArchitectureIds_ShouldReturnCriteriaWithArchitectureIds() {
            // given
            ClassTypeCategorySearchParams searchParams =
                    ClassTypeCategorySearchParams.of(null, List.of(1L, 2L), null, null, null, 20);

            // when
            ClassTypeCategorySliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.architectureIds()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 커서 기반 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithCursor_ShouldReturnCriteriaWithCursor() {
            // given
            ClassTypeCategorySearchParams searchParams =
                    ClassTypeCategorySearchParams.of(null, null, null, null, 10L, 20);

            // when
            ClassTypeCategorySliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(10L);
        }

        @Test
        @DisplayName("성공 - 모든 필터 조건 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            ClassTypeCategorySearchParams searchParams =
                    ClassTypeCategorySearchParams.of(
                            List.of(1L, 2L), List.of(1L), "CODE", "DOMAIN", 5L, 10);

            // when
            ClassTypeCategorySliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.ids()).hasSize(2);
            assertThat(result.architectureIds()).hasSize(1);
            assertThat(result.searchField()).isEqualTo("CODE");
            assertThat(result.searchWord()).isEqualTo("DOMAIN");
        }
    }
}
