package com.ryuqq.adapter.out.persistence.packagestructure.adapter;

import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import com.ryuqq.adapter.out.persistence.packagestructure.mapper.PackageStructureJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.packagestructure.repository.PackageStructureJpaRepository;
import com.ryuqq.application.packagestructure.port.out.PackageStructureCommandPort;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;

/**
 * PackageStructureCommandAdapter - 패키지 구조 명령 어댑터
 *
 * <p>PackageStructureCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class PackageStructureCommandAdapter implements PackageStructureCommandPort {

    private final PackageStructureJpaRepository repository;
    private final PackageStructureJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public PackageStructureCommandAdapter(
            PackageStructureJpaRepository repository, PackageStructureJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * PackageStructure 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param packageStructure 영속화할 PackageStructure
     * @return 영속화된 PackageStructure ID
     */
    @Override
    public PackageStructureId persist(PackageStructure packageStructure) {
        PackageStructureJpaEntity entity = mapper.toEntity(packageStructure);
        PackageStructureJpaEntity saved = repository.save(entity);
        return PackageStructureId.of(saved.getId());
    }
}
