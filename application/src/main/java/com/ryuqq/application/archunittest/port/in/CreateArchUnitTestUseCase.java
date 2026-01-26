package com.ryuqq.application.archunittest.port.in;

import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;

/**
 * CreateArchUnitTestUseCase - ArchUnit 테스트 생성 UseCase
 *
 * <p>새로운 ArchUnit 테스트를 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreateArchUnitTestUseCase {

    /**
     * ArchUnit 테스트 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 ArchUnit 테스트 ID
     */
    Long execute(CreateArchUnitTestCommand command);
}
