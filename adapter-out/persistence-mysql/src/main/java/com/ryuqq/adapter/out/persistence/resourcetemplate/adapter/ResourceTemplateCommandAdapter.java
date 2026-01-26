package com.ryuqq.adapter.out.persistence.resourcetemplate.adapter;

import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.resourcetemplate.mapper.ResourceTemplateJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.resourcetemplate.repository.ResourceTemplateJpaRepository;
import com.ryuqq.application.resourcetemplate.port.out.ResourceTemplateCommandPort;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateCommandAdapter - 리소스 템플릿 명령 어댑터
 *
 * <p>ResourceTemplateCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateCommandAdapter implements ResourceTemplateCommandPort {

    private final ResourceTemplateJpaRepository repository;
    private final ResourceTemplateJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ResourceTemplateCommandAdapter(
            ResourceTemplateJpaRepository repository, ResourceTemplateJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ResourceTemplate 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param resourceTemplate 영속화할 ResourceTemplate
     * @return 영속화된 ResourceTemplate ID
     */
    @Override
    public ResourceTemplateId persist(ResourceTemplate resourceTemplate) {
        ResourceTemplateJpaEntity entity = mapper.toEntity(resourceTemplate);
        ResourceTemplateJpaEntity saved = repository.save(entity);
        return ResourceTemplateId.of(saved.getId());
    }
}
