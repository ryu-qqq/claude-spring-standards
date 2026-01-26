package com.ryuqq.application.packagepurpose.validator;

import com.ryuqq.application.packagepurpose.manager.PackagePurposeReadManager;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurposeUpdateData;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeDuplicateCodeException;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeValidator - 패키지 목적 검증기
 *
 * <p>패키지 목적 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposeValidator {

    private final PackagePurposeReadManager packagePurposeReadManager;

    public PackagePurposeValidator(PackagePurposeReadManager packagePurposeReadManager) {
        this.packagePurposeReadManager = packagePurposeReadManager;
    }

    /**
     * 패키지 목적 존재 여부 검증 후 반환 (조회 + 검증 통합)
     *
     * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param packagePurposeId 패키지 목적 ID
     * @return 존재하는 PackagePurpose
     */
    public PackagePurpose findExistingOrThrow(PackagePurposeId packagePurposeId) {
        return packagePurposeReadManager.getById(packagePurposeId);
    }

    /**
     * 목적 코드 중복 검증 (생성 시)
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @throws PackagePurposeDuplicateCodeException 동일 코드의 목적이 존재하면
     */
    public void validateNotDuplicate(PackageStructureId structureId, PurposeCode code) {
        if (packagePurposeReadManager.existsByStructureIdAndCode(structureId, code)) {
            throw new PackagePurposeDuplicateCodeException(structureId.value(), code.value());
        }
    }

    /**
     * 목적 코드 중복 검증 (수정 시, 자신 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @param excludeId 제외할 패키지 목적 ID
     * @throws PackagePurposeDuplicateCodeException 동일 코드의 다른 목적이 존재하면
     */
    public void validateNotDuplicateExcluding(
            PackageStructureId structureId, PurposeCode code, PackagePurposeId excludeId) {
        if (packagePurposeReadManager.existsByStructureIdAndCodeExcluding(
                structureId, code, excludeId)) {
            throw new PackagePurposeDuplicateCodeException(structureId.value(), code.value());
        }
    }

    /**
     * 패키지 목적 수정 검증 및 조회
     *
     * <p>ID로 조회하고, 업데이트 데이터의 코드가 중복되지 않는지 검증합니다. 검증이 통과하면 조회한 PackagePurpose를 반환합니다.
     *
     * @param packagePurposeId 패키지 목적 ID
     * @param updateData 업데이트 데이터
     * @return 검증 완료된 PackagePurpose
     * @throws PackagePurposeDuplicateCodeException 동일 코드의 다른 목적이 존재하면
     */
    public PackagePurpose validateAndGetForUpdate(
            PackagePurposeId packagePurposeId, PackagePurposeUpdateData updateData) {
        PackagePurpose packagePurpose = packagePurposeReadManager.getById(packagePurposeId);

        validateNotDuplicateExcluding(
                packagePurpose.structureId(), updateData.code(), packagePurposeId);

        return packagePurpose;
    }
}
