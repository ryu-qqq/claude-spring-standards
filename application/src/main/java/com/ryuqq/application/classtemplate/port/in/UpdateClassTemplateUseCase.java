package com.ryuqq.application.classtemplate.port.in;

import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;

/**
 * UpdateClassTemplateUseCase - 클래스 템플릿 수정 UseCase
 *
 * <p>기존 클래스 템플릿을 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateClassTemplateUseCase {

    /**
     * 클래스 템플릿 수정
     *
     * @param command 수정 커맨드
     */
    void execute(UpdateClassTemplateCommand command);
}
