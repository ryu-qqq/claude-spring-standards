package com.ryuqq.domain.archunittest.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.Map;

/**
 * ArchUnitTestDuplicateCodeException - ArchUnit 테스트 코드 중복 예외
 *
 * <p>동일한 패키지 구조 내에서 테스트 코드가 이미 존재할 때 발생합니다.
 *
 * @author ryu-qqq
 */
public class ArchUnitTestDuplicateCodeException extends DomainException {

    public ArchUnitTestDuplicateCodeException(PackageStructureId structureId, String code) {
        super(
                ArchUnitTestErrorCode.ARCH_UNIT_TEST_DUPLICATE_CODE,
                String.format(
                        "ArchUnitTest code '%s' already exists in package structure: %d",
                        code, structureId.value()),
                Map.of("structureId", structureId.value(), "code", code));
    }
}
