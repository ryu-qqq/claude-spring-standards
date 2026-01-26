package com.ryuqq.application.checklistitem.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.checklistitem.factory.command.ChecklistItemCommandFactory;
import com.ryuqq.application.checklistitem.manager.ChecklistItemPersistenceManager;
import com.ryuqq.application.checklistitem.validator.ChecklistItemValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItemUpdateData;
import com.ryuqq.domain.checklistitem.exception.ChecklistItemNotFoundException;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateChecklistItemService 단위 테스트
 *
 * <p>ChecklistItem 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateChecklistItemService 단위 테스트")
class UpdateChecklistItemServiceTest {

    @Mock private ChecklistItemValidator checklistItemValidator;

    @Mock private ChecklistItemCommandFactory checklistItemCommandFactory;

    @Mock private ChecklistItemPersistenceManager checklistItemPersistenceManager;

    @Mock private ChecklistItem checklistItem;

    @Mock private ChecklistItemUpdateData updateData;

    private UpdateChecklistItemService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateChecklistItemService(
                        checklistItemValidator,
                        checklistItemCommandFactory,
                        checklistItemPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ChecklistItem 수정")
        void execute_WithValidCommand_ShouldUpdateChecklistItem() {
            // given
            UpdateChecklistItemCommand command = createDefaultCommand();
            ChecklistItemId checklistItemId = ChecklistItemId.of(command.checklistItemId());
            Instant changedAt = Instant.now();
            UpdateContext<ChecklistItemId, ChecklistItemUpdateData> context =
                    new UpdateContext<>(checklistItemId, updateData, changedAt);

            given(checklistItemCommandFactory.createUpdateContext(command)).willReturn(context);
            given(checklistItemValidator.findExistingOrThrow(checklistItemId))
                    .willReturn(checklistItem);
            willDoNothing().given(checklistItem).update(updateData, changedAt);
            given(checklistItemPersistenceManager.persist(checklistItem))
                    .willReturn(checklistItemId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(checklistItemCommandFactory).should().createUpdateContext(command);
            then(checklistItemValidator).should().findExistingOrThrow(checklistItemId);
            then(checklistItem).should().update(updateData, changedAt);
            then(checklistItemPersistenceManager).should().persist(checklistItem);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ChecklistItem인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateChecklistItemCommand command = createDefaultCommand();
            ChecklistItemId checklistItemId = ChecklistItemId.of(command.checklistItemId());
            Instant changedAt = Instant.now();
            UpdateContext<ChecklistItemId, ChecklistItemUpdateData> context =
                    new UpdateContext<>(checklistItemId, updateData, changedAt);

            given(checklistItemCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new ChecklistItemNotFoundException(checklistItemId.value()))
                    .given(checklistItemValidator)
                    .findExistingOrThrow(checklistItemId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ChecklistItemNotFoundException.class);

            then(checklistItemPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateChecklistItemCommand createDefaultCommand() {
        return new UpdateChecklistItemCommand(
                1L, 2, "Updated check description", "MANUAL", "Checkstyle", "CS-001", true);
    }
}
