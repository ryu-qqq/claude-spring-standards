package com.ryuqq.application.classtypecategory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.classtypecategory.assembler.ClassTypeCategoryAssembler;
import com.ryuqq.application.classtypecategory.dto.query.ClassTypeCategorySearchParams;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategoryResult;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategorySliceResult;
import com.ryuqq.application.classtypecategory.factory.query.ClassTypeCategoryQueryFactory;
import com.ryuqq.application.classtypecategory.manager.ClassTypeCategoryReadManager;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.fixture.ClassTypeCategoryFixture;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.common.vo.SliceMeta;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchClassTypeCategoriesByCursorService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SearchClassTypeCategoriesByCursorServiceTest {

    @Mock private ClassTypeCategoryQueryFactory classTypeCategoryQueryFactory;

    @Mock private ClassTypeCategoryReadManager classTypeCategoryReadManager;

    @Mock private ClassTypeCategoryAssembler classTypeCategoryAssembler;

    private SearchClassTypeCategoriesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchClassTypeCategoriesByCursorService(
                        classTypeCategoryQueryFactory,
                        classTypeCategoryReadManager,
                        classTypeCategoryAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 커서 기반 Category 검색")
        void execute_WithValidParams_ShouldReturnSliceResult() {
            // given
            ClassTypeCategorySearchParams params =
                    ClassTypeCategorySearchParams.of(null, null, null, null, null, 20);
            ClassTypeCategorySliceCriteria criteria =
                    ClassTypeCategorySliceCriteria.of(
                            CursorPageRequest.first(20), null, null, null, null);
            ClassTypeCategory category = ClassTypeCategoryFixture.defaultExistingCategory();
            List<ClassTypeCategory> categories = List.of(category);

            ClassTypeCategoryResult categoryResult =
                    new ClassTypeCategoryResult(1L, 1L, "DOMAIN", "도메인 레이어", "설명", 1, null, null);
            ClassTypeCategorySliceResult sliceResult =
                    new ClassTypeCategorySliceResult(
                            List.of(categoryResult), SliceMeta.of(20, false, 1));

            given(classTypeCategoryQueryFactory.createSliceCriteria(params)).willReturn(criteria);
            given(classTypeCategoryReadManager.findBySliceCriteria(any())).willReturn(categories);
            given(classTypeCategoryAssembler.toSliceResult(categories, 20)).willReturn(sliceResult);

            // when
            ClassTypeCategorySliceResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            then(classTypeCategoryQueryFactory).should().createSliceCriteria(params);
            then(classTypeCategoryReadManager).should().findBySliceCriteria(any());
            then(classTypeCategoryAssembler).should().toSliceResult(categories, 20);
        }

        @Test
        @DisplayName("성공 - 빈 결과")
        void execute_WithNoResult_ShouldReturnEmptySlice() {
            // given
            ClassTypeCategorySearchParams params =
                    ClassTypeCategorySearchParams.of(null, null, null, null, null, 20);
            ClassTypeCategorySliceCriteria criteria =
                    ClassTypeCategorySliceCriteria.of(
                            CursorPageRequest.first(20), null, null, null, null);
            List<ClassTypeCategory> categories = List.of();
            ClassTypeCategorySliceResult sliceResult =
                    new ClassTypeCategorySliceResult(List.of(), SliceMeta.empty(20));

            given(classTypeCategoryQueryFactory.createSliceCriteria(params)).willReturn(criteria);
            given(classTypeCategoryReadManager.findBySliceCriteria(any())).willReturn(categories);
            given(classTypeCategoryAssembler.toSliceResult(categories, 20)).willReturn(sliceResult);

            // when
            ClassTypeCategorySliceResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
        }
    }
}
