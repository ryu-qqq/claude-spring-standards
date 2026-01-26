package com.ryuqq.application.packagepurpose.port.out;

import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.Optional;

/**
 * PackagePurposeQueryPort - 패키지 목적 조회 아웃바운드 포트
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 */
public interface PackagePurposeQueryPort {

    /**
     * ID로 패키지 목적 조회
     *
     * @param id 패키지 목적 ID
     * @return 패키지 목적 Optional
     */
    Optional<PackagePurpose> findById(Long id);

    /**
     * PackagePurposeId로 패키지 목적 조회
     *
     * @param packagePurposeId 패키지 목적 ID
     * @return 패키지 목적 Optional
     */
    Optional<PackagePurpose> findById(PackagePurposeId packagePurposeId);

    /**
     * 슬라이스 조건으로 PackagePurpose 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return PackagePurpose 목록
     */
    List<PackagePurpose> findBySliceCriteria(PackagePurposeSliceCriteria criteria);

    /**
     * 패키지 구조 ID와 코드로 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @return 존재하면 true
     */
    boolean existsByStructureIdAndCode(PackageStructureId structureId, PurposeCode code);

    /**
     * 패키지 구조 ID와 코드로 존재 여부 확인 (특정 ID 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @param excludeId 제외할 패키지 목적 ID
     * @return 존재하면 true
     */
    boolean existsByStructureIdAndCodeExcluding(
            PackageStructureId structureId, PurposeCode code, PackagePurposeId excludeId);
}
