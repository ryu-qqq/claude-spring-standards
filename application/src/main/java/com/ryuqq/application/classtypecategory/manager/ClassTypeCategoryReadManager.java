package com.ryuqq.application.classtypecategory.manager;

import com.ryuqq.application.classtypecategory.port.out.ClassTypeCategoryQueryPort;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassTypeCategoryReadManager - ClassTypeCategory 조회 관리자
 *
 * <p>QueryPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional(readOnly=true)은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTypeCategoryReadManager {

    private final ClassTypeCategoryQueryPort classTypeCategoryQueryPort;

    public ClassTypeCategoryReadManager(ClassTypeCategoryQueryPort classTypeCategoryQueryPort) {
        this.classTypeCategoryQueryPort = classTypeCategoryQueryPort;
    }

    /**
     * ID로 ClassTypeCategory 조회
     *
     * @param id ClassTypeCategory ID (VO)
     * @return ClassTypeCategory (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<ClassTypeCategory> findById(ClassTypeCategoryId id) {
        return classTypeCategoryQueryPort.findById(id);
    }

    /**
     * 슬라이스 조건으로 ClassTypeCategory 목록 조회
     *
     * @param criteria 슬라이스 조건
     * @return ClassTypeCategory 목록
     */
    @Transactional(readOnly = true)
    public List<ClassTypeCategory> findBySliceCriteria(ClassTypeCategorySliceCriteria criteria) {
        return classTypeCategoryQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 아키텍처 내 코드 중복 확인
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 카테고리 코드 (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByArchitectureIdAndCode(ArchitectureId architectureId, CategoryCode code) {
        return classTypeCategoryQueryPort.existsByArchitectureIdAndCode(architectureId, code);
    }

    /**
     * ID를 제외한 코드 중복 확인 (수정 시 사용)
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 카테고리 코드 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByArchitectureIdAndCodeAndIdNot(
            ArchitectureId architectureId, CategoryCode code, ClassTypeCategoryId excludeId) {
        return classTypeCategoryQueryPort.existsByArchitectureIdAndCodeAndIdNot(
                architectureId, code, excludeId);
    }
}
