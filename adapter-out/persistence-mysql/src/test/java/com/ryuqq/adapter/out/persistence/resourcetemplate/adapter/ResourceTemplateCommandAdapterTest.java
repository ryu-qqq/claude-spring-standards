package com.ryuqq.adapter.out.persistence.resourcetemplate.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.resourcetemplate.mapper.ResourceTemplateJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.resourcetemplate.repository.ResourceTemplateJpaRepository;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ResourceTemplateCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("ResourceTemplate Command Adapter 단위 테스트")
class ResourceTemplateCommandAdapterTest {

    @Mock private ResourceTemplateJpaRepository repository;

    @Mock private ResourceTemplateJpaEntityMapper mapper;

    @InjectMocks private ResourceTemplateCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        ResourceTemplate resourceTemplate = mock(ResourceTemplate.class);
        ResourceTemplateJpaEntity entity = mock(ResourceTemplateJpaEntity.class);
        ResourceTemplateJpaEntity savedEntity = mock(ResourceTemplateJpaEntity.class);

        when(mapper.toEntity(resourceTemplate)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        ResourceTemplateId result = commandAdapter.persist(resourceTemplate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(resourceTemplate);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        ResourceTemplate resourceTemplate = mock(ResourceTemplate.class);
        ResourceTemplateJpaEntity entity = mock(ResourceTemplateJpaEntity.class);
        ResourceTemplateJpaEntity savedEntity = mock(ResourceTemplateJpaEntity.class);

        when(mapper.toEntity(resourceTemplate)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(resourceTemplate);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(resourceTemplate);
        inOrder.verify(repository).save(entity);
    }
}
