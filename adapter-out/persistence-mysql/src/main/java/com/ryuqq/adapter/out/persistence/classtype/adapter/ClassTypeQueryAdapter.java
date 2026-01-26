package com.ryuqq.adapter.out.persistence.classtype.adapter;

import com.ryuqq.adapter.out.persistence.classtype.entity.ClassTypeJpaEntity;
import com.ryuqq.adapter.out.persistence.classtype.mapper.ClassTypeEntityMapper;
import com.ryuqq.adapter.out.persistence.classtype.repository.ClassTypeQueryDslRepository;
import com.ryuqq.application.classtype.port.out.ClassTypeQueryPort;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ClassTypeQueryAdapter - ClassType 조회 어댑터
 *
 * <p>ClassTypeQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class ClassTypeQueryAdapter implements ClassTypeQueryPort {

    private final ClassTypeQueryDslRepository queryDslRepository;
    private final ClassTypeEntityMapper mapper;

    public ClassTypeQueryAdapter(
            ClassTypeQueryDslRepository queryDslRepository, ClassTypeEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 ClassType 조회
     *
     * @param id ClassType ID (VO)
     * @return ClassType (Optional)
     */
    @Override
    public Optional<ClassType> findById(ClassTypeId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id ClassType ID (VO)
     * @return 존재 여부
     */
    @Override
    public boolean existsById(ClassTypeId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 커서 기반 슬라이스 조건으로 ClassType 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ClassType 목록
     */
    @Override
    public List<ClassType> findBySliceCriteria(ClassTypeSliceCriteria criteria) {
        List<ClassTypeJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 카테고리 내 코드 중복 체크
     *
     * @param categoryId 카테고리 ID (VO)
     * @param code 체크할 코드 (VO)
     * @return 중복 여부
     */
    @Override
    public boolean existsByCategoryIdAndCode(ClassTypeCategoryId categoryId, ClassTypeCode code) {
        return queryDslRepository.existsByCategoryIdAndCode(categoryId.value(), code.value());
    }

    /**
     * ID를 제외한 코드 중복 체크 (수정 시 사용)
     *
     * @param categoryId 카테고리 ID (VO)
     * @param code 체크할 코드 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    @Override
    public boolean existsByCategoryIdAndCodeAndIdNot(
            ClassTypeCategoryId categoryId, ClassTypeCode code, ClassTypeId excludeId) {
        return queryDslRepository.existsByCategoryIdAndCodeAndIdNot(
                categoryId.value(), code.value(), excludeId.value());
    }

    /**
     * Category에 속한 ClassType 존재 여부 확인
     *
     * <p>Category 삭제 시 자식 확인을 위해 사용합니다.
     *
     * @param categoryId ClassTypeCategory ID (VO)
     * @return 자식 존재 여부
     */
    @Override
    public boolean existsByCategoryId(ClassTypeCategoryId categoryId) {
        return queryDslRepository.existsByCategoryId(categoryId.value());
    }
}
