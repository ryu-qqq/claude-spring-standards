package com.ryuqq.application.feedbackqueue.internal.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.codingrule.factory.command.CodingRuleCommandFactory;
import com.ryuqq.application.codingrule.manager.CodingRulePersistenceManager;
import com.ryuqq.application.codingrule.validator.CodingRuleValidator;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.convention.validator.ConventionValidator;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategy;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * CodingRuleMergeStrategy - 코딩 규칙 머지 전략
 *
 * <p>FeedbackQueue의 CODING_RULE 타입을 처리합니다.
 *
 * <p>처리 흐름:
 *
 * <ol>
 *   <li>Payload JSON 파싱
 *   <li>Convention 존재 여부 검증 (ADD 시)
 *   <li>CodingRule 존재 여부 검증 (MODIFY/DELETE 시)
 *   <li>도메인 객체 생성/수정/삭제
 *   <li>영속화
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleMergeStrategy implements FeedbackMergeStrategy {

    private final ObjectMapper objectMapper;
    private final ConventionValidator conventionValidator;
    private final CodingRuleValidator codingRuleValidator;
    private final CodingRuleCommandFactory codingRuleCommandFactory;
    private final CodingRulePersistenceManager codingRulePersistenceManager;
    private final TimeProvider timeProvider;

    public CodingRuleMergeStrategy(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            ConventionValidator conventionValidator,
            CodingRuleValidator codingRuleValidator,
            CodingRuleCommandFactory codingRuleCommandFactory,
            CodingRulePersistenceManager codingRulePersistenceManager,
            TimeProvider timeProvider) {
        this.objectMapper = objectMapper;
        this.conventionValidator = conventionValidator;
        this.codingRuleValidator = codingRuleValidator;
        this.codingRuleCommandFactory = codingRuleCommandFactory;
        this.codingRulePersistenceManager = codingRulePersistenceManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.CODING_RULE;
    }

    @Override
    public Long merge(FeedbackQueue feedbackQueue) {
        FeedbackType feedbackType = feedbackQueue.feedbackType();

        if (feedbackType.isAdd()) {
            return handleAdd(feedbackQueue);
        } else if (feedbackType.isModify()) {
            return handleModify(feedbackQueue);
        } else if (feedbackType.isDelete()) {
            return handleDelete(feedbackQueue);
        }

        throw new IllegalArgumentException("Unsupported feedback type: " + feedbackType);
    }

    private Long handleAdd(FeedbackQueue feedbackQueue) {
        CreateCodingRuleCommand command = parseCreateCommand(feedbackQueue.payloadValue());

        // Convention 존재 검증
        ConventionId conventionId = ConventionId.of(command.conventionId());
        conventionValidator.validateExists(conventionId);

        // 도메인 객체 생성 및 영속화
        CodingRule codingRule = codingRuleCommandFactory.create(command);
        CodingRuleId savedId = codingRulePersistenceManager.persist(codingRule);

        return savedId.value();
    }

    private Long handleModify(FeedbackQueue feedbackQueue) {
        UpdateCodingRuleCommand command = parseUpdateCommand(feedbackQueue.payloadValue());

        // CodingRule 존재 검증 및 조회
        CodingRuleId codingRuleId = CodingRuleId.of(command.codingRuleId());
        CodingRule codingRule = codingRuleValidator.findExistingOrThrow(codingRuleId);

        // 업데이트 및 영속화
        codingRule.update(codingRuleCommandFactory.createUpdateData(command), timeProvider.now());
        CodingRuleId savedId = codingRulePersistenceManager.persist(codingRule);

        return savedId.value();
    }

    private Long handleDelete(FeedbackQueue feedbackQueue) {
        Long targetId = feedbackQueue.targetId();
        CodingRuleId codingRuleId = CodingRuleId.of(targetId);

        // CodingRule 존재 검증 및 조회
        CodingRule codingRule = codingRuleValidator.findExistingOrThrow(codingRuleId);

        // 삭제 처리 (soft delete)
        codingRule.delete(timeProvider.now());
        codingRulePersistenceManager.persist(codingRule);

        return targetId;
    }

    private CreateCodingRuleCommand parseCreateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, CreateCodingRuleCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse CreateCodingRuleCommand from payload", e);
        }
    }

    private UpdateCodingRuleCommand parseUpdateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, UpdateCodingRuleCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse UpdateCodingRuleCommand from payload", e);
        }
    }
}
