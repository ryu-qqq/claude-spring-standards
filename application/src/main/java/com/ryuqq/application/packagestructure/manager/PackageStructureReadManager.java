package com.ryuqq.application.packagestructure.manager;

import com.ryuqq.application.packagestructure.port.out.PackageStructureQueryPort;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.exception.PackageStructureNotFoundException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageStructureReadManager - 패키지 구조 조회 관리자
 *
 * <p>패키지 구조 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class PackageStructureReadManager {

    private final PackageStructureQueryPort packageStructureQueryPort;

    public PackageStructureReadManager(PackageStructureQueryPort packageStructureQueryPort) {
        this.packageStructureQueryPort = packageStructureQueryPort;
    }

    /**
     * ID로 패키지 구조 조회 (존재하지 않으면 예외)
     *
     * @param packageStructureId 패키지 구조 ID
     * @return 패키지 구조
     * @throws PackageStructureNotFoundException 패키지 구조가 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public PackageStructure getById(PackageStructureId packageStructureId) {
        return packageStructureQueryPort
                .findById(packageStructureId)
                .orElseThrow(
                        () -> new PackageStructureNotFoundException(packageStructureId.value()));
    }

    /**
     * ID로 패키지 구조 존재 여부 확인 후 반환
     *
     * @param packageStructureId 패키지 구조 ID
     * @return 패키지 구조 (nullable)
     */
    @Transactional(readOnly = true)
    public PackageStructure findById(PackageStructureId packageStructureId) {
        return packageStructureQueryPort.findById(packageStructureId).orElse(null);
    }

    /**
     * 슬라이스 조건으로 패키지 구조 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 패키지 구조 목록
     */
    @Transactional(readOnly = true)
    public List<PackageStructure> findBySliceCriteria(PackageStructureSliceCriteria criteria) {
        return packageStructureQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 모듈 내 경로 패턴 존재 여부 확인
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByModuleIdAndPathPattern(ModuleId moduleId, PathPattern pathPattern) {
        return packageStructureQueryPort.existsByModuleIdAndPathPattern(moduleId, pathPattern);
    }

    /**
     * 모듈 내 경로 패턴 존재 여부 확인 (특정 구조 제외)
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @param excludePackageStructureId 제외할 패키지 구조 ID
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByModuleIdAndPathPatternExcluding(
            ModuleId moduleId,
            PathPattern pathPattern,
            PackageStructureId excludePackageStructureId) {
        return packageStructureQueryPort.existsByModuleIdAndPathPatternExcluding(
                moduleId, pathPattern, excludePackageStructureId);
    }

    /**
     * 모듈 ID로 패키지 구조 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 패키지 구조 목록
     */
    @Transactional(readOnly = true)
    public List<PackageStructure> findByModuleId(ModuleId moduleId) {
        return packageStructureQueryPort.findByModuleId(moduleId);
    }
}
