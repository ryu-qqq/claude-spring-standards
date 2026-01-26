package com.ryuqq.application.classtypecategory.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategoryResult;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategorySliceResult;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.fixture.ClassTypeCategoryFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ClassTypeCategoryAssembler 단위 테스트")
class ClassTypeCategoryAssemblerTest {

    private ClassTypeCategoryAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeCategoryAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - ClassTypeCategory를 Result로 변환")
        void toResult_WithValidCategory_ShouldReturnResult() {
            // given
            ClassTypeCategory category = ClassTypeCategoryFixture.defaultExistingCategory();

            // when
            ClassTypeCategoryResult result = sut.toResult(category);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(category.id().value());
            assertThat(result.architectureId()).isEqualTo(category.architectureId().value());
            assertThat(result.code()).isEqualTo(category.code().value());
            assertThat(result.name()).isEqualTo(category.name().value());
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenHasNext_ShouldReturnSliceWithHasNextTrue() {
            // given
            ClassTypeCategory category1 = ClassTypeCategoryFixture.defaultExistingCategory();
            ClassTypeCategory category2 = ClassTypeCategoryFixture.applicationCategory();
            ClassTypeCategory category3 = ClassTypeCategoryFixture.categoryWithId(3L);
            List<ClassTypeCategory> categories = List.of(category1, category2, category3);
            int size = 2;

            // when
            ClassTypeCategorySliceResult result = sut.toSliceResult(categories, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isTrue();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            ClassTypeCategory category = ClassTypeCategoryFixture.defaultExistingCategory();
            List<ClassTypeCategory> categories = List.of(category);
            int size = 10;

            // when
            ClassTypeCategorySliceResult result = sut.toSliceResult(categories, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isFalse();
            assertThat(result.content()).hasSize(1);
        }
    }
}
