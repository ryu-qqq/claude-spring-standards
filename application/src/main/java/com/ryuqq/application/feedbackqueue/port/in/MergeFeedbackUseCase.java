package com.ryuqq.application.feedbackqueue.port.in;

import com.ryuqq.application.feedbackqueue.dto.command.MergeFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;

/**
 * MergeFeedbackUseCase - 피드백 머지 UseCase
 *
 * <p>승인된 피드백을 실제 대상 테이블에 반영합니다. (UC-S05)
 *
 * @author ryu-qqq
 */
public interface MergeFeedbackUseCase {

    /**
     * 피드백 머지
     *
     * @param command 머지 커맨드
     * @return 머지된 피드백 정보
     */
    FeedbackQueueResult execute(MergeFeedbackCommand command);
}
