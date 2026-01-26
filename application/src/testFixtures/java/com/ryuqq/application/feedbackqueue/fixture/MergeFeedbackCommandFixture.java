package com.ryuqq.application.feedbackqueue.fixture;

import com.ryuqq.application.feedbackqueue.dto.command.MergeFeedbackCommand;

/**
 * MergeFeedbackCommand Test Fixture
 *
 * @author development-team
 */
public final class MergeFeedbackCommandFixture {

    private static final Long DEFAULT_FEEDBACK_ID = 1L;

    private MergeFeedbackCommandFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 기본 MergeFeedbackCommand 생성
     *
     * @return MergeFeedbackCommand
     */
    public static MergeFeedbackCommand defaultCommand() {
        return new MergeFeedbackCommand(DEFAULT_FEEDBACK_ID);
    }

    /**
     * 특정 피드백 ID로 MergeFeedbackCommand 생성
     *
     * @param feedbackId 피드백 ID
     * @return MergeFeedbackCommand
     */
    public static MergeFeedbackCommand withFeedbackId(Long feedbackId) {
        return new MergeFeedbackCommand(feedbackId);
    }
}
