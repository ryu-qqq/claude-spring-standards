package com.ryuqq.application.feedbackqueue.internal.validator.merge.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidator;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.application.ruleexample.manager.RuleExampleReadManager;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackMergeValidationException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * RuleExampleMergeValidator - 규칙 예시 병합 시점 검증기
 *
 * <p>RULE_EXAMPLE 타입의 피드백을 병합 시점에 재검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: CodingRule(부모) 존재 여부 (필수)
 *   <li>MODIFY/DELETE 시: RuleExample(대상) 존재 여부 (필수)
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleMergeValidator implements FeedbackMergeValidator {

    private final ObjectMapper objectMapper;
    private final CodingRuleReadManager codingRuleReadManager;
    private final RuleExampleReadManager ruleExampleReadManager;

    public RuleExampleMergeValidator(
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
        CreateRuleExampleCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateRuleExampleCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for CreateRuleExampleCommand: " + e.getMessage());
        }

        CodingRuleId ruleId = CodingRuleId.of(createCommand.ruleId());
        boolean parentExists = codingRuleReadManager.findById(ruleId) != null;

        if (!parentExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "CodingRule not found: " + ruleId.value());
        }
    }

    private void validateModify(FeedbackType feedbackType, String payload) {
        UpdateRuleExampleCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateRuleExampleCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for UpdateRuleExampleCommand: " + e.getMessage());
        }

        RuleExampleId ruleExampleId = RuleExampleId.of(updateCommand.ruleExampleId());
        boolean targetExists = ruleExampleReadManager.findById(ruleExampleId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "RuleExample not found: " + ruleExampleId.value());
        }
    }

    private void validateDelete(FeedbackType feedbackType, Long targetId) {
        if (targetId == null) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "Target ID is required for delete operation");
        }

        RuleExampleId ruleExampleId = RuleExampleId.of(targetId);
        boolean targetExists = ruleExampleReadManager.findById(ruleExampleId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "RuleExample not found: " + targetId);
        }
    }
}
