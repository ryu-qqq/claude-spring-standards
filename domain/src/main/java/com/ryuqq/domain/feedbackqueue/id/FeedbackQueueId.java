package com.ryuqq.domain.feedbackqueue.id;

/**
 * FeedbackQueueId - 피드백 큐 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record FeedbackQueueId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static FeedbackQueueId forNew() {
        return new FeedbackQueueId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static FeedbackQueueId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "FeedbackQueueId value must not be null for existing entity");
        }
        return new FeedbackQueueId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
