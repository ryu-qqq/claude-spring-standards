package com.ryuqq.adapter.in.rest.packagepurpose.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.UpdatePackagePurposeApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.UpdatePackagePurposeApiRequest;
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
 * UpdatePackagePurposeApiRequest Validation 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>@NotBlank 검증
 *   <li>@Size 검증
 *   <li>에러 메시지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("UpdatePackagePurposeApiRequest Validation 테스트")
class UpdatePackagePurposeApiRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("code 검증")
    class CodeValidation {

        @Test
        @DisplayName("정상 값일 경우 검증 성공")
        void valid_ShouldPass() {
            // Given
            UpdatePackagePurposeApiRequest request = UpdatePackagePurposeApiRequestFixture.valid();

            // When
            Set<ConstraintViolation<UpdatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("빈 문자열일 경우 검증 실패")
        void blank_ShouldFail() {
            // Given
            UpdatePackagePurposeApiRequest request =
                    UpdatePackagePurposeApiRequestFixture.invalidWithBlankCode();

            // When
            Set<ConstraintViolation<UpdatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("code는 필수입니다");
        }

        @Test
        @DisplayName("50자 초과일 경우 검증 실패")
        void tooLong_ShouldFail() {
            // Given
            UpdatePackagePurposeApiRequest request =
                    UpdatePackagePurposeApiRequestFixture.invalidWithLongCode();

            // When
            Set<ConstraintViolation<UpdatePackagePurposeApiRequest>> violations =
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
            UpdatePackagePurposeApiRequest request = UpdatePackagePurposeApiRequestFixture.valid();

            // When
            Set<ConstraintViolation<UpdatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("빈 문자열일 경우 검증 실패")
        void blank_ShouldFail() {
            // Given
            UpdatePackagePurposeApiRequest request =
                    UpdatePackagePurposeApiRequestFixture.invalidWithBlankName();

            // When
            Set<ConstraintViolation<UpdatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("name은 필수입니다");
        }

        @Test
        @DisplayName("100자 초과일 경우 검증 실패")
        void tooLong_ShouldFail() {
            // Given
            UpdatePackagePurposeApiRequest request =
                    UpdatePackagePurposeApiRequestFixture.invalidWithLongName();

            // When
            Set<ConstraintViolation<UpdatePackagePurposeApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("name은 100자 이내여야 합니다");
        }
    }
}
