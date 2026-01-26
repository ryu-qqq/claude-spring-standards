package com.ryuqq.application.classtypecategory.port.out;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import java.util.List;
import java.util.Optional;

/**
 * ClassTypeCategoryQueryPort - ClassTypeCategory 조회 Port
 *
 * <p>영속성 계층으로의 ClassTypeCategory 조회 아웃바운드 포트입니다.
 *
 * <p>QPRT-002: 표준 메서드를 제공합니다.
 *
 * <p>QPRT-003: Unique 필드(code) 조회 메서드를 추가 제공합니다.
 *
 * <p>QPRT-004: 원시타입 대신 VO를 파라미터로 사용합니다.
 *
 * @author ryu-qqq
 */
public interface ClassTypeCategoryQueryPort {

    /**
     * ID로 ClassTypeCategory 조회
     *
     * @param id ClassTypeCategory ID (VO)
     * @return ClassTypeCategory (Optional)
     */
    Optional<ClassTypeCategory> findById(ClassTypeCategoryId id);

    /**
     * ID로 존재 여부 확인
     *
     * @param id ClassTypeCategory ID (VO)
     * @return 존재 여부
     */
    boolean existsById(ClassTypeCategoryId id);

    /**
     * 커서 기반 슬라이스 조건으로 ClassTypeCategory 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ClassTypeCategory 목록
     */
    List<ClassTypeCategory> findBySliceCriteria(ClassTypeCategorySliceCriteria criteria);

    /**
     * 아키텍처 내 코드 중복 체크 (QPRT-003: Unique 필드)
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 체크할 코드 (VO)
     * @return 중복 여부
     */
    boolean existsByArchitectureIdAndCode(ArchitectureId architectureId, CategoryCode code);

    /**
     * ID를 제외한 코드 중복 체크 (수정 시 사용)
     *
     * @param architectureId 아키텍처 ID (VO)
     * @param code 체크할 코드 (VO)
     * @param excludeId 제외할 ID (VO)
     * @return 중복 여부
     */
    boolean existsByArchitectureIdAndCodeAndIdNot(
            ArchitectureId architectureId, CategoryCode code, ClassTypeCategoryId excludeId);

    /**
     * Architecture에 속한 ClassTypeCategory 존재 여부 확인
     *
     * <p>Architecture 삭제 시 자식 확인을 위해 사용합니다.
     *
     * @param architectureId Architecture ID (VO)
     * @return 자식 존재 여부
     */
    boolean existsByArchitectureId(ArchitectureId architectureId);
}
