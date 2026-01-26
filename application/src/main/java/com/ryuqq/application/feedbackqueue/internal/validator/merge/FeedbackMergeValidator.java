package com.ryuqq.application.feedbackqueue.internal.validator.merge;

import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;

/**
 * FeedbackMergeValidator - 피드백 병합 시점 검증 인터페이스
 *
 * <p>병합 시점에 피드백 페이로드의 유효성을 검증하는 전략 패턴 인터페이스입니다.
 *
 * <p>검증 항목:
 *
 * <ul>
 *   <li>페이로드 JSON 파싱 가능 여부
 *   <li>ADD 시: 부모 엔티티 존재 여부
 *   <li>MODIFY/DELETE 시: 대상 엔티티 존재 여부
 * </ul>
 *
 * <p>입력 시점 검증(Stage 1)과 별개로, 병합 시점에 데이터 변경 가능성을 고려하여 재검증합니다.
 *
 * <p>검증 실패 시 {@link com.ryuqq.domain.feedbackqueue.exception.FeedbackMergeValidationException}을
 * 던집니다.
 *
 * @author ryu-qqq
 */
public interface FeedbackMergeValidator {

    /**
     * 지원하는 FeedbackTargetType 반환
     *
     * @return 지원하는 타겟 타입
     */
    FeedbackTargetType supportedType();

    /**
     * 병합 시점 검증 수행
     *
     * <p>FeedbackQueue의 payload와 feedbackType에 따라 적절한 검증을 수행합니다.
     *
     * <p>검증 성공 시 아무 처리 없이 반환하고, 실패 시 예외를 던집니다.
     *
     * @param feedbackQueue 검증할 피드백 큐
     * @throws com.ryuqq.domain.feedbackqueue.exception.FeedbackMergeValidationException 검증 실패 시
     */
    void validate(FeedbackQueue feedbackQueue);
}
