package com.ryuqq.application.checklistitem.factory.command;

import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItemUpdateData;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.vo.AutomationRuleId;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckDescription;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.checklistitem.vo.SequenceOrder;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemCommandFactory - 체크리스트 항목 커맨드 팩토리
 *
 * <p>체크리스트 항목 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemCommandFactory {

    private final TimeProvider timeProvider;

    public ChecklistItemCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateChecklistItemCommand로부터 ChecklistItem 도메인 객체 생성
     *
     * <p>FCT-002: Factory에서 TimeProvider 사용하여 시간 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 ChecklistItem 인스턴스
     */
    public ChecklistItem create(CreateChecklistItemCommand command) {
        return ChecklistItem.forNew(
                CodingRuleId.of(command.ruleId()),
                SequenceOrder.of(command.sequenceOrder()),
                CheckDescription.of(command.checkDescription()),
                CheckType.valueOf(command.checkType()),
                command.automationTool() != null
                        ? AutomationTool.valueOf(command.automationTool())
                        : null,
                command.automationRuleId() != null
                        ? AutomationRuleId.of(command.automationRuleId())
                        : AutomationRuleId.empty(),
                command.critical(),
                timeProvider.now());
    }

    /**
     * UpdateChecklistItemCommand를 ChecklistItemUpdateData로 변환
     *
     * <p>수정 커맨드의 nullable 필드를 Optional 기반의 업데이트 데이터로 변환합니다.
     *
     * @param command 수정 커맨드
     * @return ChecklistItemUpdateData
     */
    public ChecklistItemUpdateData toUpdateData(UpdateChecklistItemCommand command) {
        ChecklistItemUpdateData.ChecklistItemUpdateDataBuilder builder =
                ChecklistItemUpdateData.builder();

        if (command.sequenceOrder() != null) {
            builder.sequenceOrder(SequenceOrder.of(command.sequenceOrder()));
        }
        if (command.checkDescription() != null) {
            builder.checkDescription(CheckDescription.of(command.checkDescription()));
        }
        if (command.checkType() != null) {
            builder.checkType(CheckType.valueOf(command.checkType()));
        }
        if (command.automationTool() != null) {
            builder.automationTool(AutomationTool.valueOf(command.automationTool()));
        }
        if (command.automationRuleId() != null) {
            builder.automationRuleId(AutomationRuleId.of(command.automationRuleId()));
        }
        if (command.critical() != null) {
            builder.critical(command.critical());
        }

        return builder.build();
    }

    /**
     * UpdateChecklistItemCommand로부터 ChecklistItemId와 ChecklistItemUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<ChecklistItemId, ChecklistItemUpdateData> createUpdateContext(
            UpdateChecklistItemCommand command) {
        ChecklistItemId id = ChecklistItemId.of(command.checklistItemId());
        ChecklistItemUpdateData updateData = toUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}
