package com.ryuqq.adapter.out.persistence.feedbackqueue.adapter;

import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.adapter.out.persistence.feedbackqueue.mapper.FeedbackQueueJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.feedbackqueue.repository.FeedbackQueueQueryDslRepository;
import com.ryuqq.application.feedbackqueue.port.out.FeedbackQueueQueryPort;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueQueryAdapter - 피드백 큐 조회 어댑터
 *
 * <p>FeedbackQueueQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>QADP-001: QueryDslRepository 위임만
 *
 * <p>QADP-002: QueryAdapter에서 @Transactional 금지
 *
 * <p>QADP-006: Domain 반환 (DTO 반환 금지)
 *
 * <p>QADP-007: Entity -> Domain 변환 (Mapper 사용)
 *
 * <p>QADP-008: QueryAdapter에 비즈니스 로직 금지
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueueQueryAdapter implements FeedbackQueueQueryPort {

    private final FeedbackQueueQueryDslRepository queryDslRepository;
    private final FeedbackQueueJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * <p>QADP-005: Mapper + QueryDslRepository 의존
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public FeedbackQueueQueryAdapter(
            FeedbackQueueQueryDslRepository queryDslRepository,
            FeedbackQueueJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 피드백 큐 조회
     *
     * @param id 피드백 큐 ID
     * @return 피드백 큐 Optional
     */
    @Override
    public Optional<FeedbackQueue> findById(FeedbackQueueId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 피드백 큐 조회 (기본 타입)
     *
     * @param id 피드백 큐 ID
     * @return 피드백 큐 Optional
     */
    @Override
    public Optional<FeedbackQueue> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * 상태별 피드백 큐 목록 조회
     *
     * @param status 처리 상태
     * @return 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findByStatus(FeedbackStatus status) {
        List<FeedbackQueueJpaEntity> entities = queryDslRepository.findByStatus(status);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 대상 타입별 피드백 큐 목록 조회
     *
     * @param targetType 피드백 대상 타입
     * @return 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findByTargetType(FeedbackTargetType targetType) {
        List<FeedbackQueueJpaEntity> entities = queryDslRepository.findByTargetType(targetType);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 리스크 수준별 피드백 큐 목록 조회
     *
     * @param riskLevel 리스크 수준
     * @return 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findByRiskLevel(RiskLevel riskLevel) {
        List<FeedbackQueueJpaEntity> entities = queryDslRepository.findByRiskLevel(riskLevel);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 대기 중인 피드백 큐 목록 조회
     *
     * @return PENDING 상태의 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findPendingFeedbacks() {
        List<FeedbackQueueJpaEntity> entities = queryDslRepository.findPendingFeedbacks();
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 자동 병합 가능한 피드백 큐 목록 조회
     *
     * @return 자동 병합 가능한 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findAutoMergeableFeedbacks() {
        List<FeedbackQueueJpaEntity> entities = queryDslRepository.findAutoMergeableFeedbacks();
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 사람 승인 필요한 피드백 큐 목록 조회
     *
     * @return 사람 승인 필요한 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findHumanReviewRequiredFeedbacks() {
        List<FeedbackQueueJpaEntity> entities =
                queryDslRepository.findHumanReviewRequiredFeedbacks();
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 특정 대상에 대한 피드백 큐 목록 조회
     *
     * @param targetType 피드백 대상 타입
     * @param targetId 피드백 대상 ID
     * @return 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findByTarget(FeedbackTargetType targetType, Long targetId) {
        List<FeedbackQueueJpaEntity> entities =
                queryDslRepository.findByTarget(targetType, targetId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 커서 기반 슬라이스 조회
     *
     * @param status 처리 상태 (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findBySlice(FeedbackStatus status, Long cursor, int fetchSize) {
        List<FeedbackQueueJpaEntity> entities =
                queryDslRepository.findBySlice(status, cursor, fetchSize);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * SliceCriteria 기반 조회
     *
     * <p>Domain Criteria 객체를 사용한 표준 슬라이스 조회입니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return 피드백 큐 목록
     */
    @Override
    public List<FeedbackQueue> findBySliceCriteria(FeedbackQueueSliceCriteria criteria) {
        List<FeedbackQueueJpaEntity> entities = queryDslRepository.findBySliceCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 피드백 큐 존재 여부 확인
     *
     * @param id 피드백 큐 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsById(FeedbackQueueId id) {
        return queryDslRepository.existsById(id.value());
    }
}
