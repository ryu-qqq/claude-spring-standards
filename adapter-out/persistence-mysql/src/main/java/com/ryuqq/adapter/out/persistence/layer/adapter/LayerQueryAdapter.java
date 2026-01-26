package com.ryuqq.adapter.out.persistence.layer.adapter;

import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import com.ryuqq.adapter.out.persistence.layer.mapper.LayerEntityMapper;
import com.ryuqq.adapter.out.persistence.layer.repository.LayerQueryDslRepository;
import com.ryuqq.application.layer.port.out.LayerQueryPort;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import com.ryuqq.domain.layer.vo.LayerCode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * LayerQueryAdapter - Layer 조회 어댑터
 *
 * <p>LayerQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QPRT-002: 표준 메서드 + Unique 필드 조회 메서드를 구현합니다.
 *
 * <p><strong>주의사항:</strong>
 *
 * <ul>
 *   <li>QueryAdapter는 QueryDslRepository에만 의존합니다 (JpaRepository 금지)
 *   <li>JpaRepository는 Command 작업을 수행할 수 있어 QueryAdapter에서 사용하면 안 됩니다
 * </ul>
 *
 * @author ryu-qqq
 */
@Component
public class LayerQueryAdapter implements LayerQueryPort {

    private final LayerQueryDslRepository queryDslRepository;
    private final LayerEntityMapper mapper;

    public LayerQueryAdapter(LayerQueryDslRepository queryDslRepository, LayerEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 Layer 조회
     *
     * @param id Layer ID (VO)
     * @return Layer (Optional)
     */
    @Override
    public Optional<Layer> findById(LayerId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id Layer ID (VO)
     * @return 존재 여부
     */
    @Override
    public boolean existsById(LayerId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 커서 기반 슬라이스 조건으로 Layer 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return Layer 목록
     */
    @Override
    public List<Layer> findBySliceCriteria(LayerSliceCriteria criteria) {
        List<LayerJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 아키텍처 내 코드 중복 체크
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 체크할 코드 (VO)
     * @return 중복 여부
     */
    @Override
    public boolean existsByArchitectureIdAndCode(ArchitectureId architectureId, LayerCode code) {
        return queryDslRepository.existsByArchitectureIdAndCode(
                architectureId.value(), code.value());
    }

    /**
     * ID를 제외한 코드 중복 체크 (수정 시 사용)
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 체크할 코드 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    @Override
    public boolean existsByArchitectureIdAndCodeAndIdNot(
            ArchitectureId architectureId, LayerCode code, LayerId excludeId) {
        return queryDslRepository.existsByArchitectureIdAndCodeAndIdNot(
                architectureId.value(), code.value(), excludeId.value());
    }

    /**
     * Architecture에 속한 Layer 존재 여부 확인
     *
     * <p>Architecture 삭제 시 자식 확인을 위해 사용합니다.
     *
     * @param architectureId Architecture ID (VO)
     * @return 자식 존재 여부
     */
    @Override
    public boolean existsByArchitectureId(ArchitectureId architectureId) {
        return queryDslRepository.existsByArchitectureId(architectureId.value());
    }
}
