package com.ryuqq.adapter.in.rest.packagepurpose.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreatePackagePurposeApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.CreatePackagePurposeApiRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * CreatePackagePurposeApiRequest Validation 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>@NotNull 검증
 *   <li>@NotBlank 검증
 *   <li>@Size 검증
 *   <li>에러 메시지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("CreatePackagePurposeApiRequest Validation 테스트")
class CreatePackagePurposeApiRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("structureId 검증")
    class StructureIdValidation {

        @Test
        @DisplayName("정상 값일 경우 검증 성공")
        void valid_ShouldPass() {
            // Given
            CreatePackagePurposeApiRequest request = CreatePackagePurposeApiRequestFixture.valid();

            // When
            Set<ConstraintViolation<CreatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("null일 경우 검증 실패")
        void null_ShouldFail() {
            // Given
            CreatePackagePurposeApiRequest request =
                    CreatePackagePurposeApiRequestFixture.invalidWithNullStructureId();

            // When
            Set<ConstraintViolation<CreatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("structureId는 필수입니다");
        }
    }

    @Nested
    @DisplayName("code 검증")
    class CodeValidation {

        @Test
        @DisplayName("정상 값일 경우 검증 성공")
        void valid_ShouldPass() {
            // Given
            CreatePackagePurposeApiRequest request = CreatePackagePurposeApiRequestFixture.valid();

            // When
            Set<ConstraintViolation<CreatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("빈 문자열일 경우 검증 실패")
        void blank_ShouldFail() {
            // Given
            CreatePackagePurposeApiRequest request =
                    CreatePackagePurposeApiRequestFixture.invalidWithBlankCode();

            // When
            Set<ConstraintViolation<CreatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("code는 필수입니다");
        }

        @Test
        @DisplayName("50자 초과일 경우 검증 실패")
        void tooLong_ShouldFail() {
            // Given
            CreatePackagePurposeApiRequest request =
                    CreatePackagePurposeApiRequestFixture.invalidWithLongCode();

            // When
            Set<ConstraintViolation<CreatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("code는 50자 이내여야 합니다");
        }
    }

    @Nested
    @DisplayName("name 검증")
    class NameValidation {

        @Test
        @DisplayName("정상 값일 경우 검증 성공")
        void valid_ShouldPass() {
            // Given
            CreatePackagePurposeApiRequest request = CreatePackagePurposeApiRequestFixture.valid();

            // When
            Set<ConstraintViolation<CreatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("빈 문자열일 경우 검증 실패")
        void blank_ShouldFail() {
            // Given
            CreatePackagePurposeApiRequest request =
                    CreatePackagePurposeApiRequestFixture.invalidWithBlankName();

            // When
            Set<ConstraintViolation<CreatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("name은 필수입니다");
        }

        @Test
        @DisplayName("100자 초과일 경우 검증 실패")
        void tooLong_ShouldFail() {
            // Given
            CreatePackagePurposeApiRequest request =
                    CreatePackagePurposeApiRequestFixture.invalidWithLongName();

            // When
            Set<ConstraintViolation<CreatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("name은 100자 이내여야 합니다");
        }
    }
}
