package com.ryuqq.application.feedbackqueue.dto.response;

import java.util.List;

/**
 * FeedbackQueueSliceResult - 피드백 큐 슬라이스 조회 결과
 *
 * <p>커서 기반 페이지네이션 결과를 담는 DTO입니다.
 *
 * @param content 피드백 큐 결과 목록
 * @param hasNext 다음 페이지 존재 여부
 * @author ryu-qqq
 */
public record FeedbackQueueSliceResult(List<FeedbackQueueResult> content, boolean hasNext) {

    /**
     * FeedbackQueueSliceResult 생성
     *
     * @param content 결과 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult of(List<FeedbackQueueResult> content, boolean hasNext) {
        return new FeedbackQueueSliceResult(content, hasNext);
    }

    /**
     * 빈 결과 생성
     *
     * @return 빈 FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult empty() {
        return new FeedbackQueueSliceResult(List.of(), false);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return content가 비어있으면 true
     */
    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }

    /**
     * 결과 개수 반환
     *
     * @return 결과 개수
     */
    public int size() {
        return content != null ? content.size() : 0;
    }
}
