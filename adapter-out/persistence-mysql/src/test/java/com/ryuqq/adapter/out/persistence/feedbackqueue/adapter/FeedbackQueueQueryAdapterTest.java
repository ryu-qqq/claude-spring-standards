package com.ryuqq.adapter.out.persistence.feedbackqueue.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.adapter.out.persistence.feedbackqueue.mapper.FeedbackQueueJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.feedbackqueue.repository.FeedbackQueueQueryDslRepository;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FeedbackQueueQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("FeedbackQueue Query Adapter 단위 테스트")
class FeedbackQueueQueryAdapterTest {

    @Mock private FeedbackQueueQueryDslRepository queryDslRepository;

    @Mock private FeedbackQueueJpaEntityMapper mapper;

    @InjectMocks private FeedbackQueueQueryAdapter queryAdapter;

    @Test
    @DisplayName("findById(FeedbackQueueId) 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_WithFeedbackQueueId_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackQueueId id = FeedbackQueueId.of(1L);
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<FeedbackQueue> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findById(1L);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById(Long) 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_WithLong_ShouldCallRepositoryAndMapper() {
        // Given
        Long id = 1L;
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<FeedbackQueue> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findById(1L);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findByStatus() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findByStatus_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackStatus status = FeedbackStatus.PENDING;
        FeedbackQueueJpaEntity entity1 = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueueJpaEntity entity2 = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain1 = mock(FeedbackQueue.class);
        FeedbackQueue domain2 = mock(FeedbackQueue.class);

        when(queryDslRepository.findByStatus(status)).thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(domain1);
        when(mapper.toDomain(entity2)).thenReturn(domain2);

        // When
        List<FeedbackQueue> result = queryAdapter.findByStatus(status);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(domain1, domain2);

        verify(queryDslRepository).findByStatus(status);
    }

    @Test
    @DisplayName("findByTargetType() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findByTargetType_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackTargetType targetType = FeedbackTargetType.CODING_RULE;
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findByTargetType(targetType)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<FeedbackQueue> result = queryAdapter.findByTargetType(targetType);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findByTargetType(targetType);
    }

    @Test
    @DisplayName("findByRiskLevel() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findByRiskLevel_ShouldCallRepositoryAndMapper() {
        // Given
        RiskLevel riskLevel = RiskLevel.SAFE;
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findByRiskLevel(riskLevel)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<FeedbackQueue> result = queryAdapter.findByRiskLevel(riskLevel);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findByRiskLevel(riskLevel);
    }

    @Test
    @DisplayName("findPendingFeedbacks() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findPendingFeedbacks_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findPendingFeedbacks()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<FeedbackQueue> result = queryAdapter.findPendingFeedbacks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findPendingFeedbacks();
    }

    @Test
    @DisplayName("findAutoMergeableFeedbacks() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findAutoMergeableFeedbacks_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findAutoMergeableFeedbacks()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<FeedbackQueue> result = queryAdapter.findAutoMergeableFeedbacks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findAutoMergeableFeedbacks();
    }

    @Test
    @DisplayName("findHumanReviewRequiredFeedbacks() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findHumanReviewRequiredFeedbacks_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findHumanReviewRequiredFeedbacks()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<FeedbackQueue> result = queryAdapter.findHumanReviewRequiredFeedbacks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findHumanReviewRequiredFeedbacks();
    }

    @Test
    @DisplayName("findByTarget() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findByTarget_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackTargetType targetType = FeedbackTargetType.CODING_RULE;
        Long targetId = 100L;
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findByTarget(targetType, targetId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<FeedbackQueue> result = queryAdapter.findByTarget(targetType, targetId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findByTarget(targetType, targetId);
    }

    @Test
    @DisplayName("findBySlice() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findBySlice_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackStatus status = FeedbackStatus.PENDING;
        Long cursor = 100L;
        int fetchSize = 10;
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findBySlice(status, cursor, fetchSize)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<FeedbackQueue> result = queryAdapter.findBySlice(status, cursor, fetchSize);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findBySlice(status, cursor, fetchSize);
    }

    @Test
    @DisplayName("findBySliceCriteria() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findBySliceCriteria_ShouldCallRepositoryAndMapper() {
        // Given
        FeedbackQueueSliceCriteria criteria = mock(FeedbackQueueSliceCriteria.class);
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findBySliceCriteria(criteria)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<FeedbackQueue> result = queryAdapter.findBySliceCriteria(criteria);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findBySliceCriteria(criteria);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("existsById() 호출 시 Repository를 올바르게 호출해야 한다")
    void existsById_ShouldCallRepository() {
        // Given
        FeedbackQueueId id = FeedbackQueueId.of(1L);

        when(queryDslRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = queryAdapter.existsById(id);

        // Then
        assertThat(result).isTrue();

        verify(queryDslRepository).existsById(1L);
    }

    @Test
    @DisplayName("findById() 호출 시 올바른 순서로 실행되어야 한다")
    void findById_ShouldExecuteInCorrectOrder() {
        // Given
        FeedbackQueueId id = FeedbackQueueId.of(1L);
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueue domain = mock(FeedbackQueue.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        queryAdapter.findById(id);

        // Then - 실행 순서 검증
        InOrder inOrder = inOrder(queryDslRepository, mapper);
        inOrder.verify(queryDslRepository).findById(1L);
        inOrder.verify(mapper).toDomain(entity);
    }
}
