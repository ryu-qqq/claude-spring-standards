package com.ryuqq.domain.packagepurpose.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.packagepurpose.fixture.PackagePurposeExceptionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PackagePurposeDuplicateCodeException 단위 테스트
 *
 * <p>테스트 전략:
 *
 * <ul>
 *   <li>DomainException 상속 검증
 *   <li>생성자 파라미터 테스트
 *   <li>에러 코드 매핑 검증
 *   <li>args 매핑 검증
 *   <li>에러 메시지 컨텍스트 정보 포함 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("domain")
@Tag("exception")
@DisplayName("PackagePurposeDuplicateCodeException 단위 테스트")
class PackagePurposeDuplicateCodeExceptionTest {

    @Nested
    @DisplayName("DomainException 상속 검증")
    class DomainExceptionInheritanceTests {

        @Test
        @DisplayName("PackagePurposeDuplicateCodeException는 DomainException을 상속해야 한다")
        void shouldExtendDomainException() {
            // Given
            PackagePurposeDuplicateCodeException exception =
                    PackagePurposeExceptionFixture.duplicateCode();

            // Then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTests {

        @Test
        @DisplayName("생성자 - structureId와 code로 예외 생성")
        void constructor_WithStructureIdAndCode_ShouldCreateException() {
            // Given
            Long structureId = 1L;
            String code = "AGGREGATE";

            // When
            PackagePurposeDuplicateCodeException exception =
                    new PackagePurposeDuplicateCodeException(structureId, code);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains(String.valueOf(structureId));
            assertThat(exception.getMessage()).contains(code);
        }
    }

    @Nested
    @DisplayName("에러 코드 매핑 검증")
    class ErrorCodeMappingTests {

        @Test
        @DisplayName("code()는 PACKAGE_PURPOSE_DUPLICATE_CODE를 반환해야 한다")
        void code_ShouldReturnPackagePurposeDuplicateCode() {
            // Given
            PackagePurposeDuplicateCodeException exception =
                    PackagePurposeExceptionFixture.duplicateCode();

            // When
            String code = exception.code();

            // Then
            assertThat(code).isEqualTo("PACKAGE_PURPOSE-002");
        }

        @Test
        @DisplayName("httpStatus()는 409를 반환해야 한다")
        void httpStatus_ShouldReturn409() {
            // Given
            PackagePurposeDuplicateCodeException exception =
                    PackagePurposeExceptionFixture.duplicateCode();

            // When
            int httpStatus = exception.httpStatus();

            // Then
            assertThat(httpStatus).isEqualTo(409);
        }

        @Test
        @DisplayName("getErrorCode()는 PACKAGE_PURPOSE_DUPLICATE_CODE를 반환해야 한다")
        void getErrorCode_ShouldReturnPackagePurposeDuplicateCode() {
            // Given
            PackagePurposeDuplicateCodeException exception =
                    PackagePurposeExceptionFixture.duplicateCode();

            // When
            var errorCode = exception.getErrorCode();

            // Then
            assertThat(errorCode).isEqualTo(PackagePurposeErrorCode.PACKAGE_PURPOSE_DUPLICATE_CODE);
        }
    }

    @Nested
    @DisplayName("args 매핑 검증")
    class ArgsMappingTests {

        @Test
        @DisplayName("args()는 structureId와 code를 포함해야 한다")
        void args_ShouldContainStructureIdAndCode() {
            // Given
            Long structureId = 1L;
            String code = "AGGREGATE";
            PackagePurposeDuplicateCodeException exception =
                    new PackagePurposeDuplicateCodeException(structureId, code);

            // When
            var args = exception.args();

            // Then
            assertThat(args).containsKey("structureId");
            assertThat(args.get("structureId")).isEqualTo(structureId);
            assertThat(args).containsKey("code");
            assertThat(args.get("code")).isEqualTo(code);
        }

        @Test
        @DisplayName("args()는 불변 Map이어야 한다")
        void args_ShouldBeImmutable() {
            // Given
            PackagePurposeDuplicateCodeException exception =
                    PackagePurposeExceptionFixture.duplicateCode();

            // When
            var args = exception.args();

            // Then
            assertThat(args).isNotNull();
            // 불변 Map이므로 수정 시도 시 예외 발생
            try {
                args.put("test", "value");
                throw new AssertionError("args should be immutable");
            } catch (UnsupportedOperationException e) {
                // 예상된 동작
            }
        }
    }

    @Nested
    @DisplayName("에러 메시지 검증")
    class ErrorMessageTests {

        @Test
        @DisplayName("예외 메시지는 structureId와 code를 포함해야 한다")
        void exceptionMessage_ShouldContainStructureIdAndCode() {
            // Given
            Long structureId = 2L;
            String code = "SERVICE";
            PackagePurposeDuplicateCodeException exception =
                    new PackagePurposeDuplicateCodeException(structureId, code);

            // When
            String message = exception.getMessage();

            // Then
            assertThat(message).contains("already exists");
            assertThat(message).contains(String.valueOf(structureId));
            assertThat(message).contains(code);
        }
    }
}
