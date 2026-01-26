package com.ryuqq.application.codingrule.port.in;

import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;

/**
 * CreateCodingRuleUseCase - 코딩 규칙 생성 UseCase
 *
 * <p>새로운 코딩 규칙을 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreateCodingRuleUseCase {

    /**
     * 코딩 규칙 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 코딩 규칙 ID
     */
    Long execute(CreateCodingRuleCommand command);
}
