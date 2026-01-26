package com.ryuqq.adapter.out.persistence.codingrule.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.codingrule.mapper.CodingRuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.codingrule.repository.CodingRuleJpaRepository;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CodingRuleCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("CodingRule Command Adapter 단위 테스트")
class CodingRuleCommandAdapterTest {

    @Mock private CodingRuleJpaRepository repository;

    @Mock private CodingRuleJpaEntityMapper mapper;

    @InjectMocks private CodingRuleCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        CodingRule codingRule = mock(CodingRule.class);
        CodingRuleJpaEntity entity = mock(CodingRuleJpaEntity.class);
        CodingRuleJpaEntity savedEntity = mock(CodingRuleJpaEntity.class);

        when(mapper.toEntity(codingRule)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        CodingRuleId result = commandAdapter.persist(codingRule);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(codingRule);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        CodingRule codingRule = mock(CodingRule.class);
        CodingRuleJpaEntity entity = mock(CodingRuleJpaEntity.class);
        CodingRuleJpaEntity savedEntity = mock(CodingRuleJpaEntity.class);

        when(mapper.toEntity(codingRule)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(codingRule);

        // Then - 실행 순서 검증
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(codingRule);
        inOrder.verify(repository).save(entity);
    }
}
