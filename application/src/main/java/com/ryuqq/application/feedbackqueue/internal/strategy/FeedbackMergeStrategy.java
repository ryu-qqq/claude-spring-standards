package com.ryuqq.application.feedbackqueue.internal.strategy;

import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;

/**
 * FeedbackMergeStrategy - 피드백 머지 전략 인터페이스
 *
 * <p>각 FeedbackTargetType별로 머지 로직을 구현하는 전략 패턴 인터페이스입니다.
 *
 * <p>전략 구현체는 다음 순서로 처리합니다:
 *
 * <ol>
 *   <li>Payload에서 데이터 추출 (JSON 파싱)
 *   <li>Validator로 생성/수정 가능 여부 검증
 *   <li>Factory로 도메인 객체 생성
 *   <li>PersistenceManager로 저장
 * </ol>
 *
 * @author ryu-qqq
 */
public interface FeedbackMergeStrategy {

    /**
     * 지원하는 FeedbackTargetType 반환
     *
     * @return 지원하는 타겟 타입
     */
    FeedbackTargetType supportedType();

    /**
     * 피드백 머지 실행
     *
     * <p>FeedbackQueue의 payload를 파싱하여 대상 테이블에 데이터를 저장합니다. feedbackType(ADD, MODIFY, DELETE)에 따라
     * 생성/수정/삭제를 수행합니다.
     *
     * @param feedbackQueue 머지할 피드백 큐
     * @return 저장된 대상 엔티티의 ID
     */
    Long merge(FeedbackQueue feedbackQueue);
}
