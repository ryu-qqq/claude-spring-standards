package com.ryuqq.application.packagepurpose.port.in;

import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;

/**
 * CreatePackagePurposeUseCase - 패키지 목적 생성 UseCase
 *
 * <p>새로운 패키지 목적을 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreatePackagePurposeUseCase {

    /**
     * 패키지 목적 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 패키지 목적 ID
     */
    Long execute(CreatePackagePurposeCommand command);
}
