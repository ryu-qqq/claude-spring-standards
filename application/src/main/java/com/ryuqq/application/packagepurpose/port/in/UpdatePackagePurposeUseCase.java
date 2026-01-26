package com.ryuqq.application.packagepurpose.port.in;

import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;

/**
 * UpdatePackagePurposeUseCase - 패키지 목적 수정 UseCase
 *
 * <p>기존 패키지 목적 정보를 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdatePackagePurposeUseCase {

    /**
     * 패키지 목적 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdatePackagePurposeCommand command);
}
