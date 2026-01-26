package com.ryuqq.application.classtype.port.in;

import com.ryuqq.application.classtype.dto.command.CreateClassTypeCommand;

/**
 * CreateClassTypeUseCase - ClassType 생성 UseCase
 *
 * <p>새로운 ClassType을 생성합니다.
 *
 * <p>UC-001: UseCase는 반드시 interface로 정의.
 *
 * <p>UC-002: UseCase는 execute() 단일 메서드만 제공.
 *
 * <p>UC-006: Command UseCase는 동사 접두어 + UseCase 네이밍.
 *
 * @author ryu-qqq
 */
public interface CreateClassTypeUseCase {

    /**
     * ClassType 생성 실행
     *
     * @param command 생성 Command
     * @return 생성된 ClassType ID
     */
    Long execute(CreateClassTypeCommand command);
}
