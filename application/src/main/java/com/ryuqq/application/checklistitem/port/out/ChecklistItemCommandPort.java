package com.ryuqq.application.checklistitem.port.out;

import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;

/**
 * ChecklistItemCommandPort - 체크리스트 항목 명령 Port
 *
 * <p>영속성 계층으로의 ChecklistItem CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ChecklistItemCommandPort {

    /**
     * ChecklistItem 영속화 (생성/수정/삭제)
     *
     * @param checklistItem 영속화할 ChecklistItem
     * @return 영속화된 ChecklistItem ID
     */
    ChecklistItemId persist(ChecklistItem checklistItem);
}
