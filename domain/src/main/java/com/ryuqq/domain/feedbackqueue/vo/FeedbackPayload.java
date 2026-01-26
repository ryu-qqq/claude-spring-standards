package com.ryuqq.domain.feedbackqueue.vo;

/**
 * FeedbackPayload - 피드백 페이로드 Value Object
 *
 * <p>피드백의 JSON 데이터를 캡슐화합니다. 실제 JSON 파싱은 Application Layer에서 수행합니다.
 *
 * @param value JSON 문자열 데이터
 * @author ryu-qqq
 */
public record FeedbackPayload(String value) {

    public FeedbackPayload {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FeedbackPayload value must not be null or blank");
        }
    }

    /**
     * FeedbackPayload 생성
     *
     * @param value JSON 문자열
     * @return FeedbackPayload
     */
    public static FeedbackPayload of(String value) {
        return new FeedbackPayload(value);
    }

    /**
     * 빈 페이로드 여부 확인
     *
     * @return 항상 false (생성자에서 검증됨)
     */
    public boolean isEmpty() {
        return false;
    }
}
