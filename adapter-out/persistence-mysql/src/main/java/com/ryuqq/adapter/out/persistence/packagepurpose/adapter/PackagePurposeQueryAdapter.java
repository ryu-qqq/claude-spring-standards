package com.ryuqq.adapter.out.persistence.packagepurpose.adapter;

import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
import com.ryuqq.adapter.out.persistence.packagepurpose.mapper.PackagePurposeEntityMapper;
import com.ryuqq.adapter.out.persistence.packagepurpose.repository.PackagePurposeQueryDslRepository;
import com.ryuqq.application.packagepurpose.port.out.PackagePurposeQueryPort;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeQueryAdapter - 패키지 목적 조회 어댑터
 *
 * <p>PackagePurposeQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QADP-001: QueryDslRepository 위임만
 *
 * <p>QADP-002: QueryAdapter에서 @Transactional 금지
 *
 * <p>QADP-006: Domain 반환 (DTO 반환 금지)
 *
 * <p>QADP-007: Entity -> Domain 변환 (Mapper 사용)
 *
 * <p>QADP-008: QueryAdapter에 비즈니스 로직 금지
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposeQueryAdapter implements PackagePurposeQueryPort {

    private final PackagePurposeQueryDslRepository queryDslRepository;
    private final PackagePurposeEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public PackagePurposeQueryAdapter(
            PackagePurposeQueryDslRepository queryDslRepository,
            PackagePurposeEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 패키지 목적 조회
     *
     * @param id 패키지 목적 ID
     * @return 패키지 목적 Optional
     */
    @Override
    public Optional<PackagePurpose> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * PackagePurposeId로 패키지 목적 조회
     *
     * @param packagePurposeId 패키지 목적 ID
     * @return 패키지 목적 Optional
     */
    @Override
    public Optional<PackagePurpose> findById(PackagePurposeId packagePurposeId) {
        return queryDslRepository.findById(packagePurposeId.value()).map(mapper::toDomain);
    }

    /**
     * 슬라이스 조건으로 PackagePurpose 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return PackagePurpose 목록
     */
    @Override
    public List<PackagePurpose> findBySliceCriteria(PackagePurposeSliceCriteria criteria) {
        List<PackagePurposeJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 패키지 구조 ID와 코드로 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @return 존재하면 true
     */
    @Override
    public boolean existsByStructureIdAndCode(PackageStructureId structureId, PurposeCode code) {
        return queryDslRepository.existsByStructureIdAndCode(structureId.value(), code.value());
    }

    /**
     * 패키지 구조 ID와 코드로 존재 여부 확인 (특정 ID 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @param excludeId 제외할 패키지 목적 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByStructureIdAndCodeExcluding(
            PackageStructureId structureId, PurposeCode code, PackagePurposeId excludeId) {
        return queryDslRepository.existsByStructureIdAndCodeExcluding(
                structureId.value(), code.value(), excludeId.value());
    }
}
