package com.ryuqq.adapter.out.persistence.codingrule.adapter;

import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.codingrule.mapper.CodingRuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.codingrule.repository.CodingRuleJpaRepository;
import com.ryuqq.application.codingrule.port.out.CodingRuleCommandPort;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.springframework.stereotype.Component;

/**
 * CodingRuleCommandAdapter - 코딩 규칙 명령 어댑터
 *
 * <p>CodingRuleCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleCommandAdapter implements CodingRuleCommandPort {

    private final CodingRuleJpaRepository repository;
    private final CodingRuleJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public CodingRuleCommandAdapter(
            CodingRuleJpaRepository repository, CodingRuleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * CodingRule 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param codingRule 영속화할 CodingRule
     * @return 영속화된 CodingRule ID
     */
    @Override
    public CodingRuleId persist(CodingRule codingRule) {
        CodingRuleJpaEntity entity = mapper.toEntity(codingRule);
        CodingRuleJpaEntity saved = repository.save(entity);
        return CodingRuleId.of(saved.getId());
    }
}
