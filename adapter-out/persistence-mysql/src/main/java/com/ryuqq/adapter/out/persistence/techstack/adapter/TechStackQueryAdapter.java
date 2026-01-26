package com.ryuqq.adapter.out.persistence.techstack.adapter;

import com.ryuqq.adapter.out.persistence.techstack.entity.TechStackJpaEntity;
import com.ryuqq.adapter.out.persistence.techstack.mapper.TechStackEntityMapper;
import com.ryuqq.adapter.out.persistence.techstack.repository.TechStackQueryDslRepository;
import com.ryuqq.application.techstack.port.out.TechStackQueryPort;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import com.ryuqq.domain.techstack.vo.TechStackName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * TechStackQueryAdapter - TechStack 조회 어댑터
 *
 * <p>TechStackQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class TechStackQueryAdapter implements TechStackQueryPort {

    private final TechStackQueryDslRepository queryDslRepository;
    private final TechStackEntityMapper mapper;

    public TechStackQueryAdapter(
            TechStackQueryDslRepository queryDslRepository, TechStackEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 TechStack 조회
     *
     * @param id TechStack ID (VO)
     * @return TechStack (Optional)
     */
    @Override
    public Optional<TechStack> findById(TechStackId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id TechStack ID (VO)
     * @return 존재 여부
     */
    @Override
    public boolean existsById(TechStackId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 이름 중복 체크
     *
     * @param name 체크할 이름 (VO)
     * @return 중복 여부
     */
    @Override
    public boolean existsByName(TechStackName name) {
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
    public boolean existsByNameAndIdNot(TechStackName name, TechStackId excludeId) {
        return queryDslRepository.existsByNameAndIdNot(name.value(), excludeId.value());
    }

    /**
     * 슬라이스 조건으로 TechStack 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return TechStack 목록
     */
    @Override
    public List<TechStack> findBySliceCriteria(TechStackSliceCriteria criteria) {
        List<TechStackJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
