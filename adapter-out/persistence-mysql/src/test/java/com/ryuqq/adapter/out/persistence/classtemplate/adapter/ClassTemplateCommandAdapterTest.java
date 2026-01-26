package com.ryuqq.adapter.out.persistence.classtemplate.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.classtemplate.mapper.ClassTemplateJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.classtemplate.repository.ClassTemplateJpaRepository;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ClassTemplateCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("ClassTemplate Command Adapter 단위 테스트")
class ClassTemplateCommandAdapterTest {

    @Mock private ClassTemplateJpaRepository repository;

    @Mock private ClassTemplateJpaEntityMapper mapper;

    @InjectMocks private ClassTemplateCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        ClassTemplate classTemplate = mock(ClassTemplate.class);
        ClassTemplateJpaEntity entity = mock(ClassTemplateJpaEntity.class);
        ClassTemplateJpaEntity savedEntity = mock(ClassTemplateJpaEntity.class);

        when(mapper.toEntity(classTemplate)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        ClassTemplateId result = commandAdapter.persist(classTemplate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(classTemplate);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        ClassTemplate classTemplate = mock(ClassTemplate.class);
        ClassTemplateJpaEntity entity = mock(ClassTemplateJpaEntity.class);
        ClassTemplateJpaEntity savedEntity = mock(ClassTemplateJpaEntity.class);

        when(mapper.toEntity(classTemplate)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(classTemplate);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(classTemplate);
        inOrder.verify(repository).save(entity);
    }
}
