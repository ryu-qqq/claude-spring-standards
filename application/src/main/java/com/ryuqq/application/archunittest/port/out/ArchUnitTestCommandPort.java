package com.ryuqq.application.archunittest.port.out;

import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;

/**
 * ArchUnitTestCommandPort - ArchUnit 테스트 명령 Port
 *
 * <p>영속성 계층으로의 ArchUnitTest CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ArchUnitTestCommandPort {

    /**
     * ArchUnitTest 영속화 (생성/수정/삭제)
     *
     * @param archUnitTest 영속화할 ArchUnitTest
     * @return 영속화된 ArchUnitTest ID
     */
    ArchUnitTestId persist(ArchUnitTest archUnitTest);
}
