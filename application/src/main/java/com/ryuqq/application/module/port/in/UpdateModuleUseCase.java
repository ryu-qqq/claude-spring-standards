package com.ryuqq.application.module.port.in;

import com.ryuqq.application.module.dto.command.UpdateModuleCommand;

/**
 * UpdateModuleUseCase - 모듈 수정 UseCase
 *
 * <p>모듈 수정 비즈니스 로직을 수행합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateModuleUseCase {

    /**
     * 모듈 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateModuleCommand command);
}
