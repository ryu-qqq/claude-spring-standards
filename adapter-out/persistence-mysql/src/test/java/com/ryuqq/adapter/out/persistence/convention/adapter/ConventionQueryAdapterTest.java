package com.ryuqq.adapter.out.persistence.convention.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import com.ryuqq.adapter.out.persistence.convention.mapper.ConventionJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.convention.repository.ConventionQueryDslRepository;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.module.id.ModuleId;
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
 * ConventionQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("Convention Query Adapter 단위 테스트")
class ConventionQueryAdapterTest {

    private static final Long DEFAULT_MODULE_ID = 1L;

    @Mock private ConventionQueryDslRepository queryDslRepository;

    @Mock private ConventionJpaEntityMapper mapper;

    @InjectMocks private ConventionQueryAdapter queryAdapter;

    @Test
    @DisplayName("findById(Long) 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_WithLong_ShouldCallRepositoryAndMapper() {
        // Given
        Long id = 1L;
        ConventionJpaEntity entity = mock(ConventionJpaEntity.class);
        Convention domain = mock(Convention.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Convention> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findById(1L);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById(ConventionId) 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_WithConventionId_ShouldCallRepositoryAndMapper() {
        // Given
        ConventionId id = ConventionId.of(1L);
        ConventionJpaEntity entity = mock(ConventionJpaEntity.class);
        Convention domain = mock(Convention.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Convention> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findById(1L);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findAllActive() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findAllActive_ShouldCallRepositoryAndMapper() {
        // Given
        ConventionJpaEntity entity = mock(ConventionJpaEntity.class);
        Convention domain = mock(Convention.class);

        when(queryDslRepository.findAllActive()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<Convention> result = queryAdapter.findAllActive();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findAllActive();
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findActiveByModuleId() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findActiveByModuleId_ShouldCallRepositoryAndMapper() {
        // Given
        ModuleId moduleId = ModuleId.of(DEFAULT_MODULE_ID);
        ConventionJpaEntity entity = mock(ConventionJpaEntity.class);
        Convention domain = mock(Convention.class);

        when(queryDslRepository.findActiveByModuleId(DEFAULT_MODULE_ID))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Convention> result = queryAdapter.findActiveByModuleId(moduleId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findActiveByModuleId(DEFAULT_MODULE_ID);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("existsById() 호출 시 Repository를 올바르게 호출해야 한다")
    void existsById_ShouldCallRepository() {
        // Given
        ConventionId id = ConventionId.of(1L);

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
        ConventionId id = ConventionId.of(1L);
        ConventionJpaEntity entity = mock(ConventionJpaEntity.class);
        Convention domain = mock(Convention.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        queryAdapter.findById(id);

        // Then
        InOrder inOrder = inOrder(queryDslRepository, mapper);
        inOrder.verify(queryDslRepository).findById(1L);
        inOrder.verify(mapper).toDomain(entity);
    }
}
