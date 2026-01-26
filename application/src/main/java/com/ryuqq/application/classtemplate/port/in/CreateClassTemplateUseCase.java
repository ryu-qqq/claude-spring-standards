package com.ryuqq.application.classtemplate.port.in;

import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;

/**
 * CreateClassTemplateUseCase - 클래스 템플릿 생성 UseCase
 *
 * <p>새로운 클래스 템플릿을 생성합니다.
 *
 * @author ryu-qqq
 */
public interface CreateClassTemplateUseCase {

    /**
     * 클래스 템플릿 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 클래스 템플릿 ID
     */
    Long execute(CreateClassTemplateCommand command);
}
