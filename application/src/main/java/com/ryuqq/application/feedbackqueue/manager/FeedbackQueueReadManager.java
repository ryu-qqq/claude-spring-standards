package com.ryuqq.application.feedbackqueue.manager;

import com.ryuqq.application.feedbackqueue.port.out.FeedbackQueueQueryPort;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackQueueNotFoundException;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * FeedbackQueueReadManager - 피드백 큐 조회 관리자
 *
 * <p>피드백 큐 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueueReadManager {

    private final FeedbackQueueQueryPort feedbackQueueQueryPort;

    public FeedbackQueueReadManager(FeedbackQueueQueryPort feedbackQueueQueryPort) {
        this.feedbackQueueQueryPort = feedbackQueueQueryPort;
    }

    /**
     * ID로 피드백 큐 조회 (존재하지 않으면 예외)
     *
     * @param feedbackQueueId 피드백 큐 ID
     * @return 피드백 큐
     * @throws FeedbackQueueNotFoundException 피드백 큐가 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public FeedbackQueue getById(FeedbackQueueId feedbackQueueId) {
        return feedbackQueueQueryPort
                .findById(feedbackQueueId)
                .orElseThrow(() -> new FeedbackQueueNotFoundException(feedbackQueueId.value()));
    }

    /**
     * Long ID로 피드백 큐 조회 (존재하지 않으면 예외)
     *
     * @param feedbackId 피드백 ID
     * @return 피드백 큐
     * @throws FeedbackQueueNotFoundException 피드백 큐가 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public FeedbackQueue getById(Long feedbackId) {
        return feedbackQueueQueryPort
                .findById(feedbackId)
                .orElseThrow(() -> new FeedbackQueueNotFoundException(feedbackId));
    }

    /**
     * ID로 피드백 큐 존재 여부 확인 후 반환
     *
     * @param feedbackQueueId 피드백 큐 ID
     * @return 피드백 큐 (nullable)
     */
    @Transactional(readOnly = true)
    public FeedbackQueue findById(FeedbackQueueId feedbackQueueId) {
        return feedbackQueueQueryPort.findById(feedbackQueueId).orElse(null);
    }

    /**
     * 상태별 피드백 큐 목록 조회
     *
     * @param status 피드백 상태
     * @return 피드백 큐 목록
     */
    @Transactional(readOnly = true)
    public List<FeedbackQueue> findByStatus(FeedbackStatus status) {
        return feedbackQueueQueryPort.findByStatus(status);
    }

    /**
     * PENDING 상태 피드백 큐 목록 조회
     *
     * @return PENDING 상태 피드백 큐 목록
     */
    @Transactional(readOnly = true)
    public List<FeedbackQueue> findPendingFeedbacks() {
        return feedbackQueueQueryPort.findPendingFeedbacks();
    }

    /**
     * Human 승인 필요한 피드백 큐 목록 조회
     *
     * @return LLM_APPROVED + MEDIUM 상태 피드백 큐 목록
     */
    @Transactional(readOnly = true)
    public List<FeedbackQueue> findHumanReviewRequiredFeedbacks() {
        return feedbackQueueQueryPort.findHumanReviewRequiredFeedbacks();
    }

    /**
     * 자동 머지 가능한 피드백 큐 목록 조회
     *
     * @return LLM_APPROVED + SAFE 상태 피드백 큐 목록
     */
    @Transactional(readOnly = true)
    public List<FeedbackQueue> findAutoMergeableFeedbacks() {
        return feedbackQueueQueryPort.findAutoMergeableFeedbacks();
    }

    /**
     * 커서 기반 슬라이스 조회
     *
     * @param status 피드백 상태 (nullable)
     * @param cursorId 커서 ID (nullable)
     * @param fetchSize 조회 크기
     * @return 피드백 큐 목록
     */
    @Transactional(readOnly = true)
    public List<FeedbackQueue> findBySlice(FeedbackStatus status, Long cursorId, int fetchSize) {
        return feedbackQueueQueryPort.findBySlice(status, cursorId, fetchSize);
    }

    /**
     * SliceCriteria 기반 조회
     *
     * <p>Domain Criteria 객체를 사용한 표준 슬라이스 조회입니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return 피드백 큐 목록
     */
    @Transactional(readOnly = true)
    public List<FeedbackQueue> findBySliceCriteria(FeedbackQueueSliceCriteria criteria) {
        return feedbackQueueQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 대상 타입별 피드백 큐 목록 조회
     *
     * @param targetType 대상 타입
     * @return 피드백 큐 목록
     */
    @Transactional(readOnly = true)
    public List<FeedbackQueue> findByTargetType(FeedbackTargetType targetType) {
        return feedbackQueueQueryPort.findByTargetType(targetType);
    }

    /**
     * 특정 대상에 대한 피드백 큐 목록 조회
     *
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 피드백 큐 목록
     */
    @Transactional(readOnly = true)
    public List<FeedbackQueue> findByTarget(FeedbackTargetType targetType, Long targetId) {
        return feedbackQueueQueryPort.findByTarget(targetType, targetId);
    }

    /**
     * 피드백 큐 존재 여부 확인
     *
     * @param feedbackQueueId 피드백 큐 ID
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsById(FeedbackQueueId feedbackQueueId) {
        return feedbackQueueQueryPort.findById(feedbackQueueId).isPresent();
    }
}
