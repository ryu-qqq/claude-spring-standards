package com.ryuqq.adapter.out.persistence.feedbackqueue.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.adapter.out.persistence.feedbackqueue.mapper.FeedbackQueueJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.feedbackqueue.repository.FeedbackQueueJpaRepository;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FeedbackQueueCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("FeedbackQueue Command Adapter 단위 테스트")
class FeedbackQueueCommandAdapterTest {

    @Mock private FeedbackQueueJpaRepository repository;

    @Mock private FeedbackQueueJpaEntityMapper mapper;

    @InjectMocks private FeedbackQueueCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        FeedbackQueue feedbackQueue = mock(FeedbackQueue.class);
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueueJpaEntity savedEntity = mock(FeedbackQueueJpaEntity.class);

        when(mapper.toEntity(feedbackQueue)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        FeedbackQueueId result = commandAdapter.persist(feedbackQueue);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(feedbackQueue);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        FeedbackQueue feedbackQueue = mock(FeedbackQueue.class);
        FeedbackQueueJpaEntity entity = mock(FeedbackQueueJpaEntity.class);
        FeedbackQueueJpaEntity savedEntity = mock(FeedbackQueueJpaEntity.class);

        when(mapper.toEntity(feedbackQueue)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(feedbackQueue);

        // Then - 실행 순서 검증
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(feedbackQueue);
        inOrder.verify(repository).save(entity);
    }
}
