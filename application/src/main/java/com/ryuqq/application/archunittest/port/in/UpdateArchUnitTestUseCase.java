package com.ryuqq.application.archunittest.port.in;

import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;

/**
 * UpdateArchUnitTestUseCase - ArchUnit 테스트 수정 UseCase
 *
 * <p>기존 ArchUnit 테스트를 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateArchUnitTestUseCase {

    /**
     * ArchUnit 테스트 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateArchUnitTestCommand command);
}
