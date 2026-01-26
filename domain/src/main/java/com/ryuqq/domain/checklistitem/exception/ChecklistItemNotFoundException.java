package com.ryuqq.domain.checklistitem.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ChecklistItemNotFoundException - 체크리스트 항목 미존재 예외
 *
 * @author ryu-qqq
 */
public class ChecklistItemNotFoundException extends DomainException {

    public ChecklistItemNotFoundException(Long checklistItemId) {
        super(
                ChecklistItemErrorCode.CHECKLIST_ITEM_NOT_FOUND,
                String.format("ChecklistItem not found: %d", checklistItemId),
                Map.of("checklistItemId", checklistItemId));
    }
}
