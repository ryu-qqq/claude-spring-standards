package com.ryuqq.adapter.out.persistence.ruleexample.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.ruleexample.mapper.RuleExampleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.ruleexample.repository.RuleExampleJpaRepository;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RuleExampleCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("RuleExample Command Adapter 단위 테스트")
class RuleExampleCommandAdapterTest {

    @Mock private RuleExampleJpaRepository repository;

    @Mock private RuleExampleJpaEntityMapper mapper;

    @InjectMocks private RuleExampleCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        RuleExample ruleExample = mock(RuleExample.class);
        RuleExampleJpaEntity entity = mock(RuleExampleJpaEntity.class);
        RuleExampleJpaEntity savedEntity = mock(RuleExampleJpaEntity.class);

        when(mapper.toEntity(ruleExample)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        RuleExampleId result = commandAdapter.persist(ruleExample);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(ruleExample);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        RuleExample ruleExample = mock(RuleExample.class);
        RuleExampleJpaEntity entity = mock(RuleExampleJpaEntity.class);
        RuleExampleJpaEntity savedEntity = mock(RuleExampleJpaEntity.class);

        when(mapper.toEntity(ruleExample)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(ruleExample);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(ruleExample);
        inOrder.verify(repository).save(entity);
    }
}
