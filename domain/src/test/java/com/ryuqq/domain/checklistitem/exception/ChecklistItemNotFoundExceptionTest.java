package com.ryuqq.domain.checklistitem.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.domain.checklistitem.fixture.ChecklistItemExceptionFixture;
import com.ryuqq.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ChecklistItemNotFoundException 단위 테스트
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
@DisplayName("ChecklistItemNotFoundException 단위 테스트")
class ChecklistItemNotFoundExceptionTest {

    @Nested
    @DisplayName("DomainException 상속 검증")
    class DomainExceptionInheritanceTests {

        @Test
        @DisplayName("ChecklistItemNotFoundException는 DomainException을 상속해야 한다")
        void shouldExtendDomainException() {
            // Given
            ChecklistItemNotFoundException exception = ChecklistItemExceptionFixture.notFound();

            // Then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTests {

        @Test
        @DisplayName("생성자 - checklistItemId로 예외 생성")
        void constructor_WithChecklistItemId_ShouldCreateException() {
            // Given
            Long checklistItemId = 1L;

            // When
            ChecklistItemNotFoundException exception =
                    new ChecklistItemNotFoundException(checklistItemId);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains(String.valueOf(checklistItemId));
        }
    }

    @Nested
    @DisplayName("에러 코드 매핑 검증")
    class ErrorCodeMappingTests {

        @Test
        @DisplayName("code()는 CHECKLIST_ITEM_NOT_FOUND를 반환해야 한다")
        void code_ShouldReturnChecklistItemNotFound() {
            // Given
            ChecklistItemNotFoundException exception = ChecklistItemExceptionFixture.notFound();

            // When
            String code = exception.code();

            // Then
            assertThat(code).isEqualTo("CHECKLIST_ITEM-001");
        }

        @Test
        @DisplayName("httpStatus()는 404를 반환해야 한다")
        void httpStatus_ShouldReturn404() {
            // Given
            ChecklistItemNotFoundException exception = ChecklistItemExceptionFixture.notFound();

            // When
            int httpStatus = exception.httpStatus();

            // Then
            assertThat(httpStatus).isEqualTo(404);
        }

        @Test
        @DisplayName("getErrorCode()는 CHECKLIST_ITEM_NOT_FOUND를 반환해야 한다")
        void getErrorCode_ShouldReturnChecklistItemNotFound() {
            // Given
            ChecklistItemNotFoundException exception = ChecklistItemExceptionFixture.notFound();

            // When
            var errorCode = exception.getErrorCode();

            // Then
            assertThat(errorCode).isEqualTo(ChecklistItemErrorCode.CHECKLIST_ITEM_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("args 매핑 검증")
    class ArgsMappingTests {

        @Test
        @DisplayName("args()는 checklistItemId를 포함해야 한다")
        void args_ShouldContainChecklistItemId() {
            // Given
            Long checklistItemId = 1L;
            ChecklistItemNotFoundException exception =
                    new ChecklistItemNotFoundException(checklistItemId);

            // When
            var args = exception.args();

            // Then
            assertThat(args).containsKey("checklistItemId");
            assertThat(args.get("checklistItemId")).isEqualTo(checklistItemId);
        }

        @Test
        @DisplayName("args()는 불변 Map이어야 한다")
        void args_ShouldBeImmutable() {
            // Given
            ChecklistItemNotFoundException exception = ChecklistItemExceptionFixture.notFound();

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
        @DisplayName("예외 메시지는 checklistItemId를 포함해야 한다")
        void exceptionMessage_ShouldContainChecklistItemId() {
            // Given
            Long checklistItemId = 123L;
            ChecklistItemNotFoundException exception =
                    new ChecklistItemNotFoundException(checklistItemId);

            // When
            String message = exception.getMessage();

            // Then
            assertThat(message).contains("ChecklistItem not found");
            assertThat(message).contains(String.valueOf(checklistItemId));
        }
    }
}
