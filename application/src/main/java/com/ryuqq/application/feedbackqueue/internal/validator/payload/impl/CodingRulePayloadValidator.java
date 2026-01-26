package com.ryuqq.application.feedbackqueue.internal.validator.payload.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.convention.manager.ConventionReadManager;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidator;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * CodingRulePayloadValidator - 코딩 규칙 페이로드 검증기
 *
 * <p>CODING_RULE 타입의 피드백 페이로드를 검증합니다.
 *
 * <p>검증 항목:
 *
 * <ol>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: Convention(부모) 존재 여부 → 없으면 예외
 *   <li>MODIFY 시: CodingRule 존재 여부 → 없으면 예외
 *   <li>DELETE 시: CodingRule 존재 여부 → 없으면 예외
 * </ol>
 *
 * <p>검증 성공 시 아무것도 반환하지 않고, 실패 시 {@link InvalidFeedbackPayloadException}을 던집니다.
 *
 * @author ryu-qqq
 */
@Component
public class CodingRulePayloadValidator implements FeedbackPayloadValidator {

    private final ObjectMapper objectMapper;
    private final ConventionReadManager conventionReadManager;
    private final CodingRuleReadManager codingRuleReadManager;

    public CodingRulePayloadValidator(
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
        CreateCodingRuleCommand createCommand;
        try {
            createCommand = objectMapper.readValue(payload, CreateCodingRuleCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for CreateCodingRuleCommand: " + e.getMessage());
        }

        ConventionId conventionId = ConventionId.of(createCommand.conventionId());
        boolean parentExists = conventionReadManager.findById(conventionId).isPresent();

        if (!parentExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "Convention not found: " + conventionId.value());
        }
    }

    private void validateModify(
            FeedbackTargetType targetType, String feedbackType, String payload) {
        UpdateCodingRuleCommand updateCommand;
        try {
            updateCommand = objectMapper.readValue(payload, UpdateCodingRuleCommand.class);
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "Invalid payload format for UpdateCodingRuleCommand: " + e.getMessage());
        }

        CodingRuleId codingRuleId = CodingRuleId.of(updateCommand.codingRuleId());
        boolean targetExists = codingRuleReadManager.findById(codingRuleId).isPresent();

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType,
                    feedbackType,
                    "CodingRule not found for modification: " + codingRuleId.value());
        }
    }

    private void validateDelete(FeedbackTargetType targetType, String feedbackType, Long targetId) {
        if (targetId == null) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "Target ID is required for delete operation");
        }

        CodingRuleId codingRuleId = CodingRuleId.of(targetId);
        boolean targetExists = codingRuleReadManager.findById(codingRuleId).isPresent();

        if (!targetExists) {
            throw new InvalidFeedbackPayloadException(
                    targetType, feedbackType, "CodingRule not found for deletion: " + targetId);
        }
    }
}
