package com.ryuqq.application.feedbackqueue.internal.validator.payload.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidator;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.application.ruleexample.manager.RuleExampleReadManager;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * RuleExamplePayloadValidator - 규칙 예시 페이로드 검증기
 *
 * <p>RULE_EXAMPLE 타입의 피드백 페이로드를 검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: CodingRule(부모) 존재 여부 → 없으면 예외
 *   <li>MODIFY 시: RuleExample 존재 여부 → 없으면 예외
 *   <li>DELETE 시: RuleExample 존재 여부 → 없으면 예외
 * </ol>
 *
 * <p>검증 성공 시 아무것도 반환하지 않고, 실패 시 {@link InvalidFeedbackPayloadException}을 던집니다.
 *
 * @author ryu-qqq
 */
@Component
public class RuleExamplePayloadValidator implements FeedbackPayloadValidator {

    private final ObjectMapper objectMapper;
    private final CodingRuleReadManager codingRuleReadManager;
    private final RuleExampleReadManager ruleExampleReadManager;

    public RuleExamplePayloadValidator(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            CodingRuleReadManager codingRuleReadManager,
            RuleExampleReadManager ruleExampleReadManager) {
        this.objectMapper = objectMapper;
        this.codingRuleReadManager = codingRuleReadManager;
        this.ruleExampleReadManager = ruleExampleReadManager;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.RULE_EXAMPLE;
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
        CreateRuleExampleCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateRuleExampleCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for CreateRuleExampleCommand: " + e.getMessage());
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
        UpdateRuleExampleCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateRuleExampleCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for UpdateRuleExampleCommand: " + e.getMessage());
        }

        RuleExampleId ruleExampleId = RuleExampleId.of(updateCommand.ruleExampleId());
        boolean targetExists = ruleExampleReadManager.findById(ruleExampleId) != null;

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "RuleExample not found for modification: " + ruleExampleId.value());
        }
    }

    private void validateDelete(FeedbackTargetType targetType, String feedbackType, Long targetId) {
        if (targetId == null) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "Target ID is required for delete operation");
        }

        RuleExampleId ruleExampleId = RuleExampleId.of(targetId);
        boolean targetExists = ruleExampleReadManager.findById(ruleExampleId) != null;

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "RuleExample not found for deletion: " + targetId);
        }
    }
}
