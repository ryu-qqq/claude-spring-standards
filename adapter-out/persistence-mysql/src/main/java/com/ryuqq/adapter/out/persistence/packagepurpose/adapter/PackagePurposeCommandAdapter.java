package com.ryuqq.adapter.out.persistence.packagepurpose.adapter;

import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
import com.ryuqq.adapter.out.persistence.packagepurpose.mapper.PackagePurposeEntityMapper;
import com.ryuqq.adapter.out.persistence.packagepurpose.repository.PackagePurposeJpaRepository;
import com.ryuqq.application.packagepurpose.port.out.PackagePurposeCommandPort;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeCommandAdapter - 패키지 목적 명령 어댑터
 *
 * <p>PackagePurposeCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposeCommandAdapter implements PackagePurposeCommandPort {

    private final PackagePurposeJpaRepository repository;
    private final PackagePurposeEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public PackagePurposeCommandAdapter(
            PackagePurposeJpaRepository repository, PackagePurposeEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * PackagePurpose 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param packagePurpose 영속화할 PackagePurpose
     * @return 영속화된 PackagePurpose ID
     */
    @Override
    public PackagePurposeId persist(PackagePurpose packagePurpose) {
        PackagePurposeJpaEntity entity = mapper.toEntity(packagePurpose);
        PackagePurposeJpaEntity saved = repository.save(entity);
        return PackagePurposeId.of(saved.getId());
    }
}
