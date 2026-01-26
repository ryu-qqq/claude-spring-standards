package com.ryuqq.adapter.out.persistence.zerotolerance.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.adapter.out.persistence.checklistitem.entity.ChecklistItemJpaEntity;
import com.ryuqq.adapter.out.persistence.checklistitem.mapper.ChecklistItemJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.codingrule.mapper.CodingRuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.ruleexample.entity.RuleExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.ruleexample.mapper.RuleExampleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.zerotolerance.repository.ZeroToleranceRuleQueryDslRepository;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleDetailResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ZeroToleranceRuleQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("query")
@Tag("persistence-layer")
@DisplayName("ZeroTolerance Rule Query Adapter 단위 테스트")
class ZeroToleranceRuleQueryAdapterTest {

    @Mock private ZeroToleranceRuleQueryDslRepository queryDslRepository;

    @Mock private CodingRuleJpaEntityMapper codingRuleMapper;

    @Mock private RuleExampleJpaEntityMapper ruleExampleMapper;

    @Mock private ChecklistItemJpaEntityMapper checklistItemMapper;

    @InjectMocks private ZeroToleranceRuleQueryAdapter queryAdapter;

    @Test
    @DisplayName("findDetailById() 호출 시 Repository와 Mapper를 올바르게 호출해야 한다")
    void findDetailById_ShouldCallRepositoryAndMappers() {
        // Given
        Long ruleId = 1L;
        CodingRuleJpaEntity codingRuleEntity = mock(CodingRuleJpaEntity.class);
        RuleExampleJpaEntity exampleEntity = mock(RuleExampleJpaEntity.class);
        ChecklistItemJpaEntity checklistEntity = mock(ChecklistItemJpaEntity.class);

        // Domain mocks with deep stubs for Result.from() calls
        CodingRule codingRule = mock(CodingRule.class, RETURNS_DEEP_STUBS);
        RuleExample ruleExample = mock(RuleExample.class, RETURNS_DEEP_STUBS);
        ChecklistItem checklistItem = mock(ChecklistItem.class, RETURNS_DEEP_STUBS);

        when(queryDslRepository.findZeroToleranceRuleById(ruleId))
                .thenReturn(Optional.of(codingRuleEntity));
        when(queryDslRepository.findRuleExamplesByRuleId(ruleId))
                .thenReturn(List.of(exampleEntity));
        when(queryDslRepository.findChecklistItemsByRuleId(ruleId))
                .thenReturn(List.of(checklistEntity));

        when(codingRuleMapper.toDomain(codingRuleEntity)).thenReturn(codingRule);
        when(ruleExampleMapper.toDomain(exampleEntity)).thenReturn(ruleExample);
        when(checklistItemMapper.toDomain(checklistEntity)).thenReturn(checklistItem);

        // When
        Optional<ZeroToleranceRuleDetailResult> result = queryAdapter.findDetailById(ruleId);

        // Then
        assertThat(result).isPresent();

        verify(queryDslRepository).findZeroToleranceRuleById(ruleId);
        verify(queryDslRepository).findRuleExamplesByRuleId(ruleId);
        verify(queryDslRepository).findChecklistItemsByRuleId(ruleId);
    }

    @Test
    @DisplayName("findDetailById() 호출 시 CodingRule이 없으면 빈 Optional을 반환해야 한다")
    void findDetailById_WhenCodingRuleNotFound_ShouldReturnEmptyOptional() {
        // Given
        Long ruleId = 999L;

        when(queryDslRepository.findZeroToleranceRuleById(ruleId)).thenReturn(Optional.empty());

        // When
        Optional<ZeroToleranceRuleDetailResult> result = queryAdapter.findDetailById(ruleId);

        // Then
        assertThat(result).isEmpty();

        verify(queryDslRepository).findZeroToleranceRuleById(ruleId);
    }

    @Test
    @DisplayName("findAllDetails() 호출 시 Repository를 올바르게 호출해야 한다")
    void findAllDetails_ShouldCallRepository() {
        // Given
        ZeroToleranceRuleSliceCriteria criteria = ZeroToleranceRuleSliceCriteria.afterId(100L, 10);

        when(queryDslRepository.findBySliceCriteria(criteria)).thenReturn(List.of());

        // When
        ZeroToleranceRuleSliceResult result = queryAdapter.findAllDetails(criteria);

        // Then
        assertThat(result).isNotNull();

        verify(queryDslRepository).findBySliceCriteria(criteria);
    }
}
