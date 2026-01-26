package com.ryuqq.adapter.out.persistence.layerdependency.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.layerdependency.mapper.LayerDependencyRuleEntityMapper;
import com.ryuqq.adapter.out.persistence.layerdependency.repository.LayerDependencyRuleQueryDslRepository;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
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
 * LayerDependencyRuleQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("LayerDependencyRule Query Adapter 단위 테스트")
class LayerDependencyRuleQueryAdapterTest {

    @Mock private LayerDependencyRuleQueryDslRepository queryDslRepository;

    @Mock private LayerDependencyRuleEntityMapper mapper;

    @InjectMocks private LayerDependencyRuleQueryAdapter queryAdapter;

    @Test
    @DisplayName("findById() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_ShouldCallRepositoryAndMapper() {
        // Given
        LayerDependencyRuleId id = LayerDependencyRuleId.of(1L);
        LayerDependencyRuleJpaEntity entity = mock(LayerDependencyRuleJpaEntity.class);
        LayerDependencyRule domain = mock(LayerDependencyRule.class);

        when(queryDslRepository.findById(id.value())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<LayerDependencyRule> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findById(id.value());
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById() 호출 시 Entity가 없으면 빈 Optional을 반환해야 한다")
    void findById_WhenEntityNotFound_ShouldReturnEmptyOptional() {
        // Given
        LayerDependencyRuleId id = LayerDependencyRuleId.of(1L);

        when(queryDslRepository.findById(id.value())).thenReturn(Optional.empty());

        // When
        Optional<LayerDependencyRule> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isEmpty();

        verify(queryDslRepository).findById(id.value());
        verify(mapper, never()).toDomain(any(LayerDependencyRuleJpaEntity.class));
    }

    @Test
    @DisplayName("findById() 호출 시 올바른 순서로 실행되어야 한다")
    void findById_ShouldExecuteInCorrectOrder() {
        // Given
        LayerDependencyRuleId id = LayerDependencyRuleId.of(1L);
        LayerDependencyRuleJpaEntity entity = mock(LayerDependencyRuleJpaEntity.class);
        LayerDependencyRule domain = mock(LayerDependencyRule.class);

        when(queryDslRepository.findById(id.value())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        queryAdapter.findById(id);

        // Then
        InOrder inOrder = inOrder(queryDslRepository, mapper);
        inOrder.verify(queryDslRepository).findById(id.value());
        inOrder.verify(mapper).toDomain(entity);
    }
}
