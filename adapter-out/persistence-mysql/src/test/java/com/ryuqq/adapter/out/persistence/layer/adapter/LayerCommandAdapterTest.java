package com.ryuqq.adapter.out.persistence.layer.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import com.ryuqq.adapter.out.persistence.layer.mapper.LayerEntityMapper;
import com.ryuqq.adapter.out.persistence.layer.repository.LayerJpaRepository;
import com.ryuqq.domain.layer.aggregate.Layer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LayerCommandAdapter 단위 테스트
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("Layer Command Adapter 단위 테스트")
class LayerCommandAdapterTest {

    @Mock private LayerJpaRepository repository;

    @Mock private LayerEntityMapper mapper;

    @InjectMocks private LayerCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        Layer layer = mock(Layer.class);
        LayerJpaEntity entity = mock(LayerJpaEntity.class);
        LayerJpaEntity savedEntity = mock(LayerJpaEntity.class);

        when(mapper.toEntity(layer)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        Long result = commandAdapter.persist(layer);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);

        verify(mapper).toEntity(layer);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        Layer layer = mock(Layer.class);
        LayerJpaEntity entity = mock(LayerJpaEntity.class);
        LayerJpaEntity savedEntity = mock(LayerJpaEntity.class);

        when(mapper.toEntity(layer)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(layer);

        // Then - 실행 순서 검증
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(layer);
        inOrder.verify(repository).save(entity);
    }
}
