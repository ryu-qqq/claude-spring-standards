package com.ryuqq.application.checklistitem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.factory.command.ChecklistItemCommandFactory;
import com.ryuqq.application.checklistitem.manager.ChecklistItemPersistenceManager;
import com.ryuqq.application.checklistitem.validator.ChecklistItemValidator;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
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
 * CreateChecklistItemService 단위 테스트
 *
 * <p>ChecklistItem 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateChecklistItemService 단위 테스트")
class CreateChecklistItemServiceTest {

    @Mock private ChecklistItemValidator checklistItemValidator;

    @Mock private ChecklistItemCommandFactory checklistItemCommandFactory;

    @Mock private ChecklistItemPersistenceManager checklistItemPersistenceManager;

    @Mock private ChecklistItem checklistItem;

    private CreateChecklistItemService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateChecklistItemService(
                        checklistItemValidator,
                        checklistItemCommandFactory,
                        checklistItemPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ChecklistItem 생성")
        void execute_WithValidCommand_ShouldCreateChecklistItem() {
            // given
            CreateChecklistItemCommand command = createDefaultCommand();
            CodingRuleId ruleId = CodingRuleId.of(command.ruleId());
            ChecklistItemId savedId = ChecklistItemId.of(1L);

            willDoNothing()
                    .given(checklistItemValidator)
                    .validateSequenceOrderNotDuplicate(ruleId, command.sequenceOrder());
            given(checklistItemCommandFactory.create(command)).willReturn(checklistItem);
            given(checklistItemPersistenceManager.persist(checklistItem)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(checklistItemValidator)
                    .should()
                    .validateSequenceOrderNotDuplicate(ruleId, command.sequenceOrder());
            then(checklistItemCommandFactory).should().create(command);
            then(checklistItemPersistenceManager).should().persist(checklistItem);
        }

        @Test
        @DisplayName("실패 - 중복된 순서인 경우")
        void execute_WhenSequenceDuplicate_ShouldThrowException() {
            // given
            CreateChecklistItemCommand command = createDefaultCommand();
            CodingRuleId ruleId = CodingRuleId.of(command.ruleId());

            willThrow(new IllegalArgumentException("Duplicate sequence order"))
                    .given(checklistItemValidator)
                    .validateSequenceOrderNotDuplicate(ruleId, command.sequenceOrder());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);

            then(checklistItemCommandFactory).shouldHaveNoInteractions();
            then(checklistItemPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateChecklistItemCommand createDefaultCommand() {
        return new CreateChecklistItemCommand(
                1L, 1, "Check for Lombok usage", "AUTOMATED", "Checkstyle", "CS-001", true);
    }
}
