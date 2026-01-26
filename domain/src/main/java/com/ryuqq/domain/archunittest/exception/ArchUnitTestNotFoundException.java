package com.ryuqq.domain.archunittest.exception;

import com.ryuqq.domain.common.exception.DomainException;

/**
 * ArchUnitTestNotFoundException - ArchUnit 테스트 미존재 예외
 *
 * <p>요청한 ArchUnit 테스트를 찾을 수 없을 때 발생합니다.
 *
 * @author ryu-qqq
 * @since 2026-01-06
 */
public class ArchUnitTestNotFoundException extends DomainException {

    private final Long archUnitTestId;

    public ArchUnitTestNotFoundException(Long archUnitTestId) {
        super(
                ArchUnitTestErrorCode.ARCH_UNIT_TEST_NOT_FOUND,
                String.format("ArchUnitTest not found with id: %s", archUnitTestId));
        this.archUnitTestId = archUnitTestId;
    }

    public Long getArchUnitTestId() {
        return archUnitTestId;
    }
}
