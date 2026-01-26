package com.ryuqq.adapter.out.persistence.ruleexample.adapter;

import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.ruleexample.mapper.RuleExampleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.ruleexample.repository.RuleExampleJpaRepository;
import com.ryuqq.application.ruleexample.port.out.RuleExampleCommandPort;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.springframework.stereotype.Component;

/**
 * RuleExampleCommandAdapter - 규칙 예시 명령 어댑터
 *
 * <p>RuleExampleCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleCommandAdapter implements RuleExampleCommandPort {

    private final RuleExampleJpaRepository repository;
    private final RuleExampleJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public RuleExampleCommandAdapter(
            RuleExampleJpaRepository repository, RuleExampleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * RuleExample 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param ruleExample 영속화할 RuleExample
     * @return 영속화된 RuleExample ID
     */
    @Override
    public RuleExampleId persist(RuleExample ruleExample) {
        RuleExampleJpaEntity entity = mapper.toEntity(ruleExample);
        RuleExampleJpaEntity saved = repository.save(entity);
        return RuleExampleId.of(saved.getId());
    }
}
