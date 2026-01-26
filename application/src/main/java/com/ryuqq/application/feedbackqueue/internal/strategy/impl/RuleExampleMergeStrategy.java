package com.ryuqq.application.feedbackqueue.internal.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.codingrule.validator.CodingRuleValidator;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategy;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.application.ruleexample.factory.command.RuleExampleCommandFactory;
import com.ryuqq.application.ruleexample.manager.RuleExamplePersistenceManager;
import com.ryuqq.application.ruleexample.validator.RuleExampleValidator;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * RuleExampleMergeStrategy - 규칙 예시 머지 전략
 *
 * <p>FeedbackQueue의 RULE_EXAMPLE 타입을 처리합니다.
 *
 * <p>처리 흐름:
 *
 * <ol>
 *   <li>Payload JSON 파싱
 *   <li>CodingRule 존재 여부 검증 (ADD/MODIFY 시)
 *   <li>RuleExample 존재 여부 검증 (MODIFY/DELETE 시)
 *   <li>도메인 객체 생성/수정/삭제
 *   <li>영속화
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleMergeStrategy implements FeedbackMergeStrategy {

    private final ObjectMapper objectMapper;
    private final CodingRuleValidator codingRuleValidator;
    private final RuleExampleValidator ruleExampleValidator;
    private final RuleExampleCommandFactory ruleExampleCommandFactory;
    private final RuleExamplePersistenceManager ruleExamplePersistenceManager;
    private final TimeProvider timeProvider;

    public RuleExampleMergeStrategy(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            CodingRuleValidator codingRuleValidator,
            RuleExampleValidator ruleExampleValidator,
            RuleExampleCommandFactory ruleExampleCommandFactory,
            RuleExamplePersistenceManager ruleExamplePersistenceManager,
            TimeProvider timeProvider) {
        this.objectMapper = objectMapper;
        this.codingRuleValidator = codingRuleValidator;
        this.ruleExampleValidator = ruleExampleValidator;
        this.ruleExampleCommandFactory = ruleExampleCommandFactory;
        this.ruleExamplePersistenceManager = ruleExamplePersistenceManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.RULE_EXAMPLE;
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
        CreateRuleExampleCommand command = parseCreateCommand(feedbackQueue.payloadValue());

        // CodingRule 존재 검증
        CodingRuleId codingRuleId = CodingRuleId.of(command.ruleId());
        codingRuleValidator.validateExists(codingRuleId);

        // 도메인 객체 생성 및 영속화
        RuleExample ruleExample = ruleExampleCommandFactory.create(command);
        RuleExampleId savedId = ruleExamplePersistenceManager.persist(ruleExample);

        return savedId.value();
    }

    private Long handleModify(FeedbackQueue feedbackQueue) {
        UpdateRuleExampleCommand command = parseUpdateCommand(feedbackQueue.payloadValue());

        // RuleExample 존재 검증 및 조회
        RuleExampleId ruleExampleId = RuleExampleId.of(command.ruleExampleId());
        RuleExample ruleExample = ruleExampleValidator.findExistingOrThrow(ruleExampleId);

        // 업데이트 및 영속화
        ruleExample.update(ruleExampleCommandFactory.toUpdateData(command), timeProvider.now());
        RuleExampleId savedId = ruleExamplePersistenceManager.persist(ruleExample);

        return savedId.value();
    }

    private Long handleDelete(FeedbackQueue feedbackQueue) {
        Long targetId = feedbackQueue.targetId();
        RuleExampleId ruleExampleId = RuleExampleId.of(targetId);

        // RuleExample 존재 검증 및 조회
        RuleExample ruleExample = ruleExampleValidator.findExistingOrThrow(ruleExampleId);

        // 삭제 처리 (soft delete)
        ruleExample.delete(timeProvider.now());
        ruleExamplePersistenceManager.persist(ruleExample);

        return targetId;
    }

    private CreateRuleExampleCommand parseCreateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, CreateRuleExampleCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse CreateRuleExampleCommand from payload", e);
        }
    }

    private UpdateRuleExampleCommand parseUpdateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, UpdateRuleExampleCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse UpdateRuleExampleCommand from payload", e);
        }
    }
}
