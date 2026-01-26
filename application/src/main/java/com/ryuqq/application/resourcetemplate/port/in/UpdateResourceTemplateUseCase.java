package com.ryuqq.application.resourcetemplate.port.in;

import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;

/**
 * UpdateResourceTemplateUseCase - 리소스 템플릿 수정 UseCase
 *
 * <p>기존 리소스 템플릿을 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateResourceTemplateUseCase {

    /**
     * 리소스 템플릿 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateResourceTemplateCommand command);
}
