package com.ryuqq.application.checklistitem.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItemUpdateData;
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
 * ChecklistItemCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ChecklistItemCommandFactory 단위 테스트")
class ChecklistItemCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ChecklistItemCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ChecklistItemCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateChecklistItemCommand로 ChecklistItem 생성")
        void create_WithValidCommand_ShouldReturnChecklistItem() {
            // given
            CreateChecklistItemCommand command =
                    new CreateChecklistItemCommand(
                            1L, 1, "Lombok 사용 금지", "MANUAL", null, null, true);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ChecklistItem result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.ruleId().value()).isEqualTo(command.ruleId());
            assertThat(result.sequenceOrder().value()).isEqualTo(command.sequenceOrder());
            assertThat(result.checkDescription().value()).isEqualTo(command.checkDescription());
            assertThat(result.checkType().name()).isEqualTo(command.checkType());
            assertThat(result.isCritical()).isEqualTo(command.critical());
        }

        @Test
        @DisplayName("성공 - 자동화 도구가 있는 ChecklistItem 생성")
        void create_WithAutomationTool_ShouldReturnChecklistItemWithAutomation() {
            // given
            CreateChecklistItemCommand command =
                    new CreateChecklistItemCommand(
                            1L, 1, "코드 스타일 검사", "AUTOMATED", "CHECKSTYLE", "rule-001", false);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ChecklistItem result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.automationTool().name()).isEqualTo(command.automationTool());
            assertThat(result.automationRuleId().value()).isEqualTo(command.automationRuleId());
        }

        @Test
        @DisplayName("성공 - 자동화 도구 없이 ChecklistItem 생성")
        void create_WithoutAutomationTool_ShouldReturnChecklistItemWithoutAutomation() {
            // given
            CreateChecklistItemCommand command =
                    new CreateChecklistItemCommand(1L, 1, "수동 검사 항목", "MANUAL", null, null, false);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ChecklistItem result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.automationTool()).isNull();
            assertThat(result.automationRuleId().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("toUpdateData 메서드")
    class ToUpdateData {

        @Test
        @DisplayName("성공 - UpdateChecklistItemCommand로 ChecklistItemUpdateData 생성")
        void toUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateChecklistItemCommand command =
                    new UpdateChecklistItemCommand(
                            1L, 2, "수정된 설명", "AUTOMATED", "PMD", "rule-002", true);

            // when
            ChecklistItemUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sequenceOrder()).isPresent();
            assertThat(result.sequenceOrder().get().value()).isEqualTo(command.sequenceOrder());
            assertThat(result.checkDescription()).isPresent();
            assertThat(result.checkDescription().get().value())
                    .isEqualTo(command.checkDescription());
        }

        @Test
        @DisplayName("성공 - 부분 업데이트 Command로 ChecklistItemUpdateData 생성")
        void toUpdateData_WithPartialCommand_ShouldReturnPartialUpdateData() {
            // given
            UpdateChecklistItemCommand command =
                    new UpdateChecklistItemCommand(1L, null, "설명만 수정", null, null, null, null);

            // when
            ChecklistItemUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sequenceOrder()).isEmpty();
            assertThat(result.checkDescription()).isPresent();
            assertThat(result.checkType()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateChecklistItemCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateChecklistItemCommand command =
                    new UpdateChecklistItemCommand(
                            1L, 2, "수정된 설명", "AUTOMATED", "PMD", "rule-002", true);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ChecklistItemId, ChecklistItemUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.checklistItemId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
