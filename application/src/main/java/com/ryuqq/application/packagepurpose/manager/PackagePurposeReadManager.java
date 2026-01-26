package com.ryuqq.application.packagepurpose.manager;

import com.ryuqq.application.packagepurpose.port.out.PackagePurposeQueryPort;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeNotFoundException;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackagePurposeReadManager - 패키지 목적 조회 관리자
 *
 * <p>패키지 목적 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposeReadManager {

    private final PackagePurposeQueryPort packagePurposeQueryPort;

    public PackagePurposeReadManager(PackagePurposeQueryPort packagePurposeQueryPort) {
        this.packagePurposeQueryPort = packagePurposeQueryPort;
    }

    /**
     * ID로 패키지 목적 조회 (존재하지 않으면 예외)
     *
     * @param packagePurposeId 패키지 목적 ID
     * @return 패키지 목적
     * @throws PackagePurposeNotFoundException 패키지 목적이 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public PackagePurpose getById(PackagePurposeId packagePurposeId) {
        return packagePurposeQueryPort
                .findById(packagePurposeId)
                .orElseThrow(() -> new PackagePurposeNotFoundException(packagePurposeId.value()));
    }

    /**
     * 슬라이스 조건으로 PackagePurpose 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return PackagePurpose 목록
     */
    @Transactional(readOnly = true)
    public List<PackagePurpose> findBySliceCriteria(PackagePurposeSliceCriteria criteria) {
        return packagePurposeQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 패키지 구조 ID와 코드로 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByStructureIdAndCode(PackageStructureId structureId, PurposeCode code) {
        return packagePurposeQueryPort.existsByStructureIdAndCode(structureId, code);
    }

    /**
     * 패키지 구조 ID와 코드로 존재 여부 확인 (특정 ID 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @param excludeId 제외할 패키지 목적 ID
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByStructureIdAndCodeExcluding(
            PackageStructureId structureId, PurposeCode code, PackagePurposeId excludeId) {
        return packagePurposeQueryPort.existsByStructureIdAndCodeExcluding(
                structureId, code, excludeId);
    }
}
