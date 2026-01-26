package com.ryuqq.application.codingrule.port.in;

import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;

/**
 * UpdateCodingRuleUseCase - 코딩 규칙 수정 UseCase
 *
 * <p>기존 코딩 규칙을 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateCodingRuleUseCase {

    /**
     * 코딩 규칙 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateCodingRuleCommand command);
}
