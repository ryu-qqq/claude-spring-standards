package com.ryuqq.adapter.out.persistence.checklistitem.adapter;

import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.adapter.out.persistence.checklistitem.mapper.ChecklistItemJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.checklistitem.repository.ChecklistItemJpaRepository;
import com.ryuqq.application.checklistitem.port.out.ChecklistItemCommandPort;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemCommandAdapter - 체크리스트 항목 명령 어댑터
 *
 * <p>ChecklistItemCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemCommandAdapter implements ChecklistItemCommandPort {

    private final ChecklistItemJpaRepository repository;
    private final ChecklistItemJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ChecklistItemCommandAdapter(
            ChecklistItemJpaRepository repository, ChecklistItemJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ChecklistItem 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param checklistItem 영속화할 ChecklistItem
     * @return 영속화된 ChecklistItem ID
     */
    @Override
    public ChecklistItemId persist(ChecklistItem checklistItem) {
        ChecklistItemJpaEntity entity = mapper.toEntity(checklistItem);
        ChecklistItemJpaEntity saved = repository.save(entity);
        return ChecklistItemId.of(saved.getId());
    }
}
