package com.ryuqq.adapter.out.persistence.module.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
import com.ryuqq.adapter.out.persistence.module.mapper.ModuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.module.repository.ModuleJpaRepository;
import com.ryuqq.domain.module.aggregate.Module;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ModuleCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("Module Command Adapter 단위 테스트")
class ModuleCommandAdapterTest {

    @Mock private ModuleJpaRepository repository;

    @Mock private ModuleJpaEntityMapper mapper;

    @InjectMocks private ModuleCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        Module module = mock(Module.class);
        ModuleJpaEntity entity = mock(ModuleJpaEntity.class);
        ModuleJpaEntity savedEntity = mock(ModuleJpaEntity.class);
        Long expectedId = 1L;

        when(mapper.toEntity(module)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(expectedId);

        // When
        Long result = commandAdapter.persist(module);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedId);

        verify(mapper).toEntity(module);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        Module module = mock(Module.class);
        ModuleJpaEntity entity = mock(ModuleJpaEntity.class);
        ModuleJpaEntity savedEntity = mock(ModuleJpaEntity.class);
        Long expectedId = 1L;

        when(mapper.toEntity(module)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(expectedId);

        // When
        commandAdapter.persist(module);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(module);
        inOrder.verify(repository).save(entity);
    }
}
