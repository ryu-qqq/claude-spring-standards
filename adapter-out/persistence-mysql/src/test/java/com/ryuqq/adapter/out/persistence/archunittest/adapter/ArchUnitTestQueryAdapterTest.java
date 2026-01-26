package com.ryuqq.adapter.out.persistence.archunittest.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.adapter.out.persistence.archunittest.mapper.ArchUnitTestJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.archunittest.repository.ArchUnitTestQueryDslRepository;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
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
 * ArchUnitTestQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("ArchUnitTest Query Adapter 단위 테스트")
class ArchUnitTestQueryAdapterTest {

    @Mock private ArchUnitTestQueryDslRepository queryDslRepository;

    @Mock private ArchUnitTestJpaEntityMapper mapper;

    @InjectMocks private ArchUnitTestQueryAdapter queryAdapter;

    @Test
    @DisplayName("findById(Long) 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_WithLong_ShouldCallRepositoryAndMapper() {
        // Given
        Long id = 1L;
        ArchUnitTestJpaEntity entity = mock(ArchUnitTestJpaEntity.class);
        ArchUnitTest domain = mock(ArchUnitTest.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<ArchUnitTest> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findById(1L);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById(ArchUnitTestId) 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findById_WithArchUnitTestId_ShouldCallRepositoryAndMapper() {
        // Given
        ArchUnitTestId id = ArchUnitTestId.of(1L);
        ArchUnitTestJpaEntity entity = mock(ArchUnitTestJpaEntity.class);
        ArchUnitTest domain = mock(ArchUnitTest.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<ArchUnitTest> result = queryAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findById(1L);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findByStructureId() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findByStructureId_ShouldCallRepositoryAndMapper() {
        // Given
        Long structureId = 1L;
        ArchUnitTestJpaEntity entity = mock(ArchUnitTestJpaEntity.class);
        ArchUnitTest domain = mock(ArchUnitTest.class);

        when(queryDslRepository.findByStructureId(structureId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<ArchUnitTest> result = queryAdapter.findByStructureId(structureId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findByStructureId(structureId);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findByCode() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findByCode_ShouldCallRepositoryAndMapper() {
        // Given
        String code = "TEST-001";
        ArchUnitTestJpaEntity entity = mock(ArchUnitTestJpaEntity.class);
        ArchUnitTest domain = mock(ArchUnitTest.class);

        when(queryDslRepository.findByCode(code)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<ArchUnitTest> result = queryAdapter.findByCode(code);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domain);

        verify(queryDslRepository).findByCode(code);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findBySliceCriteria() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findBySliceCriteria_ShouldCallRepositoryAndMapper() {
        // Given
        ArchUnitTestSliceCriteria criteria = mock(ArchUnitTestSliceCriteria.class);
        ArchUnitTestJpaEntity entity = mock(ArchUnitTestJpaEntity.class);
        ArchUnitTest domain = mock(ArchUnitTest.class);

        when(queryDslRepository.findBySliceCriteria(criteria)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        List<ArchUnitTest> result = queryAdapter.findBySliceCriteria(criteria);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(domain);

        verify(queryDslRepository).findBySliceCriteria(criteria);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById() 호출 시 올바른 순서로 실행되어야 한다")
    void findById_ShouldExecuteInCorrectOrder() {
        // Given
        ArchUnitTestId id = ArchUnitTestId.of(1L);
        ArchUnitTestJpaEntity entity = mock(ArchUnitTestJpaEntity.class);
        ArchUnitTest domain = mock(ArchUnitTest.class);

        when(queryDslRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        queryAdapter.findById(id);

        // Then
        InOrder inOrder = inOrder(queryDslRepository, mapper);
        inOrder.verify(queryDslRepository).findById(1L);
        inOrder.verify(mapper).toDomain(entity);
    }
}
