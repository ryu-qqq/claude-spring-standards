package com.ryuqq.adapter.out.persistence.zerotolerance.adapter;

import com.ryuqq.adapter.out.persistence.zerotolerance.entity.ZeroToleranceRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.zerotolerance.mapper.ZeroToleranceRuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.zerotolerance.repository.ZeroToleranceRuleJpaRepository;
import com.ryuqq.application.zerotolerance.port.out.ZeroToleranceRuleCommandPort;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleCommandAdapter - Zero-Tolerance 규칙 명령 어댑터
 *
 * <p>ZeroToleranceRuleCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleCommandAdapter implements ZeroToleranceRuleCommandPort {

    private final ZeroToleranceRuleJpaRepository repository;
    private final ZeroToleranceRuleJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ZeroToleranceRuleCommandAdapter(
            ZeroToleranceRuleJpaRepository repository, ZeroToleranceRuleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ZeroToleranceRule 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param zeroToleranceRule 영속화할 ZeroToleranceRule
     * @return 영속화된 ZeroToleranceRule ID
     */
    @Override
    public ZeroToleranceRuleId persist(ZeroToleranceRule zeroToleranceRule) {
        ZeroToleranceRuleJpaEntity entity = mapper.toEntity(zeroToleranceRule);
        ZeroToleranceRuleJpaEntity saved = repository.save(entity);
        return ZeroToleranceRuleId.of(saved.getId());
    }
}
