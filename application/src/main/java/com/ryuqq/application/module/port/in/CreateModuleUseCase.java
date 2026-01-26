package com.ryuqq.application.module.port.in;

import com.ryuqq.application.module.dto.command.CreateModuleCommand;

/**
 * CreateModuleUseCase - 모듈 생성 UseCase
 *
 * <p>모듈 생성 비즈니스 로직을 수행합니다.
 *
 * @author ryu-qqq
 */
public interface CreateModuleUseCase {

    /**
     * 모듈 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 모듈의 ID
     */
    Long execute(CreateModuleCommand command);
}
