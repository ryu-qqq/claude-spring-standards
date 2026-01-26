package com.ryuqq.application.feedbackqueue.internal.validator.payload.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.convention.manager.ConventionReadManager;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CodingRulePayloadValidator 단위 테스트
 *
 * <p>void + exception 패턴 검증:
 *
 * <ul>
 *   <li>성공 시: 아무것도 반환하지 않음 (void)
 *   <li>실패 시: {@link InvalidFeedbackPayloadException} 예외 발생
 * </ul>
 *
 * @author ryu-qqq
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("CodingRulePayloadValidator 단위 테스트")
class CodingRulePayloadValidatorTest {

    @Mock private ConventionReadManager conventionReadManager;
    @Mock private CodingRuleReadManager codingRuleReadManager;
    @Mock private Convention convention;
    @Mock private CodingRule codingRule;

    private ObjectMapper objectMapper;
    private CodingRulePayloadValidator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        validator =
                new CodingRulePayloadValidator(
                        objectMapper, conventionReadManager, codingRuleReadManager);
    }

    @Test
    @DisplayName("supportedType() - CODING_RULE 반환")
    void supportedType() {
        // When
        FeedbackTargetType result = validator.supportedType();

        // Then
        assertThat(result).isEqualTo(FeedbackTargetType.CODING_RULE);
    }

    @Nested
    @DisplayName("ADD 검증")
    class ValidateAdd {

        @Test
        @DisplayName("성공 - 부모(Convention) 존재 시 예외 없이 완료")
        void success_whenParentExists() {
            // Given
            String payload =
                    """
                    {"conventionId": 1, "code": "DOM-001", "name": "Test Rule", \
                    "severity": "BLOCKER", "category": "ANNOTATION", \
                    "description": "desc", "rationale": "rationale"}
                    """;
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", null, "ADD", payload);

            when(conventionReadManager.findById(any(ConventionId.class)))
                    .thenReturn(Optional.of(convention));

            // When & Then - 예외 없이 완료
            assertDoesNotThrow(() -> validator.validate(command));
        }

        @Test
        @DisplayName("실패 - 부모(Convention) 없음")
        void failure_whenParentNotExists() {
            // Given
            String payload =
                    """
                    {"conventionId": 999, "code": "DOM-001", "name": "Test Rule", \
                    "severity": "BLOCKER", "category": "ANNOTATION", \
                    "description": "desc", "rationale": "rationale"}
                    """;
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", null, "ADD", payload);

            when(conventionReadManager.findById(any(ConventionId.class)))
                    .thenReturn(Optional.empty());

            // When & Then - InvalidFeedbackPayloadException 발생
            assertThatThrownBy(() -> validator.validate(command))
                    .isInstanceOf(InvalidFeedbackPayloadException.class)
                    .hasMessageContaining("Convention not found");
        }

        @Test
        @DisplayName("실패 - 잘못된 JSON 형식")
        void failure_invalidJson() {
            // Given
            String payload = "{ invalid json }";
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", null, "ADD", payload);

            // When & Then - InvalidFeedbackPayloadException 발생
            assertThatThrownBy(() -> validator.validate(command))
                    .isInstanceOf(InvalidFeedbackPayloadException.class)
                    .hasMessageContaining("Invalid payload format");
        }
    }

    @Nested
    @DisplayName("MODIFY 검증")
    class ValidateModify {

        @Test
        @DisplayName("성공 - 대상(CodingRule) 존재 시 예외 없이 완료")
        void success_whenTargetExists() {
            // Given
            String payload =
                    """
                    {"codingRuleId": 1, "name": "Updated Rule"}
                    """;
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", 1L, "MODIFY", payload);

            when(codingRuleReadManager.findById(any(CodingRuleId.class)))
                    .thenReturn(Optional.of(codingRule));

            // When & Then - 예외 없이 완료
            assertDoesNotThrow(() -> validator.validate(command));
        }

        @Test
        @DisplayName("실패 - 대상(CodingRule) 없음")
        void failure_whenTargetNotExists() {
            // Given
            String payload =
                    """
                    {"codingRuleId": 999, "name": "Updated Rule"}
                    """;
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", 999L, "MODIFY", payload);

            when(codingRuleReadManager.findById(any(CodingRuleId.class)))
                    .thenReturn(Optional.empty());

            // When & Then - InvalidFeedbackPayloadException 발생
            assertThatThrownBy(() -> validator.validate(command))
                    .isInstanceOf(InvalidFeedbackPayloadException.class)
                    .hasMessageContaining("CodingRule not found for modification");
        }

        @Test
        @DisplayName("실패 - 잘못된 JSON 형식")
        void failure_invalidJson() {
            // Given
            String payload = "not a json";
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", 1L, "MODIFY", payload);

            // When & Then - InvalidFeedbackPayloadException 발생
            assertThatThrownBy(() -> validator.validate(command))
                    .isInstanceOf(InvalidFeedbackPayloadException.class)
                    .hasMessageContaining("Invalid payload format");
        }
    }

    @Nested
    @DisplayName("DELETE 검증")
    class ValidateDelete {

        @Test
        @DisplayName("성공 - 대상(CodingRule) 존재 시 예외 없이 완료")
        void success_whenTargetExists() {
            // Given
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", 1L, "DELETE", null);

            when(codingRuleReadManager.findById(any(CodingRuleId.class)))
                    .thenReturn(Optional.of(codingRule));

            // When & Then - 예외 없이 완료
            assertDoesNotThrow(() -> validator.validate(command));
        }

        @Test
        @DisplayName("실패 - 대상(CodingRule) 없음")
        void failure_whenTargetNotExists() {
            // Given
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", 999L, "DELETE", null);

            when(codingRuleReadManager.findById(any(CodingRuleId.class)))
                    .thenReturn(Optional.empty());

            // When & Then - InvalidFeedbackPayloadException 발생
            assertThatThrownBy(() -> validator.validate(command))
                    .isInstanceOf(InvalidFeedbackPayloadException.class)
                    .hasMessageContaining("CodingRule not found for deletion");
        }

        @Test
        @DisplayName("실패 - targetId가 null")
        void failure_whenTargetIdNull() {
            // Given
            CreateFeedbackCommand command =
                    new CreateFeedbackCommand("CODING_RULE", null, "DELETE", null);

            // When & Then - InvalidFeedbackPayloadException 발생
            assertThatThrownBy(() -> validator.validate(command))
                    .isInstanceOf(InvalidFeedbackPayloadException.class)
                    .hasMessageContaining("Target ID is required");
        }
    }

    @Test
    @DisplayName("지원하지 않는 feedbackType")
    void unsupportedFeedbackType() {
        // Given
        CreateFeedbackCommand command =
                new CreateFeedbackCommand("CODING_RULE", 1L, "UNKNOWN", null);

        // When & Then - UNKNOWN은 FeedbackType enum에 없으므로 예외 발생
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class, () -> validator.validate(command));
    }
}
