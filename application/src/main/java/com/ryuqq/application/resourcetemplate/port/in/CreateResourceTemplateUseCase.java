package com.ryuqq.application.resourcetemplate.port.in;

import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;

/**
 * CreateResourceTemplateUseCase - 리소스 템플릿 생성 UseCase
 *
 * <p>새로운 리소스 템플릿을 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreateResourceTemplateUseCase {

    /**
     * 리소스 템플릿 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 리소스 템플릿 ID
     */
    Long execute(CreateResourceTemplateCommand command);
}
