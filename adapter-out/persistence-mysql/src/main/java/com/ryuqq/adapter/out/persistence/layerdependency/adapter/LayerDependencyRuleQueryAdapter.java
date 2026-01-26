package com.ryuqq.adapter.out.persistence.layerdependency.adapter;

import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.layerdependency.mapper.LayerDependencyRuleEntityMapper;
import com.ryuqq.adapter.out.persistence.layerdependency.repository.LayerDependencyRuleQueryDslRepository;
import com.ryuqq.application.layerdependency.port.out.LayerDependencyRuleQueryPort;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleQueryAdapter - 레이어 의존성 규칙 조회 어댑터
 *
 * <p>LayerDependencyRuleQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class LayerDependencyRuleQueryAdapter implements LayerDependencyRuleQueryPort {

    private final LayerDependencyRuleQueryDslRepository queryDslRepository;
    private final LayerDependencyRuleEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public LayerDependencyRuleQueryAdapter(
            LayerDependencyRuleQueryDslRepository queryDslRepository,
            LayerDependencyRuleEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 레이어 의존성 규칙 조회
     *
     * @param id 레이어 의존성 규칙 ID
     * @return LayerDependencyRule (Optional)
     */
    @Override
    public Optional<LayerDependencyRule> findById(LayerDependencyRuleId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * 아키텍처 ID로 레이어 의존성 규칙 목록 조회
     *
     * @param architectureId 아키텍처 ID
     * @return 레이어 의존성 규칙 목록
     */
    @Override
    public List<LayerDependencyRule> findByArchitectureId(ArchitectureId architectureId) {
        List<LayerDependencyRuleJpaEntity> entities =
                queryDslRepository.findByArchitectureId(architectureId.value());
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 슬라이스 조건으로 레이어 의존성 규칙 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 레이어 의존성 규칙 목록
     */
    @Override
    public List<LayerDependencyRule> findBySliceCriteria(
            com.ryuqq.domain.layerdependency.query.LayerDependencyRuleSliceCriteria criteria) {
        List<LayerDependencyRuleJpaEntity> entities =
                queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id 레이어 의존성 규칙 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(LayerDependencyRuleId id) {
        return queryDslRepository.existsById(id.value());
    }
}
