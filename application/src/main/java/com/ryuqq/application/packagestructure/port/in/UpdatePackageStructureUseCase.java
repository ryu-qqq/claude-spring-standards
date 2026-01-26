package com.ryuqq.application.packagestructure.port.in;

import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;

/**
 * UpdatePackageStructureUseCase - 패키지 구조 수정 UseCase
 *
 * <p>기존 패키지 구조를 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdatePackageStructureUseCase {

    /**
     * 패키지 구조 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdatePackageStructureCommand command);
}
