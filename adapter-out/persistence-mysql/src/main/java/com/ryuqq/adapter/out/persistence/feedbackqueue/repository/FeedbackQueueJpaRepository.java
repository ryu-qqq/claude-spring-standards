package com.ryuqq.adapter.out.persistence.feedbackqueue.repository;

import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * FeedbackQueueJpaRepository - 피드백 큐 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface FeedbackQueueJpaRepository extends JpaRepository<FeedbackQueueJpaEntity, Long> {}
