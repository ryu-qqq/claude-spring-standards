package com.ryuqq.application.convention.port.out;

import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.List;
import java.util.Optional;

/**
 * ConventionQueryPort - Convention 조회 Port
 *
 * <p>영속성 계층으로의 Convention 조회 아웃바운드 포트입니다.
 *
 * @author ryu-qqq
 */
public interface ConventionQueryPort {

    /**
     * 전체 활성화된 컨벤션 조회
     *
     * @return 활성화된 컨벤션 목록
     */
    List<Convention> findAllActive();

    /**
     * 슬라이스 조건으로 Convention 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return Convention 목록
     */
    List<Convention> findBySliceCriteria(ConventionSliceCriteria criteria);

    /**
     * 모듈별 활성화된 컨벤션 조회
     *
     * @param moduleId 모듈 ID
     * @return 해당 모듈의 활성화된 컨벤션 (Optional)
     */
    Optional<Convention> findActiveByModuleId(ModuleId moduleId);

    /**
     * ID로 컨벤션 조회
     *
     * @param id 컨벤션 ID
     * @return 컨벤션 (Optional)
     */
    Optional<Convention> findById(Long id);

    /**
     * ID(VO)로 컨벤션 조회
     *
     * @param id 컨벤션 ID (VO)
     * @return 컨벤션 (Optional)
     */
    Optional<Convention> findById(ConventionId id);

    /**
     * ID로 존재 여부 확인
     *
     * @param id 컨벤션 ID (VO)
     * @return 존재 여부
     */
    boolean existsById(ConventionId id);

    /**
     * 모듈+버전 중복 체크
     *
     * @param moduleId 모듈 ID
     * @param version 컨벤션 버전
     * @return 중복 여부
     */
    boolean existsByModuleIdAndVersion(ModuleId moduleId, String version);

    /**
     * ID를 제외한 모듈+버전 중복 체크 (수정 시 사용)
     *
     * @param moduleId 모듈 ID
     * @param version 컨벤션 버전
     * @param excludeId 제외할 ID
     * @return 중복 여부
     */
    boolean existsByModuleIdAndVersionAndIdNot(
            ModuleId moduleId, String version, ConventionId excludeId);
}
