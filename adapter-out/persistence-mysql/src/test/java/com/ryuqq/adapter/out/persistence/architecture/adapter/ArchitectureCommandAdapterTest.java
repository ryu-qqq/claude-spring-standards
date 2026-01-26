package com.ryuqq.adapter.out.persistence.architecture.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.adapter.out.persistence.architecture.mapper.ArchitectureEntityMapper;
import com.ryuqq.adapter.out.persistence.architecture.repository.ArchitectureJpaRepository;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ArchitectureCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("Architecture Command Adapter 단위 테스트")
class ArchitectureCommandAdapterTest {

    @Mock private ArchitectureJpaRepository repository;

    @Mock private ArchitectureEntityMapper mapper;

    @InjectMocks private ArchitectureCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        Architecture architecture = mock(Architecture.class);
        ArchitectureJpaEntity entity = mock(ArchitectureJpaEntity.class);
        ArchitectureJpaEntity savedEntity = mock(ArchitectureJpaEntity.class);

        when(mapper.toEntity(architecture)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        Long result = commandAdapter.persist(architecture);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);

        verify(mapper).toEntity(architecture);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        Architecture architecture = mock(Architecture.class);
        ArchitectureJpaEntity entity = mock(ArchitectureJpaEntity.class);
        ArchitectureJpaEntity savedEntity = mock(ArchitectureJpaEntity.class);

        when(mapper.toEntity(architecture)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(architecture);

        // Then - 실행 순서 검증
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(architecture);
        inOrder.verify(repository).save(entity);
    }
}
