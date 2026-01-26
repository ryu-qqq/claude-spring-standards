package com.ryuqq.adapter.out.persistence.archunittest.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.adapter.out.persistence.archunittest.mapper.ArchUnitTestJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.archunittest.repository.ArchUnitTestJpaRepository;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ArchUnitTestCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("ArchUnitTest Command Adapter 단위 테스트")
class ArchUnitTestCommandAdapterTest {

    @Mock private ArchUnitTestJpaRepository repository;

    @Mock private ArchUnitTestJpaEntityMapper mapper;

    @InjectMocks private ArchUnitTestCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        ArchUnitTest archUnitTest = mock(ArchUnitTest.class);
        ArchUnitTestJpaEntity entity = mock(ArchUnitTestJpaEntity.class);
        ArchUnitTestJpaEntity savedEntity = mock(ArchUnitTestJpaEntity.class);

        when(mapper.toEntity(archUnitTest)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        ArchUnitTestId result = commandAdapter.persist(archUnitTest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(archUnitTest);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        ArchUnitTest archUnitTest = mock(ArchUnitTest.class);
        ArchUnitTestJpaEntity entity = mock(ArchUnitTestJpaEntity.class);
        ArchUnitTestJpaEntity savedEntity = mock(ArchUnitTestJpaEntity.class);

        when(mapper.toEntity(archUnitTest)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(archUnitTest);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(archUnitTest);
        inOrder.verify(repository).save(entity);
    }
}
