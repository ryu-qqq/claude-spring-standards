package com.ryuqq.application.archunittest.manager;

import com.ryuqq.application.archunittest.port.out.ArchUnitTestQueryPort;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.exception.ArchUnitTestNotFoundException;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ArchUnitTestReadManager - ArchUnit 테스트 조회 관리자
 *
 * <p>ArchUnit 테스트 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestReadManager {

    private final ArchUnitTestQueryPort archUnitTestQueryPort;

    public ArchUnitTestReadManager(ArchUnitTestQueryPort archUnitTestQueryPort) {
        this.archUnitTestQueryPort = archUnitTestQueryPort;
    }

    /**
     * ID로 ArchUnit 테스트 조회 (존재하지 않으면 예외)
     *
     * @param archUnitTestId ArchUnit 테스트 ID
     * @return ArchUnit 테스트
     * @throws ArchUnitTestNotFoundException ArchUnit 테스트가 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public ArchUnitTest getById(ArchUnitTestId archUnitTestId) {
        return archUnitTestQueryPort
                .findById(archUnitTestId)
                .orElseThrow(() -> new ArchUnitTestNotFoundException(archUnitTestId.value()));
    }

    /**
     * ID로 ArchUnit 테스트 존재 여부 확인 후 반환
     *
     * @param archUnitTestId ArchUnit 테스트 ID
     * @return ArchUnit 테스트 (nullable)
     */
    @Transactional(readOnly = true)
    public ArchUnitTest findById(ArchUnitTestId archUnitTestId) {
        return archUnitTestQueryPort.findById(archUnitTestId).orElse(null);
    }

    /**
     * 슬라이스 조건으로 ArchUnit 테스트 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return ArchUnit 테스트 목록
     */
    @Transactional(readOnly = true)
    public List<ArchUnitTest> findBySliceCriteria(ArchUnitTestSliceCriteria criteria) {
        return archUnitTestQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 패키지 구조 내 테스트 코드 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByStructureIdAndCode(PackageStructureId structureId, String code) {
        return archUnitTestQueryPort.existsByStructureIdAndCode(structureId, code);
    }

    /**
     * 패키지 구조 내 테스트 코드 존재 여부 확인 (특정 테스트 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @param excludeArchUnitTestId 제외할 ArchUnit 테스트 ID
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByStructureIdAndCodeExcluding(
            PackageStructureId structureId, String code, ArchUnitTestId excludeArchUnitTestId) {
        return archUnitTestQueryPort.existsByStructureIdAndCodeExcluding(
                structureId, code, excludeArchUnitTestId);
    }
}
