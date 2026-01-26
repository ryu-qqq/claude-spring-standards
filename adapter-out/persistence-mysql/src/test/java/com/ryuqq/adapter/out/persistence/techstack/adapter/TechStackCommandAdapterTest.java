package com.ryuqq.adapter.out.persistence.techstack.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.techstack.entity.TechStackJpaEntity;
import com.ryuqq.adapter.out.persistence.techstack.mapper.TechStackEntityMapper;
import com.ryuqq.adapter.out.persistence.techstack.repository.TechStackJpaRepository;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TechStackCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("TechStack Command Adapter 단위 테스트")
class TechStackCommandAdapterTest {

    @Mock private TechStackJpaRepository repository;

    @Mock private TechStackEntityMapper mapper;

    @InjectMocks private TechStackCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        TechStack techStack = mock(TechStack.class);
        TechStackJpaEntity entity = mock(TechStackJpaEntity.class);
        TechStackJpaEntity savedEntity = mock(TechStackJpaEntity.class);

        when(mapper.toEntity(techStack)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        Long result = commandAdapter.persist(techStack);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);

        verify(mapper).toEntity(techStack);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        TechStack techStack = mock(TechStack.class);
        TechStackJpaEntity entity = mock(TechStackJpaEntity.class);
        TechStackJpaEntity savedEntity = mock(TechStackJpaEntity.class);

        when(mapper.toEntity(techStack)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(techStack);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(techStack);
        inOrder.verify(repository).save(entity);
    }
}
