package com.ryuqq.adapter.out.persistence.module.adapter;

import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
import com.ryuqq.adapter.out.persistence.module.mapper.ModuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.module.repository.ModuleQueryDslRepository;
import com.ryuqq.application.module.port.out.ModuleQueryPort;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import com.ryuqq.domain.module.vo.ModuleName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ModuleQueryAdapter - Module 조회 어댑터
 *
 * <p>ModuleQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class ModuleQueryAdapter implements ModuleQueryPort {

    private final ModuleQueryDslRepository queryDslRepository;
    private final ModuleJpaEntityMapper mapper;

    public ModuleQueryAdapter(
            ModuleQueryDslRepository queryDslRepository, ModuleJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Module> findById(ModuleId moduleId) {
        return queryDslRepository.findById(moduleId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Module> findBySliceCriteria(ModuleSliceCriteria criteria) {
        List<ModuleJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Module> findAllByLayerId(LayerId layerId) {
        List<ModuleJpaEntity> entities = queryDslRepository.findAllByLayerId(layerId.value());
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByLayerIdAndName(LayerId layerId, ModuleName name) {
        return queryDslRepository.existsByLayerIdAndName(layerId.value(), name.value());
    }

    @Override
    public boolean existsByLayerIdAndNameExcluding(
            LayerId layerId, ModuleName name, ModuleId excludeModuleId) {
        return queryDslRepository.existsByLayerIdAndNameExcluding(
                layerId.value(), name.value(), excludeModuleId.value());
    }

    @Override
    public boolean existsByParentModuleId(ModuleId parentModuleId) {
        return queryDslRepository.existsByParentModuleId(parentModuleId.value());
    }

    @Override
    public List<Module> searchByKeyword(String keyword, Long layerId) {
        List<ModuleJpaEntity> entities = queryDslRepository.searchByKeyword(keyword, layerId);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
