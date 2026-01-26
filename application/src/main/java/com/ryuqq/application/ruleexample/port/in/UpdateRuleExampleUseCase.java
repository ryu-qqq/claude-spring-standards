package com.ryuqq.application.ruleexample.port.in;

import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;

/**
 * UpdateRuleExampleUseCase - 규칙 예시 수정 UseCase
 *
 * <p>기존 규칙 예시를 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateRuleExampleUseCase {

    /**
     * 규칙 예시 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateRuleExampleCommand command);
}
