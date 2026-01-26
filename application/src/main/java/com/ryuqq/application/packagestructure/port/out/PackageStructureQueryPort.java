package com.ryuqq.application.packagestructure.port.out;

import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.util.List;
import java.util.Optional;

/**
 * PackageStructureQueryPort - 패키지 구조 조회 아웃바운드 포트
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 */
public interface PackageStructureQueryPort {

    /**
     * ID로 패키지 구조 조회
     *
     * @param id 패키지 구조 ID
     * @return 패키지 구조 Optional
     */
    Optional<PackageStructure> findById(Long id);

    /**
     * PackageStructureId로 패키지 구조 조회
     *
     * @param packageStructureId 패키지 구조 ID
     * @return 패키지 구조 Optional
     */
    Optional<PackageStructure> findById(PackageStructureId packageStructureId);

    /**
     * Module ID로 패키지 구조 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 패키지 구조 목록
     */
    List<PackageStructure> findByModuleId(Long moduleId);

    /**
     * ModuleId 값 객체로 패키지 구조 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 패키지 구조 목록
     */
    List<PackageStructure> findByModuleId(ModuleId moduleId);

    /**
     * 슬라이스 조건으로 패키지 구조 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 패키지 구조 목록
     */
    List<PackageStructure> findBySliceCriteria(PackageStructureSliceCriteria criteria);

    /**
     * 모듈 내 경로 패턴 존재 여부 확인
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @return 존재하면 true
     */
    boolean existsByModuleIdAndPathPattern(Long moduleId, String pathPattern);

    /**
     * 모듈 내 경로 패턴 존재 여부 확인 (VO 사용)
     *
     * @param moduleId 모듈 ID VO
     * @param pathPattern 경로 패턴 VO
     * @return 존재하면 true
     */
    boolean existsByModuleIdAndPathPattern(ModuleId moduleId, PathPattern pathPattern);

    /**
     * 모듈 내 경로 패턴 존재 여부 확인 (특정 구조 제외)
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @param excludeId 제외할 패키지 구조 ID
     * @return 존재하면 true
     */
    boolean existsByModuleIdAndPathPatternExcluding(
            Long moduleId, String pathPattern, Long excludeId);

    /**
     * 모듈 내 경로 패턴 존재 여부 확인 (특정 구조 제외, VO 사용)
     *
     * @param moduleId 모듈 ID VO
     * @param pathPattern 경로 패턴 VO
     * @param excludePackageStructureId 제외할 패키지 구조 ID
     * @return 존재하면 true
     */
    boolean existsByModuleIdAndPathPatternExcluding(
            ModuleId moduleId,
            PathPattern pathPattern,
            PackageStructureId excludePackageStructureId);

    /**
     * 전체 패키지 구조 목록 조회
     *
     * @return 패키지 구조 목록
     */
    List<PackageStructure> findAll();
}
