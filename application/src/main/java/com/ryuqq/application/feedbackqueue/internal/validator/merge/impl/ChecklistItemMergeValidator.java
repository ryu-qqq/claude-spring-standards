package com.ryuqq.application.feedbackqueue.internal.validator.merge.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.checklistitem.manager.ChecklistItemReadManager;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidator;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackMergeValidationException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemMergeValidator - 체크리스트 항목 병합 시점 검증기
 *
 * <p>CHECKLIST_ITEM 타입의 피드백을 병합 시점에 재검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: CodingRule(부모) 존재 여부 (필수)
 *   <li>MODIFY/DELETE 시: ChecklistItem(대상) 존재 여부 (필수)
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemMergeValidator implements FeedbackMergeValidator {

    private final ObjectMapper objectMapper;
    private final CodingRuleReadManager codingRuleReadManager;
    private final ChecklistItemReadManager checklistItemReadManager;

    public ChecklistItemMergeValidator(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            CodingRuleReadManager codingRuleReadManager,
            ChecklistItemReadManager checklistItemReadManager) {
        this.objectMapper = objectMapper;
        this.codingRuleReadManager = codingRuleReadManager;
        this.checklistItemReadManager = checklistItemReadManager;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.CHECKLIST_ITEM;
    }

    @Override
    public void validate(FeedbackQueue feedbackQueue) {
        FeedbackType feedbackType = feedbackQueue.feedbackType();
        String payload = feedbackQueue.payloadValue();

        if (feedbackType.isAdd()) {
            validateAdd(feedbackType, payload);
        } else if (feedbackType.isModify()) {
            validateModify(feedbackType, payload);
        } else if (feedbackType.isDelete()) {
            validateDelete(feedbackType, feedbackQueue.targetId());
        } else {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "Unsupported feedback type");
        }
    }

    private void validateAdd(FeedbackType feedbackType, String payload) {
        CreateChecklistItemCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateChecklistItemCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for CreateChecklistItemCommand: " + e.getMessage());
        }

        CodingRuleId ruleId = CodingRuleId.of(createCommand.ruleId());
        boolean parentExists = codingRuleReadManager.findById(ruleId) != null;

        if (!parentExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "CodingRule not found: " + ruleId.value());
        }
    }

    private void validateModify(FeedbackType feedbackType, String payload) {
        UpdateChecklistItemCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateChecklistItemCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for UpdateChecklistItemCommand: " + e.getMessage());
        }

        ChecklistItemId checklistItemId = ChecklistItemId.of(updateCommand.checklistItemId());
        boolean targetExists = checklistItemReadManager.findById(checklistItemId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "ChecklistItem not found: " + checklistItemId.value());
        }
    }

    private void validateDelete(FeedbackType feedbackType, Long targetId) {
        if (targetId == null) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "Target ID is required for delete operation");
        }

        ChecklistItemId checklistItemId = ChecklistItemId.of(targetId);
        boolean targetExists = checklistItemReadManager.findById(checklistItemId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "ChecklistItem not found: " + targetId);
        }
    }
}
