package com.ryuqq.application.layerdependency.port.in;

import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;

/**
 * CreateLayerDependencyRuleUseCase - 레이어 의존성 규칙 생성 UseCase
 *
 * <p>새로운 레이어 의존성 규칙을 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreateLayerDependencyRuleUseCase {

    /**
     * 레이어 의존성 규칙 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 레이어 의존성 규칙 ID
     */
    Long execute(CreateLayerDependencyRuleCommand command);
}
