package com.ryuqq.application.layerdependency.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.layerdependency.assembler.LayerDependencyRuleAssembler;
import com.ryuqq.application.layerdependency.dto.query.LayerDependencyRuleSearchParams;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleResult;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleSliceResult;
import com.ryuqq.application.layerdependency.factory.query.LayerDependencyRuleQueryFactory;
import com.ryuqq.application.layerdependency.port.out.LayerDependencyRuleQueryPort;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.query.LayerDependencyRuleSliceCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SearchLayerDependencyRulesByCursorService 단위 테스트
 *
 * <p>LayerDependencyRule 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchLayerDependencyRulesByCursorService 단위 테스트")
class SearchLayerDependencyRulesByCursorServiceTest {

    @Mock private LayerDependencyRuleQueryFactory queryFactory;

    @Mock private LayerDependencyRuleQueryPort queryPort;

    @Mock private LayerDependencyRuleAssembler assembler;

    @Mock private LayerDependencyRuleSliceCriteria criteria;

    @Mock private LayerDependencyRule rule;

    @Mock private LayerDependencyRuleResult ruleResult;

    private SearchLayerDependencyRulesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchLayerDependencyRulesByCursorService(queryFactory, queryPort, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 LayerDependencyRule 목록 조회 (다음 페이지 없음)")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            LayerDependencyRuleSearchParams searchParams = createDefaultSearchParams();
            List<LayerDependencyRule> rules = List.of(rule);

            given(queryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(queryPort.findBySliceCriteria(criteria)).willReturn(rules);
            given(criteria.size()).willReturn(20);
            given(assembler.toResult(rule)).willReturn(ruleResult);

            // when
            LayerDependencyRuleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.rules()).containsExactly(ruleResult);
            assertThat(result.hasNext()).isFalse();

            then(queryFactory).should().createSliceCriteria(searchParams);
            then(queryPort).should().findBySliceCriteria(criteria);
            then(assembler).should().toResult(rule);
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            LayerDependencyRuleSearchParams searchParams = createDefaultSearchParams();
            List<LayerDependencyRule> emptyList = List.of();

            given(queryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(queryPort.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(criteria.size()).willReturn(20);

            // when
            LayerDependencyRuleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.rules()).isEmpty();
            assertThat(result.hasNext()).isFalse();

            then(queryFactory).should().createSliceCriteria(searchParams);
            then(queryPort).should().findBySliceCriteria(criteria);
        }
    }

    private LayerDependencyRuleSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return LayerDependencyRuleSearchParams.of(cursorParams);
    }
}
