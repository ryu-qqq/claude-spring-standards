package com.ryuqq.application.layerdependency.port.in;

import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;

/**
 * UpdateLayerDependencyRuleUseCase - 레이어 의존성 규칙 수정 UseCase
 *
 * <p>기존 레이어 의존성 규칙을 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateLayerDependencyRuleUseCase {

    /**
     * 레이어 의존성 규칙 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateLayerDependencyRuleCommand command);
}
