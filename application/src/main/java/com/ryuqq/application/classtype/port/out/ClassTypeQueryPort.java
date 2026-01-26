package com.ryuqq.application.classtype.port.out;

import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import java.util.List;
import java.util.Optional;

/**
 * ClassTypeQueryPort - ClassType 조회 Port
 *
 * <p>영속성 계층으로의 ClassType 조회 아웃바운드 포트입니다.
 *
 * <p>QPRT-002: 표준 메서드를 제공합니다.
 *
 * <p>QPRT-003: Unique 필드(code) 조회 메서드를 추가 제공합니다.
 *
 * <p>QPRT-004: 원시타입 대신 VO를 파라미터로 사용합니다.
 *
 * @author ryu-qqq
 */
public interface ClassTypeQueryPort {

    /**
     * ID로 ClassType 조회
     *
     * @param id ClassType ID (VO)
     * @return ClassType (Optional)
     */
    Optional<ClassType> findById(ClassTypeId id);

    /**
     * ID로 존재 여부 확인
     *
     * @param id ClassType ID (VO)
     * @return 존재 여부
     */
    boolean existsById(ClassTypeId id);

    /**
     * 커서 기반 슬라이스 조건으로 ClassType 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ClassType 목록
     */
    List<ClassType> findBySliceCriteria(ClassTypeSliceCriteria criteria);

    /**
     * 카테고리 내 코드 중복 체크 (QPRT-003: Unique 필드)
     *
     * @param categoryId 카테고리 ID (VO)
     * @param code 체크할 코드 (VO)
     * @return 중복 여부
     */
    boolean existsByCategoryIdAndCode(ClassTypeCategoryId categoryId, ClassTypeCode code);

    /**
     * ID를 제외한 코드 중복 체크 (수정 시 사용)
     *
     * @param categoryId 카테고리 ID (VO)
     * @param code 체크할 코드 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    boolean existsByCategoryIdAndCodeAndIdNot(
            ClassTypeCategoryId categoryId, ClassTypeCode code, ClassTypeId excludeId);

    /**
     * Category에 속한 ClassType 존재 여부 확인
     *
     * <p>Category 삭제 시 자식 확인을 위해 사용합니다.
     *
     * @param categoryId Category ID (VO)
     * @return 자식 존재 여부
     */
    boolean existsByCategoryId(ClassTypeCategoryId categoryId);
}
