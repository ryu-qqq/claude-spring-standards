package com.ryuqq.adapter.out.persistence.archunittest.adapter;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.adapter.out.persistence.archunittest.mapper.ArchUnitTestJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.archunittest.repository.ArchUnitTestQueryDslRepository;
import com.ryuqq.application.archunittest.port.out.ArchUnitTestQueryPort;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestQueryAdapter - ArchUnit 테스트 조회 어댑터
 *
 * <p>ArchUnitTestQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class ArchUnitTestQueryAdapter implements ArchUnitTestQueryPort {

    private final ArchUnitTestQueryDslRepository queryDslRepository;
    private final ArchUnitTestJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ArchUnitTestQueryAdapter(
            ArchUnitTestQueryDslRepository queryDslRepository, ArchUnitTestJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 ArchUnit 테스트 조회
     *
     * @param id ArchUnit 테스트 ID
     * @return ArchUnit 테스트 Optional
     */
    @Override
    public Optional<ArchUnitTest> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * ArchUnitTestId로 ArchUnit 테스트 조회
     *
     * @param archUnitTestId ArchUnit 테스트 ID
     * @return ArchUnit 테스트 Optional
     */
    @Override
    public Optional<ArchUnitTest> findById(ArchUnitTestId archUnitTestId) {
        return queryDslRepository.findById(archUnitTestId.value()).map(mapper::toDomain);
    }

    /**
     * 코드로 ArchUnit 테스트 조회
     *
     * @param code 테스트 코드
     * @return ArchUnit 테스트 Optional
     */
    @Override
    public Optional<ArchUnitTest> findByCode(String code) {
        return queryDslRepository.findByCode(code).map(mapper::toDomain);
    }

    /**
     * 패키지 구조 ID로 ArchUnit 테스트 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return ArchUnit 테스트 목록
     */
    @Override
    public List<ArchUnitTest> findByStructureId(Long structureId) {
        List<ArchUnitTestJpaEntity> entities = queryDslRepository.findByStructureId(structureId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * PackageStructureId로 ArchUnit 테스트 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return ArchUnit 테스트 목록
     */
    @Override
    public List<ArchUnitTest> findByStructureId(PackageStructureId structureId) {
        return findByStructureId(structureId.value());
    }

    /**
     * 심각도로 ArchUnit 테스트 목록 조회
     *
     * @param severity 심각도
     * @return ArchUnit 테스트 목록
     */
    @Override
    public List<ArchUnitTest> findBySeverity(String severity) {
        List<ArchUnitTestJpaEntity> entities = queryDslRepository.findBySeverity(severity);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 슬라이스 조건으로 ArchUnit 테스트 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return ArchUnit 테스트 목록
     */
    @Override
    public List<ArchUnitTest> findBySliceCriteria(ArchUnitTestSliceCriteria criteria) {
        List<ArchUnitTestJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);

        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 패키지 구조 내 코드 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @return 존재하면 true
     */
    @Override
    public boolean existsByStructureIdAndCode(PackageStructureId structureId, String code) {
        return queryDslRepository.existsByStructureIdAndCode(structureId.value(), code);
    }

    /**
     * 패키지 구조 내 코드 존재 여부 확인 (특정 테스트 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @param excludeArchUnitTestId 제외할 ArchUnit 테스트 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByStructureIdAndCodeExcluding(
            PackageStructureId structureId, String code, ArchUnitTestId excludeArchUnitTestId) {
        return queryDslRepository.existsByStructureIdAndCodeExcluding(
                structureId.value(), code, excludeArchUnitTestId.value());
    }
}
