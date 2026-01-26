package com.ryuqq.application.zerotolerance.port.in;

import com.ryuqq.application.zerotolerance.dto.command.CreateZeroToleranceRuleCommand;

/**
 * CreateZeroToleranceRuleUseCase - Zero-Tolerance 규칙 생성 유스케이스
 *
 * <p>Zero-Tolerance 규칙 생성을 위한 인바운드 포트입니다.
 *
 * <p>PORT-001: UseCase는 인터페이스로 정의.
 *
 * @author ryu-qqq
 */
public interface CreateZeroToleranceRuleUseCase {

    /**
     * Zero-Tolerance 규칙 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 Zero-Tolerance 규칙 ID
     */
    Long execute(CreateZeroToleranceRuleCommand command);
}
