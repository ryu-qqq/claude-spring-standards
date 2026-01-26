package com.ryuqq.application.ruleexample.port.in;

import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;

/**
 * CreateRuleExampleUseCase - 규칙 예시 생성 UseCase
 *
 * <p>새로운 규칙 예시를 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreateRuleExampleUseCase {

    /**
     * 규칙 예시 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 규칙 예시 ID
     */
    Long execute(CreateRuleExampleCommand command);
}
