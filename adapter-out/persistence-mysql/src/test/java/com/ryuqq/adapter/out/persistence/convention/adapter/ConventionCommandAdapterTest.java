package com.ryuqq.adapter.out.persistence.convention.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import com.ryuqq.adapter.out.persistence.convention.mapper.ConventionJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.convention.repository.ConventionJpaRepository;
import com.ryuqq.domain.convention.aggregate.Convention;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ConventionCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("Convention Command Adapter 단위 테스트")
class ConventionCommandAdapterTest {

    @Mock private ConventionJpaRepository repository;

    @Mock private ConventionJpaEntityMapper mapper;

    @InjectMocks private ConventionCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        Convention convention = mock(Convention.class);
        ConventionJpaEntity entity = mock(ConventionJpaEntity.class);
        ConventionJpaEntity savedEntity = mock(ConventionJpaEntity.class);

        when(mapper.toEntity(convention)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        Long result = commandAdapter.persist(convention);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);

        verify(mapper).toEntity(convention);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        Convention convention = mock(Convention.class);
        ConventionJpaEntity entity = mock(ConventionJpaEntity.class);
        ConventionJpaEntity savedEntity = mock(ConventionJpaEntity.class);

        when(mapper.toEntity(convention)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(convention);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(convention);
        inOrder.verify(repository).save(entity);
    }
}
