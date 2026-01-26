package com.ryuqq.application.feedbackqueue.internal.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.checklistitem.factory.command.ChecklistItemCommandFactory;
import com.ryuqq.application.checklistitem.manager.ChecklistItemPersistenceManager;
import com.ryuqq.application.checklistitem.validator.ChecklistItemValidator;
import com.ryuqq.application.codingrule.validator.CodingRuleValidator;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategy;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemMergeStrategy - 체크리스트 항목 머지 전략
 *
 * <p>FeedbackQueue의 CHECKLIST_ITEM 타입을 처리합니다.
 *
 * <p>처리 흐름:
 *
 * <ol>
 *   <li>Payload JSON 파싱
 *   <li>CodingRule 존재 여부 검증 (ADD 시)
 *   <li>ChecklistItem 존재 여부 검증 (MODIFY/DELETE 시)
 *   <li>도메인 객체 생성/수정/삭제
 *   <li>영속화
 * </ol>
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemMergeStrategy implements FeedbackMergeStrategy {

    private final ObjectMapper objectMapper;
    private final CodingRuleValidator codingRuleValidator;
    private final ChecklistItemValidator checklistItemValidator;
    private final ChecklistItemCommandFactory checklistItemCommandFactory;
    private final ChecklistItemPersistenceManager checklistItemPersistenceManager;
    private final TimeProvider timeProvider;

    public ChecklistItemMergeStrategy(
            @Qualifier("applicationObjectMapper") ObjectMapper objectMapper,
            CodingRuleValidator codingRuleValidator,
            ChecklistItemValidator checklistItemValidator,
            ChecklistItemCommandFactory checklistItemCommandFactory,
            ChecklistItemPersistenceManager checklistItemPersistenceManager,
            TimeProvider timeProvider) {
        this.objectMapper = objectMapper;
        this.codingRuleValidator = codingRuleValidator;
        this.checklistItemValidator = checklistItemValidator;
        this.checklistItemCommandFactory = checklistItemCommandFactory;
        this.checklistItemPersistenceManager = checklistItemPersistenceManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public FeedbackTargetType supportedType() {
        return FeedbackTargetType.CHECKLIST_ITEM;
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
        CreateChecklistItemCommand command = parseCreateCommand(feedbackQueue.payloadValue());

        // CodingRule 존재 검증
        CodingRuleId codingRuleId = CodingRuleId.of(command.ruleId());
        codingRuleValidator.validateExists(codingRuleId);

        // 도메인 객체 생성 및 영속화
        ChecklistItem checklistItem = checklistItemCommandFactory.create(command);
        ChecklistItemId savedId = checklistItemPersistenceManager.persist(checklistItem);

        return savedId.value();
    }

    private Long handleModify(FeedbackQueue feedbackQueue) {
        UpdateChecklistItemCommand command = parseUpdateCommand(feedbackQueue.payloadValue());

        // ChecklistItem 존재 검증 및 조회
        ChecklistItemId checklistItemId = ChecklistItemId.of(command.checklistItemId());
        ChecklistItem checklistItem = checklistItemValidator.findExistingOrThrow(checklistItemId);

        // 업데이트 및 영속화
        checklistItem.update(checklistItemCommandFactory.toUpdateData(command), timeProvider.now());
        ChecklistItemId savedId = checklistItemPersistenceManager.persist(checklistItem);

        return savedId.value();
    }

    private Long handleDelete(FeedbackQueue feedbackQueue) {
        Long targetId = feedbackQueue.targetId();
        ChecklistItemId checklistItemId = ChecklistItemId.of(targetId);

        // ChecklistItem 존재 검증 및 조회
        ChecklistItem checklistItem = checklistItemValidator.findExistingOrThrow(checklistItemId);

        // 삭제 처리 (soft delete)
        checklistItem.delete(timeProvider.now());
        checklistItemPersistenceManager.persist(checklistItem);

        return targetId;
    }

    private CreateChecklistItemCommand parseCreateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, CreateChecklistItemCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse CreateChecklistItemCommand from payload", e);
        }
    }

    private UpdateChecklistItemCommand parseUpdateCommand(String payload) {
        try {
            return objectMapper.readValue(payload, UpdateChecklistItemCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse UpdateChecklistItemCommand from payload", e);
        }
    }
}
