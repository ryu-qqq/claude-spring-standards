package com.ryuqq.adapter.out.persistence.architecture.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.adapter.out.persistence.architecture.mapper.ArchitectureEntityMapper;
import com.ryuqq.adapter.out.persistence.architecture.repository.ArchitectureQueryDslRepository;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.techstack.id.TechStackId;
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
 * ArchitectureQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("Architecture Query Adapter 단위 테스트")
class ArchitectureQueryAdapterTest {

    @Mock private ArchitectureQueryDslRepository queryDslRepository;

    @Mock private ArchitectureEntityMapper mapper;

    @InjectMocks private ArchitectureQueryAdapter queryAdapter;

    @Test
    @DisplayName("findById() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_ShouldCallRepositoryAndMapper() {
        // Given
        ArchitectureId id = ArchitectureId.of(1L);
        ArchitectureJpaEntity entity = mock(ArchitectureJpaEntity.class);
        Architecture domain = mock(Architecture.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Architecture> result = queryAdapter.findById(id);

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
        ArchitectureId id = ArchitectureId.of(999L);

        when(queryDslRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Architecture> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isEmpty();

        verify(queryDslRepository).findById(999L);
        verify(mapper, never()).toDomain(any(ArchitectureJpaEntity.class));
    }

    @Test
    @DisplayName("existsById() 호출 시 Entity가 존재하면 true를 반환해야 한다")
    void existsById_WhenEntityExists_ShouldReturnTrue() {
        // Given
        ArchitectureId id = ArchitectureId.of(1L);

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
        ArchitectureId id = ArchitectureId.of(999L);

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
        ArchitectureSliceCriteria criteria = mock(ArchitectureSliceCriteria.class);
        ArchitectureJpaEntity entity1 = mock(ArchitectureJpaEntity.class);
        Architecture domain1 = mock(Architecture.class);

        when(queryDslRepository.findBySliceCriteria(criteria)).thenReturn(List.of(entity1));
        when(mapper.toDomain(entity1)).thenReturn(domain1);

        // When
        List<Architecture> result = queryAdapter.findBySliceCriteria(criteria);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain1);

        verify(queryDslRepository).findBySliceCriteria(criteria);
        verify(mapper).toDomain(entity1);
    }

    @Test
    @DisplayName("existsByName() 호출 시 Repository를 올바르게 호출해야 한다")
    void existsByName_ShouldCallRepository() {
        // Given
        ArchitectureName name = ArchitectureName.of("Test Architecture");

        when(queryDslRepository.existsByName("Test Architecture")).thenReturn(true);

        // When
        boolean result = queryAdapter.existsByName(name);

        // Then
        assertThat(result).isTrue();

        verify(queryDslRepository).existsByName("Test Architecture");
    }

    @Test
    @DisplayName("existsByNameAndIdNot() 호출 시 Repository를 올바르게 호출해야 한다")
    void existsByNameAndIdNot_ShouldCallRepository() {
        // Given
        ArchitectureName name = ArchitectureName.of("Test Architecture");
        ArchitectureId excludeId = ArchitectureId.of(1L);

        when(queryDslRepository.existsByNameAndIdNot("Test Architecture", 1L)).thenReturn(false);

        // When
        boolean result = queryAdapter.existsByNameAndIdNot(name, excludeId);

        // Then
        assertThat(result).isFalse();

        verify(queryDslRepository).existsByNameAndIdNot("Test Architecture", 1L);
    }

    @Test
    @DisplayName("existsByTechStackId() 호출 시 Repository를 올바르게 호출해야 한다")
    void existsByTechStackId_ShouldCallRepository() {
        // Given
        TechStackId techStackId = TechStackId.of(1L);

        when(queryDslRepository.existsByTechStackId(1L)).thenReturn(true);

        // When
        boolean result = queryAdapter.existsByTechStackId(techStackId);

        // Then
        assertThat(result).isTrue();

        verify(queryDslRepository).existsByTechStackId(1L);
    }

    @Test
    @DisplayName("findById() 호출 시 올바른 순서로 실행되어야 한다")
    void findById_ShouldExecuteInCorrectOrder() {
        // Given
        ArchitectureId id = ArchitectureId.of(1L);
        ArchitectureJpaEntity entity = mock(ArchitectureJpaEntity.class);
        Architecture domain = mock(Architecture.class);

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
