package com.ryuqq.application.feedbackqueue.internal.validator.payload.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.checklistitem.manager.ChecklistItemReadManager;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidator;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemPayloadValidator - 체크리스트 항목 페이로드 검증기
 *
 * <p>CHECKLIST_ITEM 타입의 피드백 페이로드를 검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: CodingRule(부모) 존재 여부 → 없으면 예외
 *   <li>MODIFY 시: ChecklistItem 존재 여부 → 없으면 예외
 *   <li>DELETE 시: ChecklistItem 존재 여부 → 없으면 예외
 * </ol>
 *
 * <p>검증 성공 시 아무것도 반환하지 않고, 실패 시 {@link InvalidFeedbackPayloadException}을 던집니다.
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemPayloadValidator implements FeedbackPayloadValidator {

    private final ObjectMapper objectMapper;
    private final CodingRuleReadManager codingRuleReadManager;
    private final ChecklistItemReadManager checklistItemReadManager;

    public ChecklistItemPayloadValidator(
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
    public void validate(CreateFeedbackCommand command) {
        FeedbackTargetType targetType = supportedType();
        String feedbackTypeString = command.feedbackType();
        FeedbackType feedbackType = FeedbackType.valueOf(feedbackTypeString);

        if (feedbackType.isAdd()) {
            validateAdd(targetType, feedbackTypeString, command.payload());
        } else if (feedbackType.isModify()) {
            validateModify(targetType, feedbackTypeString, command.payload());
        } else if (feedbackType.isDelete()) {
            validateDelete(targetType, feedbackTypeString, command.targetId());
        } else {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackTypeString, "Unsupported feedback type");
        }
    }

    private void validateAdd(FeedbackTargetType targetType, String feedbackType, String payload) {
        CreateChecklistItemCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateChecklistItemCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for CreateChecklistItemCommand: " + e.getMessage());
        }

        CodingRuleId codingRuleId = CodingRuleId.of(createCommand.ruleId());
        boolean parentExists = codingRuleReadManager.findById(codingRuleId) != null;

        if (!parentExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "CodingRule not found: " + codingRuleId.value());
        }
    }

    private void validateModify(
            FeedbackTargetType targetType, String feedbackType, String payload) {
        UpdateChecklistItemCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateChecklistItemCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for UpdateChecklistItemCommand: " + e.getMessage());
        }

        ChecklistItemId checklistItemId = ChecklistItemId.of(updateCommand.checklistItemId());
        boolean targetExists = checklistItemReadManager.findById(checklistItemId) != null;

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "ChecklistItem not found for modification: " + checklistItemId.value());
        }
    }

    private void validateDelete(FeedbackTargetType targetType, String feedbackType, Long targetId) {
        if (targetId == null) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "Target ID is required for delete operation");
        }

        ChecklistItemId checklistItemId = ChecklistItemId.of(targetId);
        boolean targetExists = checklistItemReadManager.findById(checklistItemId) != null;

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "ChecklistItem not found for deletion: " + targetId);
        }
    }
}
