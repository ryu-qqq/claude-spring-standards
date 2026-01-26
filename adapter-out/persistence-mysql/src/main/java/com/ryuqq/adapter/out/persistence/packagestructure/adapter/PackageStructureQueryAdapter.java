package com.ryuqq.adapter.out.persistence.packagestructure.adapter;

import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import com.ryuqq.adapter.out.persistence.packagestructure.mapper.PackageStructureJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.packagestructure.repository.PackageStructureQueryDslRepository;
import com.ryuqq.application.packagestructure.port.out.PackageStructureQueryPort;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * PackageStructureQueryAdapter - 패키지 구조 조회 어댑터
 *
 * <p>PackageStructureQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class PackageStructureQueryAdapter implements PackageStructureQueryPort {

    private final PackageStructureQueryDslRepository queryDslRepository;
    private final PackageStructureJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public PackageStructureQueryAdapter(
            PackageStructureQueryDslRepository queryDslRepository,
            PackageStructureJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 패키지 구조 조회
     *
     * @param id 패키지 구조 ID
     * @return 패키지 구조 Optional
     */
    @Override
    public Optional<PackageStructure> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * PackageStructureId로 패키지 구조 조회
     *
     * @param packageStructureId 패키지 구조 ID
     * @return 패키지 구조 Optional
     */
    @Override
    public Optional<PackageStructure> findById(PackageStructureId packageStructureId) {
        return queryDslRepository.findById(packageStructureId.value()).map(mapper::toDomain);
    }

    /**
     * Module ID로 패키지 구조 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 패키지 구조 목록
     */
    @Override
    public List<PackageStructure> findByModuleId(Long moduleId) {
        List<PackageStructureJpaEntity> entities = queryDslRepository.findByModuleId(moduleId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * ModuleId 값 객체로 패키지 구조 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 패키지 구조 목록
     */
    @Override
    public List<PackageStructure> findByModuleId(ModuleId moduleId) {
        return findByModuleId(moduleId.value());
    }

    /**
     * 슬라이스 조건으로 패키지 구조 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 패키지 구조 목록
     */
    @Override
    public List<PackageStructure> findBySliceCriteria(PackageStructureSliceCriteria criteria) {
        List<PackageStructureJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 모듈 내 경로 패턴 존재 여부 확인
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @return 존재하면 true
     */
    @Override
    public boolean existsByModuleIdAndPathPattern(Long moduleId, String pathPattern) {
        return queryDslRepository.existsByModuleIdAndPathPattern(moduleId, pathPattern);
    }

    /**
     * 모듈 내 경로 패턴 존재 여부 확인 (VO 사용)
     *
     * @param moduleId 모듈 ID VO
     * @param pathPattern 경로 패턴 VO
     * @return 존재하면 true
     */
    @Override
    public boolean existsByModuleIdAndPathPattern(ModuleId moduleId, PathPattern pathPattern) {
        return queryDslRepository.existsByModuleIdAndPathPattern(
                moduleId.value(), pathPattern.value());
    }

    /**
     * 모듈 내 경로 패턴 존재 여부 확인 (특정 구조 제외)
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @param excludeId 제외할 패키지 구조 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByModuleIdAndPathPatternExcluding(
            Long moduleId, String pathPattern, Long excludeId) {
        return queryDslRepository.existsByModuleIdAndPathPatternExcluding(
                moduleId, pathPattern, excludeId);
    }

    /**
     * 모듈 내 경로 패턴 존재 여부 확인 (특정 구조 제외, VO 사용)
     *
     * @param moduleId 모듈 ID VO
     * @param pathPattern 경로 패턴 VO
     * @param excludePackageStructureId 제외할 패키지 구조 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByModuleIdAndPathPatternExcluding(
            ModuleId moduleId,
            PathPattern pathPattern,
            PackageStructureId excludePackageStructureId) {
        return queryDslRepository.existsByModuleIdAndPathPatternExcluding(
                moduleId.value(), pathPattern.value(), excludePackageStructureId.value());
    }

    /**
     * 전체 패키지 구조 목록 조회
     *
     * @return 패키지 구조 목록
     */
    @Override
    public List<PackageStructure> findAll() {
        List<PackageStructureJpaEntity> entities = queryDslRepository.searchAll();
        return entities.stream().map(mapper::toDomain).toList();
    }
}
