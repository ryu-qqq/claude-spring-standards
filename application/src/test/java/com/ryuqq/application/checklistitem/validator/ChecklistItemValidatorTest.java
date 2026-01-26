package com.ryuqq.application.checklistitem.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.checklistitem.manager.ChecklistItemReadManager;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.fixture.ChecklistItemFixture;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ChecklistItemValidator 단위 테스트
 *
 * <p>ChecklistItem 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("ChecklistItemValidator 단위 테스트")
class ChecklistItemValidatorTest {

    @Mock private ChecklistItemReadManager checklistItemReadManager;

    private ChecklistItemValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ChecklistItemValidator(checklistItemReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 ChecklistItem 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnChecklistItem() {
            // given
            ChecklistItemId id = ChecklistItemId.of(1L);
            ChecklistItem expected = ChecklistItemFixture.reconstitute();

            given(checklistItemReadManager.getById(id)).willReturn(expected);

            // when
            ChecklistItem result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("validateSequenceOrderNotDuplicate 메서드")
    class ValidateSequenceOrderNotDuplicate {

        @Test
        @DisplayName("성공 - 중복되지 않는 순서")
        void validateSequenceOrderNotDuplicate_WhenNotDuplicate_ShouldNotThrow() {
            // given
            CodingRuleId ruleId = CodingRuleId.of(1L);
            int sequenceOrder = 1;

            given(checklistItemReadManager.existsByRuleIdAndSequenceOrder(ruleId, sequenceOrder))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateSequenceOrderNotDuplicate(ruleId, sequenceOrder))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 중복된 순서인 경우 예외")
        void validateSequenceOrderNotDuplicate_WhenDuplicate_ShouldThrowException() {
            // given
            CodingRuleId ruleId = CodingRuleId.of(1L);
            int sequenceOrder = 1;

            given(checklistItemReadManager.existsByRuleIdAndSequenceOrder(ruleId, sequenceOrder))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateSequenceOrderNotDuplicate(ruleId, sequenceOrder))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }
}
