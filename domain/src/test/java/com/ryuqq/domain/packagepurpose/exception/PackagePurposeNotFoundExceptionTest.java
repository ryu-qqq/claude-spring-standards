package com.ryuqq.domain.packagepurpose.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.packagepurpose.fixture.PackagePurposeExceptionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PackagePurposeNotFoundException 단위 테스트
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
@DisplayName("PackagePurposeNotFoundException 단위 테스트")
class PackagePurposeNotFoundExceptionTest {

    @Nested
    @DisplayName("DomainException 상속 검증")
    class DomainExceptionInheritanceTests {

        @Test
        @DisplayName("PackagePurposeNotFoundException는 DomainException을 상속해야 한다")
        void shouldExtendDomainException() {
            // Given
            PackagePurposeNotFoundException exception = PackagePurposeExceptionFixture.notFound();

            // Then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTests {

        @Test
        @DisplayName("생성자 - packagePurposeId로 예외 생성")
        void constructor_WithPackagePurposeId_ShouldCreateException() {
            // Given
            Long packagePurposeId = 1L;

            // When
            PackagePurposeNotFoundException exception =
                    new PackagePurposeNotFoundException(packagePurposeId);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains(String.valueOf(packagePurposeId));
        }
    }

    @Nested
    @DisplayName("에러 코드 매핑 검증")
    class ErrorCodeMappingTests {

        @Test
        @DisplayName("code()는 PACKAGE_PURPOSE_NOT_FOUND를 반환해야 한다")
        void code_ShouldReturnPackagePurposeNotFound() {
            // Given
            PackagePurposeNotFoundException exception = PackagePurposeExceptionFixture.notFound();

            // When
            String code = exception.code();

            // Then
            assertThat(code).isEqualTo("PACKAGE_PURPOSE-001");
        }

        @Test
        @DisplayName("httpStatus()는 404를 반환해야 한다")
        void httpStatus_ShouldReturn404() {
            // Given
            PackagePurposeNotFoundException exception = PackagePurposeExceptionFixture.notFound();

            // When
            int httpStatus = exception.httpStatus();

            // Then
            assertThat(httpStatus).isEqualTo(404);
        }

        @Test
        @DisplayName("getErrorCode()는 PACKAGE_PURPOSE_NOT_FOUND를 반환해야 한다")
        void getErrorCode_ShouldReturnPackagePurposeNotFound() {
            // Given
            PackagePurposeNotFoundException exception = PackagePurposeExceptionFixture.notFound();

            // When
            var errorCode = exception.getErrorCode();

            // Then
            assertThat(errorCode).isEqualTo(PackagePurposeErrorCode.PACKAGE_PURPOSE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("args 매핑 검증")
    class ArgsMappingTests {

        @Test
        @DisplayName("args()는 packagePurposeId를 포함해야 한다")
        void args_ShouldContainPackagePurposeId() {
            // Given
            Long packagePurposeId = 1L;
            PackagePurposeNotFoundException exception =
                    new PackagePurposeNotFoundException(packagePurposeId);

            // When
            var args = exception.args();

            // Then
            assertThat(args).containsKey("packagePurposeId");
            assertThat(args.get("packagePurposeId")).isEqualTo(packagePurposeId);
        }

        @Test
        @DisplayName("args()는 불변 Map이어야 한다")
        void args_ShouldBeImmutable() {
            // Given
            PackagePurposeNotFoundException exception = PackagePurposeExceptionFixture.notFound();

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
        @DisplayName("예외 메시지는 packagePurposeId를 포함해야 한다")
        void exceptionMessage_ShouldContainPackagePurposeId() {
            // Given
            Long packagePurposeId = 123L;
            PackagePurposeNotFoundException exception =
                    new PackagePurposeNotFoundException(packagePurposeId);

            // When
            String message = exception.getMessage();

            // Then
            assertThat(message).contains("PackagePurpose not found");
            assertThat(message).contains(String.valueOf(packagePurposeId));
        }
    }
}
