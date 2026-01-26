package com.ryuqq.application.classtype.manager;

import com.ryuqq.application.classtype.port.out.ClassTypeQueryPort;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassTypeReadManager - ClassType 조회 관리자
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
public class ClassTypeReadManager {

    private final ClassTypeQueryPort classTypeQueryPort;

    public ClassTypeReadManager(ClassTypeQueryPort classTypeQueryPort) {
        this.classTypeQueryPort = classTypeQueryPort;
    }

    /**
     * ID로 ClassType 조회
     *
     * @param id ClassType ID (VO)
     * @return ClassType (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<ClassType> findById(ClassTypeId id) {
        return classTypeQueryPort.findById(id);
    }

    /**
     * 슬라이스 조건으로 ClassType 목록 조회
     *
     * @param criteria 슬라이스 조건
     * @return ClassType 목록
     */
    @Transactional(readOnly = true)
    public List<ClassType> findBySliceCriteria(ClassTypeSliceCriteria criteria) {
        return classTypeQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 카테고리 내 코드 중복 확인
     *
     * @param categoryId 카테고리 ID (VO)
     * @param code 클래스 타입 코드 (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByCategoryIdAndCode(ClassTypeCategoryId categoryId, ClassTypeCode code) {
        return classTypeQueryPort.existsByCategoryIdAndCode(categoryId, code);
    }

    /**
     * ID를 제외한 코드 중복 확인 (수정 시 사용)
     *
     * @param categoryId 카테고리 ID (VO)
     * @param code 클래스 타입 코드 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByCategoryIdAndCodeAndIdNot(
            ClassTypeCategoryId categoryId, ClassTypeCode code, ClassTypeId excludeId) {
        return classTypeQueryPort.existsByCategoryIdAndCodeAndIdNot(categoryId, code, excludeId);
    }
}
