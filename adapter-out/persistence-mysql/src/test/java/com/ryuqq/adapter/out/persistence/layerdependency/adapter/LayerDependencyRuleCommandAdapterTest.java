package com.ryuqq.adapter.out.persistence.layerdependency.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.layerdependency.mapper.LayerDependencyRuleEntityMapper;
import com.ryuqq.adapter.out.persistence.layerdependency.repository.LayerDependencyRuleJpaRepository;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LayerDependencyRuleCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("LayerDependencyRule Command Adapter 단위 테스트")
class LayerDependencyRuleCommandAdapterTest {

    @Mock private LayerDependencyRuleJpaRepository repository;

    @Mock private LayerDependencyRuleEntityMapper mapper;

    @InjectMocks private LayerDependencyRuleCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        LayerDependencyRule layerDependencyRule = mock(LayerDependencyRule.class);
        LayerDependencyRuleJpaEntity entity = mock(LayerDependencyRuleJpaEntity.class);
        LayerDependencyRuleJpaEntity savedEntity = mock(LayerDependencyRuleJpaEntity.class);

        when(mapper.toEntity(layerDependencyRule)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        LayerDependencyRuleId result = commandAdapter.persist(layerDependencyRule);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(layerDependencyRule);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        LayerDependencyRule layerDependencyRule = mock(LayerDependencyRule.class);
        LayerDependencyRuleJpaEntity entity = mock(LayerDependencyRuleJpaEntity.class);
        LayerDependencyRuleJpaEntity savedEntity = mock(LayerDependencyRuleJpaEntity.class);

        when(mapper.toEntity(layerDependencyRule)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(layerDependencyRule);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(layerDependencyRule);
        inOrder.verify(repository).save(entity);
    }
}
