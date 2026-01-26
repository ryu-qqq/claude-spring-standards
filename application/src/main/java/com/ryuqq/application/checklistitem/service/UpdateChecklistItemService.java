package com.ryuqq.application.checklistitem.service;

import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.checklistitem.factory.command.ChecklistItemCommandFactory;
import com.ryuqq.application.checklistitem.manager.ChecklistItemPersistenceManager;
import com.ryuqq.application.checklistitem.port.in.UpdateChecklistItemUseCase;
import com.ryuqq.application.checklistitem.validator.ChecklistItemValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItemUpdateData;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import org.springframework.stereotype.Service;

/**
 * UpdateChecklistItemService - 체크리스트 항목 수정 서비스
 *
 * <p>UpdateChecklistItemUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → UpdateContext.changedAt() 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateChecklistItemService implements UpdateChecklistItemUseCase {

    private final ChecklistItemValidator checklistItemValidator;
    private final ChecklistItemCommandFactory checklistItemCommandFactory;
    private final ChecklistItemPersistenceManager checklistItemPersistenceManager;

    public UpdateChecklistItemService(
            ChecklistItemValidator checklistItemValidator,
            ChecklistItemCommandFactory checklistItemCommandFactory,
            ChecklistItemPersistenceManager checklistItemPersistenceManager) {
        this.checklistItemValidator = checklistItemValidator;
        this.checklistItemCommandFactory = checklistItemCommandFactory;
        this.checklistItemPersistenceManager = checklistItemPersistenceManager;
    }

    @Override
    public void execute(UpdateChecklistItemCommand command) {
        UpdateContext<ChecklistItemId, ChecklistItemUpdateData> context =
                checklistItemCommandFactory.createUpdateContext(command);

        ChecklistItem checklistItem = checklistItemValidator.findExistingOrThrow(context.id());

        checklistItem.update(context.updateData(), context.changedAt());

        checklistItemPersistenceManager.persist(checklistItem);
    }
}
