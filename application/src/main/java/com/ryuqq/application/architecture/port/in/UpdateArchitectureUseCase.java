package com.ryuqq.application.architecture.port.in;

import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;

/**
 * UpdateArchitectureUseCase - Architecture 수정 UseCase
 *
 * <p>기존 Architecture의 정보를 수정합니다.
 *
 * <p>UC-001: UseCase는 반드시 interface로 정의.
 *
 * <p>UC-002: UseCase는 execute() 단일 메서드만 제공.
 *
 * <p>UC-006: Command UseCase는 동사 접두어 + UseCase 네이밍.
 *
 * @author ryu-qqq
 */
public interface UpdateArchitectureUseCase {

    /**
     * Architecture 수정 실행
     *
     * @param command 수정 Command
     * @throws com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException 존재하지 않는 경우
     */
    void execute(UpdateArchitectureCommand command);
}
