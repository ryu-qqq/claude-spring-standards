package com.ryuqq.application.archunittest.port.out;

import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.Optional;

/**
 * ArchUnitTestQueryPort - ArchUnit 테스트 조회 아웃바운드 포트
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 */
public interface ArchUnitTestQueryPort {

    /**
     * ID로 ArchUnit 테스트 조회
     *
     * @param id ArchUnit 테스트 ID
     * @return ArchUnit 테스트 Optional
     */
    Optional<ArchUnitTest> findById(Long id);

    /**
     * ArchUnitTestId로 ArchUnit 테스트 조회
     *
     * @param archUnitTestId ArchUnit 테스트 ID
     * @return ArchUnit 테스트 Optional
     */
    Optional<ArchUnitTest> findById(ArchUnitTestId archUnitTestId);

    /**
     * 코드로 ArchUnit 테스트 조회
     *
     * @param code 테스트 코드
     * @return ArchUnit 테스트 Optional
     */
    Optional<ArchUnitTest> findByCode(String code);

    /**
     * 패키지 구조 ID로 ArchUnit 테스트 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return ArchUnit 테스트 목록
     */
    List<ArchUnitTest> findByStructureId(Long structureId);

    /**
     * PackageStructureId로 ArchUnit 테스트 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return ArchUnit 테스트 목록
     */
    List<ArchUnitTest> findByStructureId(PackageStructureId structureId);

    /**
     * 심각도로 ArchUnit 테스트 목록 조회
     *
     * @param severity 심각도
     * @return ArchUnit 테스트 목록
     */
    List<ArchUnitTest> findBySeverity(String severity);

    /**
     * 슬라이스 조건으로 ArchUnit 테스트 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return ArchUnit 테스트 목록
     */
    List<ArchUnitTest> findBySliceCriteria(ArchUnitTestSliceCriteria criteria);

    /**
     * 패키지 구조 내 코드 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @return 존재하면 true
     */
    boolean existsByStructureIdAndCode(PackageStructureId structureId, String code);

    /**
     * 패키지 구조 내 코드 존재 여부 확인 (특정 테스트 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @param excludeArchUnitTestId 제외할 ArchUnit 테스트 ID
     * @return 존재하면 true
     */
    boolean existsByStructureIdAndCodeExcluding(
            PackageStructureId structureId, String code, ArchUnitTestId excludeArchUnitTestId);
}
