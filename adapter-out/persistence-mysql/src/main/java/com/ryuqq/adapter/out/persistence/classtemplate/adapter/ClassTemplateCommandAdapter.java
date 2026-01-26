package com.ryuqq.adapter.out.persistence.classtemplate.adapter;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.classtemplate.mapper.ClassTemplateJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.classtemplate.repository.ClassTemplateJpaRepository;
import com.ryuqq.application.classtemplate.port.out.ClassTemplateCommandPort;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateCommandAdapter - 클래스 템플릿 명령 어댑터
 *
 * <p>ClassTemplateCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateCommandAdapter implements ClassTemplateCommandPort {

    private final ClassTemplateJpaRepository repository;
    private final ClassTemplateJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ClassTemplateCommandAdapter(
            ClassTemplateJpaRepository repository, ClassTemplateJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ClassTemplate 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param classTemplate 영속화할 ClassTemplate
     * @return 영속화된 ClassTemplate ID
     */
    @Override
    public ClassTemplateId persist(ClassTemplate classTemplate) {
        ClassTemplateJpaEntity entity = mapper.toEntity(classTemplate);
        ClassTemplateJpaEntity saved = repository.save(entity);
        return ClassTemplateId.of(saved.getId());
    }
}
