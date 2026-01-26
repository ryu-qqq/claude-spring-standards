package com.ryuqq.application.checklistitem.manager;

import com.ryuqq.application.checklistitem.port.out.ChecklistItemCommandPort;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ChecklistItemPersistenceManager - 체크리스트 항목 영속화 관리자
 *
 * <p>체크리스트 항목 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemPersistenceManager {

    private final ChecklistItemCommandPort checklistItemCommandPort;

    public ChecklistItemPersistenceManager(ChecklistItemCommandPort checklistItemCommandPort) {
        this.checklistItemCommandPort = checklistItemCommandPort;
    }

    /**
     * 체크리스트 항목 영속화 (생성 또는 수정)
     *
     * @param checklistItem 영속화할 체크리스트 항목
     * @return 영속화된 체크리스트 항목 ID
     */
    @Transactional
    public ChecklistItemId persist(ChecklistItem checklistItem) {
        return checklistItemCommandPort.persist(checklistItem);
    }
}
