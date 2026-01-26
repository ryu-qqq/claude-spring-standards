package com.ryuqq.application.feedbackqueue.port.in;

import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;

/**
 * CreateFeedbackUseCase - 피드백 생성 UseCase
 *
 * <p>새로운 피드백을 큐에 등록합니다. (UC-L01)
 *
 * @author ryu-qqq
 */
public interface CreateFeedbackUseCase {

    /**
     * 피드백 생성
     *
     * @param command 생성 커맨드
     * @return 생성된 피드백 ID
     */
    Long execute(CreateFeedbackCommand command);
}
