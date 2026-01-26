package com.ryuqq.adapter.out.persistence.classtemplate.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.classtemplate.mapper.ClassTemplateJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.classtemplate.repository.ClassTemplateQueryDslRepository;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
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
 * ClassTemplateQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("ClassTemplate Query Adapter 단위 테스트")
class ClassTemplateQueryAdapterTest {

    @Mock private ClassTemplateQueryDslRepository queryDslRepository;

    @Mock private ClassTemplateJpaEntityMapper mapper;

    @InjectMocks private ClassTemplateQueryAdapter queryAdapter;

    @Test
    @DisplayName("findById() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_ShouldCallRepositoryAndMapper() {
        // Given
        ClassTemplateId id = ClassTemplateId.of(1L);
        ClassTemplateJpaEntity entity = mock(ClassTemplateJpaEntity.class);
        ClassTemplate domain = mock(ClassTemplate.class);

        when(queryDslRepository.findById(id.value())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<ClassTemplate> result = queryAdapter.findById(id);

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
        ClassTemplateId id = ClassTemplateId.of(1L);

        when(queryDslRepository.findById(id.value())).thenReturn(Optional.empty());

        // When
        Optional<ClassTemplate> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isEmpty();

        verify(queryDslRepository).findById(id.value());
        verify(mapper, never()).toDomain(any(ClassTemplateJpaEntity.class));
    }

    @Test
    @DisplayName("findById() 호출 시 올바른 순서로 실행되어야 한다")
    void findById_ShouldExecuteInCorrectOrder() {
        // Given
        ClassTemplateId id = ClassTemplateId.of(1L);
        ClassTemplateJpaEntity entity = mock(ClassTemplateJpaEntity.class);
        ClassTemplate domain = mock(ClassTemplate.class);

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
