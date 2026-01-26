package com.ryuqq.adapter.out.persistence.layer.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import com.ryuqq.adapter.out.persistence.layer.mapper.LayerEntityMapper;
import com.ryuqq.adapter.out.persistence.layer.repository.LayerQueryDslRepository;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import com.ryuqq.domain.layer.vo.LayerCode;
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
 * LayerQueryAdapter 단위 테스트
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("Layer Query Adapter 단위 테스트")
class LayerQueryAdapterTest {

    @Mock private LayerQueryDslRepository queryDslRepository;

    @Mock private LayerEntityMapper mapper;

    @InjectMocks private LayerQueryAdapter queryAdapter;

    @Test
    @DisplayName("findById() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_ShouldCallRepositoryAndMapper() {
        // Given
        LayerId id = LayerId.of(1L);
        LayerJpaEntity entity = mock(LayerJpaEntity.class);
        Layer domain = mock(Layer.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Layer> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findById(1L);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById() 호출 시 Entity가 없으면 빈 Optional을 반환해야 한다")
    void findById_WhenEntityNotFound_ShouldReturnEmptyOptional() {
        // Given
        LayerId id = LayerId.of(999L);

        when(queryDslRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Layer> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isEmpty();

        verify(queryDslRepository).findById(999L);
        verify(mapper, never()).toDomain(any(LayerJpaEntity.class));
    }

    @Test
    @DisplayName("existsById() 호출 시 Entity가 존재하면 true를 반환해야 한다")
    void existsById_WhenEntityExists_ShouldReturnTrue() {
        // Given
        LayerId id = LayerId.of(1L);

        when(queryDslRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = queryAdapter.existsById(id);

        // Then
        assertThat(result).isTrue();

        verify(queryDslRepository).existsById(1L);
    }

    @Test
    @DisplayName("existsById() 호출 시 Entity가 없으면 false를 반환해야 한다")
    void existsById_WhenEntityNotFound_ShouldReturnFalse() {
        // Given
        LayerId id = LayerId.of(999L);

        when(queryDslRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = queryAdapter.existsById(id);

        // Then
        assertThat(result).isFalse();

        verify(queryDslRepository).existsById(999L);
    }

    @Test
    @DisplayName("findBySliceCriteria() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findBySliceCriteria_ShouldCallRepositoryAndMapper() {
        // Given
        LayerSliceCriteria criteria = LayerSliceCriteria.first(10);
        LayerJpaEntity entity1 = mock(LayerJpaEntity.class);
        Layer domain1 = mock(Layer.class);

        when(queryDslRepository.findBySliceCriteria(criteria)).thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(domain1);

        // When
        List<Layer> result = queryAdapter.findBySliceCriteria(criteria);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain1);

        verify(queryDslRepository).findBySliceCriteria(criteria);
        verify(mapper).toDomain(entity1);
    }

    @Test
    @DisplayName("findBySliceCriteria() 호출 시 빈 리스트를 반환해야 한다")
    void findBySliceCriteria_WhenNoResults_ShouldReturnEmptyList() {
        // Given
        LayerSliceCriteria criteria = LayerSliceCriteria.first(10);

        when(queryDslRepository.findBySliceCriteria(criteria)).thenReturn(List.of());

        // When
        List<Layer> result = queryAdapter.findBySliceCriteria(criteria);

        // Then
        assertThat(result).isEmpty();

        verify(queryDslRepository).findBySliceCriteria(criteria);
        verify(mapper, never()).toDomain(any(LayerJpaEntity.class));
    }

    @Test
    @DisplayName("existsByArchitectureIdAndCode() 호출 시 Repository를 올바르게 호출해야 한다")
    void existsByArchitectureIdAndCode_ShouldCallRepository() {
        // Given
        ArchitectureId architectureId = ArchitectureId.of(100L);
        LayerCode code = LayerCode.of("DOMAIN");

        when(queryDslRepository.existsByArchitectureIdAndCode(100L, "DOMAIN")).thenReturn(true);

        // When
        boolean result = queryAdapter.existsByArchitectureIdAndCode(architectureId, code);

        // Then
        assertThat(result).isTrue();

        verify(queryDslRepository).existsByArchitectureIdAndCode(100L, "DOMAIN");
    }

    @Test
    @DisplayName("existsByArchitectureIdAndCodeAndIdNot() 호출 시 Repository를 올바르게 호출해야 한다")
    void existsByArchitectureIdAndCodeAndIdNot_ShouldCallRepository() {
        // Given
        ArchitectureId architectureId = ArchitectureId.of(100L);
        LayerCode code = LayerCode.of("DOMAIN");
        LayerId excludeId = LayerId.of(1L);

        when(queryDslRepository.existsByArchitectureIdAndCodeAndIdNot(100L, "DOMAIN", 1L))
                .thenReturn(false);

        // When
        boolean result =
                queryAdapter.existsByArchitectureIdAndCodeAndIdNot(architectureId, code, excludeId);

        // Then
        assertThat(result).isFalse();

        verify(queryDslRepository).existsByArchitectureIdAndCodeAndIdNot(100L, "DOMAIN", 1L);
    }

    @Test
    @DisplayName("existsByArchitectureId() 호출 시 Repository를 올바르게 호출해야 한다")
    void existsByArchitectureId_ShouldCallRepository() {
        // Given
        ArchitectureId architectureId = ArchitectureId.of(100L);

        when(queryDslRepository.existsByArchitectureId(100L)).thenReturn(true);

        // When
        boolean result = queryAdapter.existsByArchitectureId(architectureId);

        // Then
        assertThat(result).isTrue();

        verify(queryDslRepository).existsByArchitectureId(100L);
    }

    @Test
    @DisplayName("findById() 호출 시 올바른 순서로 실행되어야 한다")
    void findById_ShouldExecuteInCorrectOrder() {
        // Given
        LayerId id = LayerId.of(1L);
        LayerJpaEntity entity = mock(LayerJpaEntity.class);
        Layer domain = mock(Layer.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        queryAdapter.findById(id);

        // Then - 실행 순서 검증
        InOrder inOrder = inOrder(queryDslRepository, mapper);
        inOrder.verify(queryDslRepository).findById(1L);
        inOrder.verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findBySliceCriteria() 호출 시 올바른 순서로 실행되어야 한다")
    void findBySliceCriteria_ShouldExecuteInCorrectOrder() {
        // Given
        LayerSliceCriteria criteria = LayerSliceCriteria.first(10);
        LayerJpaEntity entity1 = mock(LayerJpaEntity.class);
        Layer domain1 = mock(Layer.class);

        when(queryDslRepository.findBySliceCriteria(criteria)).thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(domain1);

        // When
        queryAdapter.findBySliceCriteria(criteria);

        // Then - 실행 순서 검증
        InOrder inOrder = inOrder(queryDslRepository, mapper);
        inOrder.verify(queryDslRepository).findBySliceCriteria(criteria);
        inOrder.verify(mapper).toDomain(entity1);
    }
}
