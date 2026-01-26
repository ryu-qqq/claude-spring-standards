package com.ryuqq.adapter.out.persistence.architecture.adapter;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.adapter.out.persistence.architecture.mapper.ArchitectureEntityMapper;
import com.ryuqq.adapter.out.persistence.architecture.repository.ArchitectureQueryDslRepository;
import com.ryuqq.application.architecture.port.out.ArchitectureQueryPort;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ArchitectureQueryAdapter - Architecture 조회 어댑터
 *
 * <p>ArchitectureQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QPRT-002: 표준 4개 메서드 + Unique 필드 조회 메서드를 구현합니다.
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
public class ArchitectureQueryAdapter implements ArchitectureQueryPort {

    private final ArchitectureQueryDslRepository queryDslRepository;
    private final ArchitectureEntityMapper mapper;

    public ArchitectureQueryAdapter(
            ArchitectureQueryDslRepository queryDslRepository, ArchitectureEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 Architecture 조회
     *
     * @param id Architecture ID (VO)
     * @return Architecture (Optional)
     */
    @Override
    public Optional<Architecture> findById(ArchitectureId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id Architecture ID (VO)
     * @return 존재 여부
     */
    @Override
    public boolean existsById(ArchitectureId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 커서 기반 슬라이스 조건으로 Architecture 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return Architecture 목록
     */
    @Override
    public List<Architecture> findBySliceCriteria(ArchitectureSliceCriteria criteria) {
        List<ArchitectureJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 이름 중복 체크
     *
     * @param name 체크할 이름 (VO)
     * @return 중복 여부
     */
    @Override
    public boolean existsByName(ArchitectureName name) {
        return queryDslRepository.existsByName(name.value());
    }

    /**
     * ID를 제외한 이름 중복 체크 (수정 시 사용)
     *
     * @param name 체크할 이름 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    @Override
    public boolean existsByNameAndIdNot(ArchitectureName name, ArchitectureId excludeId) {
        return queryDslRepository.existsByNameAndIdNot(name.value(), excludeId.value());
    }

    /**
     * TechStack에 속한 Architecture 존재 여부 확인
     *
     * <p>TechStack 삭제 시 자식 확인을 위해 사용합니다.
     *
     * @param techStackId TechStack ID (VO)
     * @return 자식 존재 여부
     */
    @Override
    public boolean existsByTechStackId(TechStackId techStackId) {
        return queryDslRepository.existsByTechStackId(techStackId.value());
    }
}
