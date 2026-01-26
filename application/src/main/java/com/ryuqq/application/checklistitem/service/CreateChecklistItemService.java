package com.ryuqq.application.checklistitem.service;

import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.factory.command.ChecklistItemCommandFactory;
import com.ryuqq.application.checklistitem.manager.ChecklistItemPersistenceManager;
import com.ryuqq.application.checklistitem.port.in.CreateChecklistItemUseCase;
import com.ryuqq.application.checklistitem.validator.ChecklistItemValidator;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.springframework.stereotype.Service;

/**
 * CreateChecklistItemService - 체크리스트 항목 생성 서비스
 *
 * <p>CreateChecklistItemUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * @author ryu-qqq
 */
@Service
public class CreateChecklistItemService implements CreateChecklistItemUseCase {

    private final ChecklistItemValidator checklistItemValidator;
    private final ChecklistItemCommandFactory checklistItemCommandFactory;
    private final ChecklistItemPersistenceManager checklistItemPersistenceManager;

    public CreateChecklistItemService(
            ChecklistItemValidator checklistItemValidator,
            ChecklistItemCommandFactory checklistItemCommandFactory,
            ChecklistItemPersistenceManager checklistItemPersistenceManager) {
        this.checklistItemValidator = checklistItemValidator;
        this.checklistItemCommandFactory = checklistItemCommandFactory;
        this.checklistItemPersistenceManager = checklistItemPersistenceManager;
    }

    @Override
    public Long execute(CreateChecklistItemCommand command) {
        CodingRuleId ruleId = CodingRuleId.of(command.ruleId());
        checklistItemValidator.validateSequenceOrderNotDuplicate(ruleId, command.sequenceOrder());

        ChecklistItem checklistItem = checklistItemCommandFactory.create(command);
        ChecklistItemId savedId = checklistItemPersistenceManager.persist(checklistItem);

        return savedId.value();
    }
}
