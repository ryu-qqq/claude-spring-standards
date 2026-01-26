package com.ryuqq.application.packagestructure.validator;

import com.ryuqq.application.packagestructure.manager.PackageStructureReadManager;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.exception.PackageStructureDuplicateException;
import com.ryuqq.domain.packagestructure.exception.PackageStructureNotFoundException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import org.springframework.stereotype.Component;

/**
 * PackageStructureValidator - 패키지 구조 검증기
 *
 * <p>패키지 구조 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class PackageStructureValidator {

    private final PackageStructureReadManager packageStructureReadManager;

    public PackageStructureValidator(PackageStructureReadManager packageStructureReadManager) {
        this.packageStructureReadManager = packageStructureReadManager;
    }

    /**
     * 패키지 구조 존재 여부 검증 후 반환 (조회 + 검증 통합)
     *
     * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param packageStructureId 패키지 구조 ID
     * @return 존재하는 PackageStructure
     * @throws PackageStructureNotFoundException 패키지 구조가 존재하지 않으면
     */
    public PackageStructure findExistingOrThrow(PackageStructureId packageStructureId) {
        return packageStructureReadManager.getById(packageStructureId);
    }

    /**
     * 패키지 구조 존재 여부 검증
     *
     * @param packageStructureId 패키지 구조 ID
     * @throws PackageStructureNotFoundException 패키지 구조가 존재하지 않으면
     */
    public void validateExists(PackageStructureId packageStructureId) {
        packageStructureReadManager.getById(packageStructureId);
    }

    /**
     * 경로 패턴 중복 검증 (생성 시)
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @throws PackageStructureDuplicateException 동일 경로 패턴의 구조가 존재하면
     */
    public void validateNotDuplicate(ModuleId moduleId, PathPattern pathPattern) {
        if (packageStructureReadManager.existsByModuleIdAndPathPattern(moduleId, pathPattern)) {
            throw new PackageStructureDuplicateException(moduleId.value(), pathPattern.value());
        }
    }

    /**
     * 경로 패턴 중복 검증 (수정 시, 자신 제외)
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @param excludePackageStructureId 제외할 패키지 구조 ID
     * @throws PackageStructureDuplicateException 동일 경로 패턴의 다른 구조가 존재하면
     */
    public void validateNotDuplicateExcluding(
            ModuleId moduleId,
            PathPattern pathPattern,
            PackageStructureId excludePackageStructureId) {
        if (packageStructureReadManager.existsByModuleIdAndPathPatternExcluding(
                moduleId, pathPattern, excludePackageStructureId)) {
            throw new PackageStructureDuplicateException(moduleId.value(), pathPattern.value());
        }
    }
}
