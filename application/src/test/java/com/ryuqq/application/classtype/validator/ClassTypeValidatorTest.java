package com.ryuqq.application.classtype.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.classtype.manager.ClassTypeReadManager;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.exception.ClassTypeDuplicateCodeException;
import com.ryuqq.domain.classtype.exception.ClassTypeNotFoundException;
import com.ryuqq.domain.classtype.fixture.ClassTypeFixture;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
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
@DisplayName("ClassTypeValidator 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ClassTypeValidatorTest {

    @Mock private ClassTypeReadManager classTypeReadManager;

    private ClassTypeValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTypeValidator(classTypeReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 ClassType 조회")
        void findExistingOrThrow_WithExistingId_ShouldReturnClassType() {
            // given
            ClassTypeId classTypeId = ClassTypeId.of(1L);
            ClassType classType = ClassTypeFixture.defaultExistingClassType();
            given(classTypeReadManager.findById(classTypeId)).willReturn(Optional.of(classType));

            // when
            ClassType result = sut.findExistingOrThrow(classTypeId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ClassType")
        void findExistingOrThrow_WithNonExistingId_ShouldThrowException() {
            // given
            ClassTypeId classTypeId = ClassTypeId.of(999L);
            given(classTypeReadManager.findById(classTypeId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(classTypeId))
                    .isInstanceOf(ClassTypeNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicated 메서드")
    class ValidateCodeNotDuplicated {

        @Test
        @DisplayName("성공 - 코드 중복 없음")
        void validateCodeNotDuplicated_WithUniqueCode_ShouldNotThrow() {
            // given
            ClassTypeCategoryId categoryId = ClassTypeCategoryId.of(1L);
            ClassTypeCode code = ClassTypeCode.of("UNIQUE_CODE");
            given(classTypeReadManager.existsByCategoryIdAndCode(categoryId, code))
                    .willReturn(false);

            // when & then
            sut.validateCodeNotDuplicated(categoryId, code);
            // 예외 없이 통과
        }

        @Test
        @DisplayName("실패 - 코드 중복")
        void validateCodeNotDuplicated_WithDuplicateCode_ShouldThrowException() {
            // given
            ClassTypeCategoryId categoryId = ClassTypeCategoryId.of(1L);
            ClassTypeCode code = ClassTypeCode.of("DUPLICATE_CODE");
            given(classTypeReadManager.existsByCategoryIdAndCode(categoryId, code))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateCodeNotDuplicated(categoryId, code))
                    .isInstanceOf(ClassTypeDuplicateCodeException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicatedExcluding 메서드")
    class ValidateCodeNotDuplicatedExcluding {

        @Test
        @DisplayName("성공 - 자신 제외 코드 중복 없음")
        void validateCodeNotDuplicatedExcluding_WithUniqueCode_ShouldNotThrow() {
            // given
            ClassTypeCategoryId categoryId = ClassTypeCategoryId.of(1L);
            ClassTypeCode code = ClassTypeCode.of("UNIQUE_CODE");
            ClassTypeId excludeId = ClassTypeId.of(1L);
            given(
                            classTypeReadManager.existsByCategoryIdAndCodeAndIdNot(
                                    categoryId, code, excludeId))
                    .willReturn(false);

            // when & then
            sut.validateCodeNotDuplicatedExcluding(categoryId, code, excludeId);
            // 예외 없이 통과
        }

        @Test
        @DisplayName("실패 - 자신 제외 코드 중복")
        void validateCodeNotDuplicatedExcluding_WithDuplicateCode_ShouldThrowException() {
            // given
            ClassTypeCategoryId categoryId = ClassTypeCategoryId.of(1L);
            ClassTypeCode code = ClassTypeCode.of("DUPLICATE_CODE");
            ClassTypeId excludeId = ClassTypeId.of(1L);
            given(
                            classTypeReadManager.existsByCategoryIdAndCodeAndIdNot(
                                    categoryId, code, excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateCodeNotDuplicatedExcluding(
                                            categoryId, code, excludeId))
                    .isInstanceOf(ClassTypeDuplicateCodeException.class);
        }
    }
}
