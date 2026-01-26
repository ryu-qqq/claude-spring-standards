package com.ryuqq.application.archunittest.validator;

import com.ryuqq.application.archunittest.manager.ArchUnitTestReadManager;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.exception.ArchUnitTestDuplicateCodeException;
import com.ryuqq.domain.archunittest.exception.ArchUnitTestNotFoundException;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestValidator - ArchUnit 테스트 검증기
 *
 * <p>ArchUnit 테스트 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * <p>APP-VAL-001: findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestValidator {

    private final ArchUnitTestReadManager archUnitTestReadManager;

    public ArchUnitTestValidator(ArchUnitTestReadManager archUnitTestReadManager) {
        this.archUnitTestReadManager = archUnitTestReadManager;
    }

    /**
     * ArchUnit 테스트 조회 + 존재 검증 통합
     *
     * <p>APP-VAL-001: findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param archUnitTestId ArchUnit 테스트 ID
     * @return ArchUnitTest 도메인 객체
     * @throws ArchUnitTestNotFoundException ArchUnit 테스트가 존재하지 않으면
     */
    public ArchUnitTest findExistingOrThrow(ArchUnitTestId archUnitTestId) {
        return archUnitTestReadManager.getById(archUnitTestId);
    }

    /**
     * ArchUnit 테스트 존재 여부 검증
     *
     * @param archUnitTestId ArchUnit 테스트 ID
     * @throws ArchUnitTestNotFoundException ArchUnit 테스트가 존재하지 않으면
     */
    public void validateExists(ArchUnitTestId archUnitTestId) {
        archUnitTestReadManager.getById(archUnitTestId);
    }

    /**
     * 테스트 코드 중복 검증 (생성 시)
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @throws ArchUnitTestDuplicateCodeException 동일 코드의 테스트가 존재하면
     */
    public void validateNotDuplicate(PackageStructureId structureId, String code) {
        if (archUnitTestReadManager.existsByStructureIdAndCode(structureId, code)) {
            throw new ArchUnitTestDuplicateCodeException(structureId, code);
        }
    }

    /**
     * 테스트 코드 중복 검증 (수정 시, 자신 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @param excludeArchUnitTestId 제외할 ArchUnit 테스트 ID
     * @throws ArchUnitTestDuplicateCodeException 동일 코드의 다른 테스트가 존재하면
     */
    public void validateNotDuplicateExcluding(
            PackageStructureId structureId, String code, ArchUnitTestId excludeArchUnitTestId) {
        if (archUnitTestReadManager.existsByStructureIdAndCodeExcluding(
                structureId, code, excludeArchUnitTestId)) {
            throw new ArchUnitTestDuplicateCodeException(structureId, code);
        }
    }
}
