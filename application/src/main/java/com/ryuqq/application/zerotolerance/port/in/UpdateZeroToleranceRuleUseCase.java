package com.ryuqq.application.zerotolerance.port.in;

import com.ryuqq.application.zerotolerance.dto.command.UpdateZeroToleranceRuleCommand;

/**
 * UpdateZeroToleranceRuleUseCase - Zero-Tolerance 규칙 수정 유스케이스
 *
 * <p>Zero-Tolerance 규칙 수정을 위한 인바운드 포트입니다.
 *
 * <p>PORT-001: UseCase는 인터페이스로 정의.
 *
 * @author ryu-qqq
 */
public interface UpdateZeroToleranceRuleUseCase {

    /**
     * Zero-Tolerance 규칙 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateZeroToleranceRuleCommand command);
}
