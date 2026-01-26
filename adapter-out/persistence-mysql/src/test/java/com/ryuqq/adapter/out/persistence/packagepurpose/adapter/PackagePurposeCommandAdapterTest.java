package com.ryuqq.adapter.out.persistence.packagepurpose.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
import com.ryuqq.adapter.out.persistence.packagepurpose.mapper.PackagePurposeEntityMapper;
import com.ryuqq.adapter.out.persistence.packagepurpose.repository.PackagePurposeJpaRepository;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PackagePurposeCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("command")
@Tag("persistence-layer")
@DisplayName("PackagePurpose Command Adapter 단위 테스트")
class PackagePurposeCommandAdapterTest {

    @Mock private PackagePurposeJpaRepository repository;

    @Mock private PackagePurposeEntityMapper mapper;

    @InjectMocks private PackagePurposeCommandAdapter commandAdapter;

    @Test
    @DisplayName("persist() 호출 시 Mapper와 Repository를 올바르게 호출해야 한다")
    void persist_ShouldCallMapperAndRepository() {
        // Given
        PackagePurpose packagePurpose = mock(PackagePurpose.class);
        PackagePurposeJpaEntity entity = mock(PackagePurposeJpaEntity.class);
        PackagePurposeJpaEntity savedEntity = mock(PackagePurposeJpaEntity.class);

        when(mapper.toEntity(packagePurpose)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        PackagePurposeId result = commandAdapter.persist(packagePurpose);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1L);

        verify(mapper).toEntity(packagePurpose);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("persist() 호출 시 올바른 순서로 실행되어야 한다")
    void persist_ShouldExecuteInCorrectOrder() {
        // Given
        PackagePurpose packagePurpose = mock(PackagePurpose.class);
        PackagePurposeJpaEntity entity = mock(PackagePurposeJpaEntity.class);
        PackagePurposeJpaEntity savedEntity = mock(PackagePurposeJpaEntity.class);

        when(mapper.toEntity(packagePurpose)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(savedEntity.getId()).thenReturn(1L);

        // When
        commandAdapter.persist(packagePurpose);

        // Then
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(packagePurpose);
        inOrder.verify(repository).save(entity);
    }
}
