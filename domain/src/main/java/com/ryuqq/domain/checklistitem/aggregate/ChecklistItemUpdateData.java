package com.ryuqq.domain.checklistitem.aggregate;

import com.ryuqq.domain.checklistitem.vo.AutomationRuleId;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckDescription;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.checklistitem.vo.SequenceOrder;
import java.util.Optional;

/**
 * ChecklistItemUpdateData - 체크리스트 항목 수정 데이터 Value Object
 *
 * <p>체크리스트 항목 수정에 필요한 데이터를 전달합니다.
 *
 * <p>요청으로 들어온 데이터를 기반으로 객체를 만들고, JPA의 더티체킹을 활용하여 변경사항을 처리합니다.
 *
 * <p>Optional 필드는 null인 경우 해당 필드를 업데이트하지 않음을 의미합니다.
 *
 * @param sequenceOrder 순서 (optional)
 * @param checkDescription 체크 설명 (optional)
 * @param checkType 체크 타입 (optional)
 * @param automationTool 자동화 도구 (optional)
 * @param automationRuleId 자동화 규칙 ID (optional)
 * @param critical 필수 여부 (optional)
 * @author ryu-qqq
 */
public record ChecklistItemUpdateData(
        Optional<SequenceOrder> sequenceOrder,
        Optional<CheckDescription> checkDescription,
        Optional<CheckType> checkType,
        Optional<AutomationTool> automationTool,
        Optional<AutomationRuleId> automationRuleId,
        Optional<Boolean> critical) {

    public ChecklistItemUpdateData {
        if (sequenceOrder == null) {
            sequenceOrder = Optional.empty();
        }
        if (checkDescription == null) {
            checkDescription = Optional.empty();
        }
        if (checkType == null) {
            checkType = Optional.empty();
        }
        if (automationTool == null) {
            automationTool = Optional.empty();
        }
        if (automationRuleId == null) {
            automationRuleId = Optional.empty();
        }
        if (critical == null) {
            critical = Optional.empty();
        }
    }

    /**
     * 빈 업데이트 데이터 생성
     *
     * @return 빈 ChecklistItemUpdateData
     */
    public static ChecklistItemUpdateData empty() {
        return new ChecklistItemUpdateData(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
    }

    /**
     * Builder 스타일 생성 메서드
     *
     * @return ChecklistItemUpdateDataBuilder
     */
    public static ChecklistItemUpdateDataBuilder builder() {
        return new ChecklistItemUpdateDataBuilder();
    }

    /**
     * 업데이트할 내용이 있는지 확인
     *
     * @return 하나 이상의 필드가 present이면 true
     */
    public boolean hasUpdates() {
        return sequenceOrder.isPresent()
                || checkDescription.isPresent()
                || checkType.isPresent()
                || automationTool.isPresent()
                || automationRuleId.isPresent()
                || critical.isPresent();
    }

    /** ChecklistItemUpdateData Builder */
    public static class ChecklistItemUpdateDataBuilder {

        private Optional<SequenceOrder> sequenceOrder = Optional.empty();
        private Optional<CheckDescription> checkDescription = Optional.empty();
        private Optional<CheckType> checkType = Optional.empty();
        private Optional<AutomationTool> automationTool = Optional.empty();
        private Optional<AutomationRuleId> automationRuleId = Optional.empty();
        private Optional<Boolean> critical = Optional.empty();

        private ChecklistItemUpdateDataBuilder() {}

        public ChecklistItemUpdateDataBuilder sequenceOrder(SequenceOrder sequenceOrder) {
            this.sequenceOrder = Optional.ofNullable(sequenceOrder);
            return this;
        }

        public ChecklistItemUpdateDataBuilder checkDescription(CheckDescription checkDescription) {
            this.checkDescription = Optional.ofNullable(checkDescription);
            return this;
        }

        public ChecklistItemUpdateDataBuilder checkType(CheckType checkType) {
            this.checkType = Optional.ofNullable(checkType);
            return this;
        }

        public ChecklistItemUpdateDataBuilder automationTool(AutomationTool automationTool) {
            this.automationTool = Optional.ofNullable(automationTool);
            return this;
        }

        public ChecklistItemUpdateDataBuilder automationRuleId(AutomationRuleId automationRuleId) {
            this.automationRuleId = Optional.ofNullable(automationRuleId);
            return this;
        }

        public ChecklistItemUpdateDataBuilder critical(Boolean critical) {
            this.critical = Optional.ofNullable(critical);
            return this;
        }

        public ChecklistItemUpdateData build() {
            return new ChecklistItemUpdateData(
                    sequenceOrder,
                    checkDescription,
                    checkType,
                    automationTool,
                    automationRuleId,
                    critical);
        }
    }
}
