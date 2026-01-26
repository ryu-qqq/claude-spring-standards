package com.ryuqq.adapter.out.persistence.classtypecategory.adapter;

import com.ryuqq.adapter.out.persistence.classtypecategory.entity.ClassTypeCategoryJpaEntity;
import com.ryuqq.adapter.out.persistence.classtypecategory.mapper.ClassTypeCategoryEntityMapper;
import com.ryuqq.adapter.out.persistence.classtypecategory.repository.ClassTypeCategoryQueryDslRepository;
import com.ryuqq.application.classtypecategory.port.out.ClassTypeCategoryQueryPort;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryQueryAdapter - ClassTypeCategory 조회 어댑터
 *
 * <p>ClassTypeCategoryQueryPort를 구현하여 영속성 계층과 연결합니다.
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
 * @since 1.0.0
 */
@Component
public class ClassTypeCategoryQueryAdapter implements ClassTypeCategoryQueryPort {

    private final ClassTypeCategoryQueryDslRepository queryDslRepository;
    private final ClassTypeCategoryEntityMapper mapper;

    public ClassTypeCategoryQueryAdapter(
            ClassTypeCategoryQueryDslRepository queryDslRepository,
            ClassTypeCategoryEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 ClassTypeCategory 조회
     *
     * @param id ClassTypeCategory ID (VO)
     * @return ClassTypeCategory (Optional)
     */
    @Override
    public Optional<ClassTypeCategory> findById(ClassTypeCategoryId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id ClassTypeCategory ID (VO)
     * @return 존재 여부
     */
    @Override
    public boolean existsById(ClassTypeCategoryId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 커서 기반 슬라이스 조건으로 ClassTypeCategory 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ClassTypeCategory 목록
     */
    @Override
    public List<ClassTypeCategory> findBySliceCriteria(ClassTypeCategorySliceCriteria criteria) {
        List<ClassTypeCategoryJpaEntity> entities =
                queryDslRepository.findBySliceCriteria(criteria);
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
    public boolean existsByArchitectureIdAndCode(ArchitectureId architectureId, CategoryCode code) {
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
            ArchitectureId architectureId, CategoryCode code, ClassTypeCategoryId excludeId) {
        return queryDslRepository.existsByArchitectureIdAndCodeAndIdNot(
                architectureId.value(), code.value(), excludeId.value());
    }

    /**
     * Architecture에 속한 ClassTypeCategory 존재 여부 확인
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
