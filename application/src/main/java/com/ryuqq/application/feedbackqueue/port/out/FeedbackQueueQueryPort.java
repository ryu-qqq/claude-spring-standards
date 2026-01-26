package com.ryuqq.application.feedbackqueue.port.out;

import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.util.List;
import java.util.Optional;

/**
 * FeedbackQueueQueryPort - 피드백 큐 조회 포트
 *
 * <p>피드백 큐 조회를 위한 아웃바운드 포트입니다.
 *
 * <p>QPRT-001: QueryPort는 Domain 반환만 허용합니다.
 *
 * @author ryu-qqq
 */
public interface FeedbackQueueQueryPort {

    /**
     * ID로 피드백 큐 조회
     *
     * @param id 피드백 큐 ID
     * @return 피드백 큐 Optional
     */
    Optional<FeedbackQueue> findById(FeedbackQueueId id);

    /**
     * ID로 피드백 큐 조회 (기본 타입)
     *
     * @param id 피드백 큐 ID
     * @return 피드백 큐 Optional
     */
    Optional<FeedbackQueue> findById(Long id);

    /**
     * 상태별 피드백 큐 목록 조회
     *
     * @param status 처리 상태
     * @return 피드백 큐 목록
     */
    List<FeedbackQueue> findByStatus(FeedbackStatus status);

    /**
     * 대상 타입별 피드백 큐 목록 조회
     *
     * @param targetType 피드백 대상 타입
     * @return 피드백 큐 목록
     */
    List<FeedbackQueue> findByTargetType(FeedbackTargetType targetType);

    /**
     * 리스크 수준별 피드백 큐 목록 조회
     *
     * @param riskLevel 리스크 수준
     * @return 피드백 큐 목록
     */
    List<FeedbackQueue> findByRiskLevel(RiskLevel riskLevel);

    /**
     * 대기 중인 피드백 큐 목록 조회
     *
     * @return PENDING 상태의 피드백 큐 목록
     */
    List<FeedbackQueue> findPendingFeedbacks();

    /**
     * 자동 병합 가능한 피드백 큐 목록 조회
     *
     * <p>SAFE 리스크이고 LLM_APPROVED 상태인 피드백만 조회합니다.
     *
     * @return 자동 병합 가능한 피드백 큐 목록
     */
    List<FeedbackQueue> findAutoMergeableFeedbacks();

    /**
     * 사람 승인 필요한 피드백 큐 목록 조회
     *
     * <p>MEDIUM 리스크이고 LLM_APPROVED 상태인 피드백만 조회합니다.
     *
     * @return 사람 승인 필요한 피드백 큐 목록
     */
    List<FeedbackQueue> findHumanReviewRequiredFeedbacks();

    /**
     * 특정 대상에 대한 피드백 큐 목록 조회
     *
     * @param targetType 피드백 대상 타입
     * @param targetId 피드백 대상 ID
     * @return 피드백 큐 목록
     */
    List<FeedbackQueue> findByTarget(FeedbackTargetType targetType, Long targetId);

    /**
     * 커서 기반 슬라이스 조회
     *
     * @param status 처리 상태 (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 피드백 큐 목록
     */
    List<FeedbackQueue> findBySlice(FeedbackStatus status, Long cursor, int fetchSize);

    /**
     * SliceCriteria 기반 조회
     *
     * <p>Domain Criteria 객체를 사용한 표준 슬라이스 조회입니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return 피드백 큐 목록
     */
    List<FeedbackQueue> findBySliceCriteria(FeedbackQueueSliceCriteria criteria);

    /**
     * 피드백 큐 존재 여부 확인
     *
     * @param id 피드백 큐 ID
     * @return 존재하면 true
     */
    boolean existsById(FeedbackQueueId id);
}
