package com.ryuqq.adapter.out.persistence.archunittest.adapter;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.adapter.out.persistence.archunittest.mapper.ArchUnitTestJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.archunittest.repository.ArchUnitTestJpaRepository;
import com.ryuqq.application.archunittest.port.out.ArchUnitTestCommandPort;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestCommandAdapter - ArchUnit 테스트 명령 어댑터
 *
 * <p>ArchUnitTestCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestCommandAdapter implements ArchUnitTestCommandPort {

    private final ArchUnitTestJpaRepository repository;
    private final ArchUnitTestJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ArchUnitTestCommandAdapter(
            ArchUnitTestJpaRepository repository, ArchUnitTestJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ArchUnitTest 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param archUnitTest 영속화할 ArchUnitTest
     * @return 영속화된 ArchUnitTest ID
     */
    @Override
    public ArchUnitTestId persist(ArchUnitTest archUnitTest) {
        ArchUnitTestJpaEntity entity = mapper.toEntity(archUnitTest);
        ArchUnitTestJpaEntity saved = repository.save(entity);
        return ArchUnitTestId.of(saved.getId());
    }
}
