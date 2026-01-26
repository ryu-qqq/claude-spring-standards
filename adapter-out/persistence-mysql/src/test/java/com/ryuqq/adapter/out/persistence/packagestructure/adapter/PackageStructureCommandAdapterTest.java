package com.ryuqq.adapter.out.persistence.packagestructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import com.ryuqq.adapter.out.persistence.packagestructure.mapper.PackageStructureJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.packagestructure.repository.PackageStructureJpaRepository;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PackageStructureCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("PackageStructure Command Adapter 단위 테스트")
class PackageStructureCommandAdapterTest {

    @Mock private PackageStructureJpaRepository repository;

    @Mock private PackageStructureJpaEntityMapper mapper;

    @InjectMocks private PackageStructureCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        PackageStructure packageStructure = mock(PackageStructure.class);
        PackageStructureJpaEntity entity = mock(PackageStructureJpaEntity.class);
        PackageStructureJpaEntity savedEntity = mock(PackageStructureJpaEntity.class);

        when(mapper.toEntity(packageStructure)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        PackageStructureId result = commandAdapter.persist(packageStructure);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(packageStructure);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        PackageStructure packageStructure = mock(PackageStructure.class);
        PackageStructureJpaEntity entity = mock(PackageStructureJpaEntity.class);
        PackageStructureJpaEntity savedEntity = mock(PackageStructureJpaEntity.class);

        when(mapper.toEntity(packageStructure)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(packageStructure);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(packageStructure);
        inOrder.verify(repository).save(entity);
    }
}
