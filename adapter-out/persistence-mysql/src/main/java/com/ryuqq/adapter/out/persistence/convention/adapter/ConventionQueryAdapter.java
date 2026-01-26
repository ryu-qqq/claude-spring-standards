package com.ryuqq.adapter.out.persistence.convention.adapter;

import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import com.ryuqq.adapter.out.persistence.convention.mapper.ConventionJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.convention.repository.ConventionQueryDslRepository;
import com.ryuqq.application.convention.port.out.ConventionQueryPort;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ConventionQueryAdapter - Convention 조회 어댑터
 *
 * <p>ConventionQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QADP-001: QueryDslRepository 위임만.
 *
 * <p>QADP-002: QueryAdapter에서 @Transactional 금지.
 *
 * <p>QADP-004: Query Port 구현 필수.
 *
 * @author ryu-qqq
 */
@Component
public class ConventionQueryAdapter implements ConventionQueryPort {

    private final ConventionQueryDslRepository queryDslRepository;
    private final ConventionJpaEntityMapper mapper;

    public ConventionQueryAdapter(
            ConventionQueryDslRepository queryDslRepository, ConventionJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Convention> findAllActive() {
        List<ConventionJpaEntity> entities = queryDslRepository.findAllActive();
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Convention> findActiveByModuleId(ModuleId moduleId) {
        return queryDslRepository.findActiveByModuleId(moduleId.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Convention> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Convention> findById(ConventionId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(ConventionId id) {
        return queryDslRepository.existsById(id.value());
    }

    @Override
    public boolean existsByModuleIdAndVersion(ModuleId moduleId, String version) {
        return queryDslRepository.existsByModuleIdAndVersion(moduleId.value(), version);
    }

    @Override
    public boolean existsByModuleIdAndVersionAndIdNot(
            ModuleId moduleId, String version, ConventionId excludeId) {
        return queryDslRepository.existsByModuleIdAndVersionAndIdNot(
                moduleId.value(), version, excludeId.value());
    }

    @Override
    public List<Convention> findBySliceCriteria(ConventionSliceCriteria criteria) {
        List<ConventionJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
