package com.ryuqq.application.classtypecategory.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.classtypecategory.manager.ClassTypeCategoryReadManager;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.exception.ClassTypeCategoryDuplicateCodeException;
import com.ryuqq.domain.classtypecategory.exception.ClassTypeCategoryNotFoundException;
import com.ryuqq.domain.classtypecategory.fixture.ClassTypeCategoryFixture;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("ClassTypeCategoryValidator 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ClassTypeCategoryValidatorTest {

    @Mock private ClassTypeCategoryReadManager classTypeCategoryReadManager;

    private ClassTypeCategoryValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeCategoryValidator(classTypeCategoryReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 Category 조회")
        void findExistingOrThrow_WithExistingId_ShouldReturnCategory() {
            // given
            ClassTypeCategoryId categoryId = ClassTypeCategoryId.of(1L);
            ClassTypeCategory category = ClassTypeCategoryFixture.defaultExistingCategory();
            given(classTypeCategoryReadManager.findById(categoryId))
                    .willReturn(Optional.of(category));

            // when
            ClassTypeCategory result = sut.findExistingOrThrow(categoryId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Category")
        void findExistingOrThrow_WithNonExistingId_ShouldThrowException() {
            // given
            ClassTypeCategoryId categoryId = ClassTypeCategoryId.of(999L);
            given(classTypeCategoryReadManager.findById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(categoryId))
                    .isInstanceOf(ClassTypeCategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicated 메서드")
    class ValidateCodeNotDuplicated {

        @Test
        @DisplayName("성공 - 코드 중복 없음")
        void validateCodeNotDuplicated_WithUniqueCode_ShouldNotThrow() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            CategoryCode code = CategoryCode.of("UNIQUE_CODE");
            given(classTypeCategoryReadManager.existsByArchitectureIdAndCode(architectureId, code))
                    .willReturn(false);

            // when & then
            sut.validateCodeNotDuplicated(architectureId, code);
        }

        @Test
        @DisplayName("실패 - 코드 중복")
        void validateCodeNotDuplicated_WithDuplicateCode_ShouldThrowException() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            CategoryCode code = CategoryCode.of("DUPLICATE_CODE");
            given(classTypeCategoryReadManager.existsByArchitectureIdAndCode(architectureId, code))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateCodeNotDuplicated(architectureId, code))
                    .isInstanceOf(ClassTypeCategoryDuplicateCodeException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicatedExcluding 메서드")
    class ValidateCodeNotDuplicatedExcluding {

        @Test
        @DisplayName("성공 - 자신 제외 코드 중복 없음")
        void validateCodeNotDuplicatedExcluding_WithUniqueCode_ShouldNotThrow() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            CategoryCode code = CategoryCode.of("UNIQUE_CODE");
            ClassTypeCategoryId excludeId = ClassTypeCategoryId.of(1L);
            given(
                            classTypeCategoryReadManager.existsByArchitectureIdAndCodeAndIdNot(
                                    architectureId, code, excludeId))
                    .willReturn(false);

            // when & then
            sut.validateCodeNotDuplicatedExcluding(architectureId, code, excludeId);
        }

        @Test
        @DisplayName("실패 - 자신 제외 코드 중복")
        void validateCodeNotDuplicatedExcluding_WithDuplicateCode_ShouldThrowException() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            CategoryCode code = CategoryCode.of("DUPLICATE_CODE");
            ClassTypeCategoryId excludeId = ClassTypeCategoryId.of(1L);
            given(
                            classTypeCategoryReadManager.existsByArchitectureIdAndCodeAndIdNot(
                                    architectureId, code, excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateCodeNotDuplicatedExcluding(
                                            architectureId, code, excludeId))
                    .isInstanceOf(ClassTypeCategoryDuplicateCodeException.class);
        }
    }
}
