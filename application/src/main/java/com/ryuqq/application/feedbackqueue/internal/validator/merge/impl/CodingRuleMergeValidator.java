package com.ryuqq.application.feedbackqueue.internal.validator.merge.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.convention.manager.ConventionReadManager;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidator;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackMergeValidationException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * CodingRuleMergeValidator - 코딩 규칙 병합 시점 검증기
 *
 * <p>CODING_RULE 타입의 피드백을 병합 시점에 재검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: Convention(부모) 존재 여부 (필수)
 *   <li>MODIFY/DELETE 시: CodingRule(대상) 존재 여부 (필수)
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleMergeValidator implements FeedbackMergeValidator {

    private final ObjectMapper objectMapper;
    private final ConventionReadManager conventionReadManager;
    private final CodingRuleReadManager codingRuleReadManager;

    public CodingRuleMergeValidator(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            ConventionReadManager conventionReadManager,
            CodingRuleReadManager codingRuleReadManager) {
        this.objectMapper = objectMapper;
        this.conventionReadManager = conventionReadManager;
        this.codingRuleReadManager = codingRuleReadManager;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.CODING_RULE;
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
        CreateCodingRuleCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateCodingRuleCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for CreateCodingRuleCommand: " + e.getMessage());
        }

        ConventionId conventionId = ConventionId.of(createCommand.conventionId());
        boolean parentExists = conventionReadManager.findById(conventionId).isPresent();

        if (!parentExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "Convention not found: " + conventionId.value());
        }
    }

    private void validateModify(FeedbackType feedbackType, String payload) {
        UpdateCodingRuleCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateCodingRuleCommand.class);
        } catch (JsonProcessingException e) {
            throw new FeedbackMergeValidationException(
                    supportedType(),
                    feedbackType,
                    "Invalid payload format for UpdateCodingRuleCommand: " + e.getMessage());
        }

        CodingRuleId codingRuleId = CodingRuleId.of(updateCommand.codingRuleId());
        boolean targetExists = codingRuleReadManager.findById(codingRuleId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "CodingRule not found: " + codingRuleId.value());
        }
    }

    private void validateDelete(FeedbackType feedbackType, Long targetId) {
        if (targetId == null) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "Target ID is required for delete operation");
        }

        CodingRuleId codingRuleId = CodingRuleId.of(targetId);
        boolean targetExists = codingRuleReadManager.findById(codingRuleId) != null;

        if (!targetExists) {
            throw new FeedbackMergeValidationException(
                    supportedType(), feedbackType, "CodingRule not found: " + targetId);
        }
    }
}
