package com.ryuqq.application.packagestructure.port.in;

import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;

/**
 * CreatePackageStructureUseCase - 패키지 구조 생성 UseCase
 *
 * <p>새로운 패키지 구조를 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreatePackageStructureUseCase {

    /**
     * 패키지 구조 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 패키지 구조 ID
     */
    Long execute(CreatePackageStructureCommand command);
}
