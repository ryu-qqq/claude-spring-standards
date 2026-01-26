package com.ryuqq.adapter.out.persistence.resourcetemplate.adapter;

import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.resourcetemplate.mapper.ResourceTemplateJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.resourcetemplate.repository.ResourceTemplateQueryDslRepository;
import com.ryuqq.application.resourcetemplate.port.out.ResourceTemplateQueryPort;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateQueryAdapter - 리소스 템플릿 조회 어댑터
 *
 * <p>ResourceTemplateQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QADP-001: QueryDslRepository 위임만
 *
 * <p>QADP-002: QueryAdapter에서 @Transactional 금지
 *
 * <p>QADP-006: Domain 반환 (DTO 반환 금지)
 *
 * <p>QADP-007: Entity -> Domain 변환 (Mapper 사용)
 *
 * <p>QADP-008: QueryAdapter에 비즈니스 로직 금지
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateQueryAdapter implements ResourceTemplateQueryPort {

    private final ResourceTemplateQueryDslRepository queryDslRepository;
    private final ResourceTemplateJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    @SuppressFBWarnings(value = "EI2", justification = "Spring-managed bean injection")
    public ResourceTemplateQueryAdapter(
            ResourceTemplateQueryDslRepository queryDslRepository,
            ResourceTemplateJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 리소스 템플릿 조회
     *
     * @param id 리소스 템플릿 ID
     * @return 리소스 템플릿 Optional
     */
    @Override
    public Optional<ResourceTemplate> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * ResourceTemplateId로 리소스 템플릿 조회
     *
     * @param resourceTemplateId 리소스 템플릿 ID
     * @return 리소스 템플릿 Optional
     */
    @Override
    public Optional<ResourceTemplate> findById(ResourceTemplateId resourceTemplateId) {
        return queryDslRepository.findById(resourceTemplateId.value()).map(mapper::toDomain);
    }

    /**
     * 모듈 ID로 리소스 템플릿 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 리소스 템플릿 목록
     */
    @Override
    public List<ResourceTemplate> findByModuleId(Long moduleId) {
        List<ResourceTemplateJpaEntity> entities = queryDslRepository.findByModuleId(moduleId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * ModuleId 값 객체로 리소스 템플릿 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 리소스 템플릿 목록
     */
    @Override
    public List<ResourceTemplate> findByModuleId(ModuleId moduleId) {
        return findByModuleId(moduleId.value());
    }

    /**
     * 슬라이스 조건으로 리소스 템플릿 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 리소스 템플릿 목록
     */
    @Override
    public List<ResourceTemplate> findBySliceCriteria(ResourceTemplateSliceCriteria criteria) {
        List<ResourceTemplateJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
