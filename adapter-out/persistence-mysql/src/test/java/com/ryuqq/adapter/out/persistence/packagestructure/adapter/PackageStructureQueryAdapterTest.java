package com.ryuqq.adapter.out.persistence.packagestructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import com.ryuqq.adapter.out.persistence.packagestructure.mapper.PackageStructureJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.packagestructure.repository.PackageStructureQueryDslRepository;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
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
 * PackageStructureQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("PackageStructure Query Adapter 단위 테스트")
class PackageStructureQueryAdapterTest {

    @Mock private PackageStructureQueryDslRepository queryDslRepository;

    @Mock private PackageStructureJpaEntityMapper mapper;

    @InjectMocks private PackageStructureQueryAdapter queryAdapter;

    @Test
    @DisplayName("findById() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_ShouldCallRepositoryAndMapper() {
        // Given
        PackageStructureId id = PackageStructureId.of(1L);
        PackageStructureJpaEntity entity = mock(PackageStructureJpaEntity.class);
        PackageStructure domain = mock(PackageStructure.class);

        when(queryDslRepository.findById(id.value())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<PackageStructure> result = queryAdapter.findById(id);

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
        PackageStructureId id = PackageStructureId.of(1L);

        when(queryDslRepository.findById(id.value())).thenReturn(Optional.empty());

        // When
        Optional<PackageStructure> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isEmpty();

        verify(queryDslRepository).findById(id.value());
        verify(mapper, never()).toDomain(any(PackageStructureJpaEntity.class));
    }

    @Test
    @DisplayName("findById() 호출 시 올바른 순서로 실행되어야 한다")
    void findById_ShouldExecuteInCorrectOrder() {
        // Given
        PackageStructureId id = PackageStructureId.of(1L);
        PackageStructureJpaEntity entity = mock(PackageStructureJpaEntity.class);
        PackageStructure domain = mock(PackageStructure.class);

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
