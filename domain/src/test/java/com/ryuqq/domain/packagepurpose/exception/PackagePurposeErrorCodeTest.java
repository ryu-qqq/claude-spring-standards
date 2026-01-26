package com.ryuqq.domain.packagepurpose.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * PackagePurposeErrorCode Enum 단위 테스트
 *
 * <p>테스트 전략:
 *
 * <ul>
 *   <li>ErrorCode 인터페이스 구현 검증
 *   <li>에러 코드 형식 검증 (PACKAGE_PURPOSE-{3자리 숫자})
 *   <li>HTTP 상태 코드 매핑 검증
 *   <li>에러 메시지 null 체크
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("domain")
@Tag("exception")
@DisplayName("PackagePurposeErrorCode Enum 단위 테스트")
class PackagePurposeErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 검증")
    class ErrorCodeInterfaceTests {

        @ParameterizedTest
        @EnumSource(PackagePurposeErrorCode.class)
        @DisplayName("모든 ErrorCode는 getCode()를 구현해야 한다")
        void allErrorCodes_ShouldImplementGetCode(PackagePurposeErrorCode errorCode) {
            // When
            String code = errorCode.getCode();

            // Then
            assertThat(code).isNotNull();
            assertThat(code).isNotBlank();
        }

        @ParameterizedTest
        @EnumSource(PackagePurposeErrorCode.class)
        @DisplayName("모든 ErrorCode는 getHttpStatus()를 구현해야 한다")
        void allErrorCodes_ShouldImplementGetHttpStatus(PackagePurposeErrorCode errorCode) {
            // When
            int httpStatus = errorCode.getHttpStatus();

            // Then
            assertThat(httpStatus).isBetween(100, 599); // HTTP 상태 코드 범위
        }

        @ParameterizedTest
        @EnumSource(PackagePurposeErrorCode.class)
        @DisplayName("모든 ErrorCode는 getMessage()를 구현해야 한다")
        void allErrorCodes_ShouldImplementGetMessage(PackagePurposeErrorCode errorCode) {
            // When
            String message = errorCode.getMessage();

            // Then
            assertThat(message).isNotNull();
            assertThat(message).isNotBlank();
        }

        @ParameterizedTest
        @EnumSource(PackagePurposeErrorCode.class)
        @DisplayName("모든 ErrorCode는 ErrorCode 인터페이스를 구현해야 한다")
        void allErrorCodes_ShouldImplementErrorCodeInterface(PackagePurposeErrorCode errorCode) {
            // Then
            assertThat(errorCode).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("에러 코드 형식 검증 (PACKAGE_PURPOSE-{3자리 숫자})")
    class CodeFormatTests {

        @ParameterizedTest
        @EnumSource(PackagePurposeErrorCode.class)
        @DisplayName("에러 코드는 PACKAGE_PURPOSE-{3자리 숫자} 형식이어야 한다")
        void errorCode_ShouldFollowNamingConvention(PackagePurposeErrorCode errorCode) {
            // When
            String code = errorCode.getCode();

            // Then - 형식: PACKAGE_PURPOSE-{3자리 숫자}
            assertThat(code).matches("^PACKAGE_PURPOSE-\\d{3}$");
        }

        @Test
        @DisplayName("PACKAGE_PURPOSE_NOT_FOUND는 'PACKAGE_PURPOSE-001' 형식이어야 한다")
        void packagePurposeNotFound_ShouldHaveCorrectCodeFormat() {
            // When
            String code = PackagePurposeErrorCode.PACKAGE_PURPOSE_NOT_FOUND.getCode();

            // Then
            assertThat(code).isEqualTo("PACKAGE_PURPOSE-001");
        }

        @Test
        @DisplayName("PACKAGE_PURPOSE_DUPLICATE_CODE는 'PACKAGE_PURPOSE-002' 형식이어야 한다")
        void packagePurposeDuplicateCode_ShouldHaveCorrectCodeFormat() {
            // When
            String code = PackagePurposeErrorCode.PACKAGE_PURPOSE_DUPLICATE_CODE.getCode();

            // Then
            assertThat(code).isEqualTo("PACKAGE_PURPOSE-002");
        }
    }

    @Nested
    @DisplayName("HTTP 상태 코드 매핑 검증")
    class HttpStatusMappingTests {

        @Test
        @DisplayName("PACKAGE_PURPOSE_NOT_FOUND는 404 NOT FOUND를 반환해야 한다")
        void notFound_ShouldReturn404() {
            // When
            int httpStatus = PackagePurposeErrorCode.PACKAGE_PURPOSE_NOT_FOUND.getHttpStatus();

            // Then
            assertThat(httpStatus).isEqualTo(404);
        }

        @Test
        @DisplayName("PACKAGE_PURPOSE_DUPLICATE_CODE는 409 CONFLICT를 반환해야 한다")
        void duplicateCode_ShouldReturn409() {
            // When
            int httpStatus = PackagePurposeErrorCode.PACKAGE_PURPOSE_DUPLICATE_CODE.getHttpStatus();

            // Then
            assertThat(httpStatus).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("에러 메시지 검증")
    class ErrorMessageTests {

        @ParameterizedTest
        @EnumSource(PackagePurposeErrorCode.class)
        @DisplayName("에러 메시지는 null이 아니어야 한다")
        void errorMessage_ShouldNotBeNull(PackagePurposeErrorCode errorCode) {
            // When
            String message = errorCode.getMessage();

            // Then
            assertThat(message).isNotNull();
        }

        @Test
        @DisplayName("PACKAGE_PURPOSE_NOT_FOUND 메시지는 'not found'를 포함해야 한다")
        void notFoundMessage_ShouldContainNotFound() {
            // When
            String message = PackagePurposeErrorCode.PACKAGE_PURPOSE_NOT_FOUND.getMessage();

            // Then
            assertThat(message.toLowerCase()).contains("not found");
        }

        @Test
        @DisplayName("PACKAGE_PURPOSE_DUPLICATE_CODE 메시지는 'already exists'를 포함해야 한다")
        void duplicateCodeMessage_ShouldContainAlreadyExists() {
            // When
            String message = PackagePurposeErrorCode.PACKAGE_PURPOSE_DUPLICATE_CODE.getMessage();

            // Then
            assertThat(message.toLowerCase()).contains("already exists");
        }
    }
}
