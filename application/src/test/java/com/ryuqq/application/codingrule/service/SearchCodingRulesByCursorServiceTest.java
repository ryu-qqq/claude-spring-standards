package com.ryuqq.application.codingrule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.codingrule.assembler.CodingRuleAssembler;
import com.ryuqq.application.codingrule.dto.query.CodingRuleSearchParams;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.application.codingrule.factory.query.CodingRuleQueryFactory;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
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
 * SearchCodingRulesByCursorService 단위 테스트
 *
 * <p>CodingRule 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchCodingRulesByCursorService 단위 테스트")
class SearchCodingRulesByCursorServiceTest {

    @Mock private CodingRuleQueryFactory codingRuleQueryFactory;

    @Mock private CodingRuleReadManager codingRuleReadManager;

    @Mock private CodingRuleAssembler codingRuleAssembler;

    @Mock private CodingRuleSliceCriteria criteria;

    @Mock private CodingRule codingRule;

    @Mock private CodingRuleSliceResult sliceResult;

    private SearchCodingRulesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchCodingRulesByCursorService(
                        codingRuleQueryFactory, codingRuleReadManager, codingRuleAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 CodingRule 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            CodingRuleSearchParams searchParams = createDefaultSearchParams();
            List<CodingRule> codingRules = List.of(codingRule);

            given(codingRuleQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(codingRuleReadManager.findBySliceCriteria(criteria)).willReturn(codingRules);
            given(codingRuleAssembler.toSliceResult(codingRules, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            CodingRuleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(codingRuleQueryFactory).should().createSliceCriteria(searchParams);
            then(codingRuleReadManager).should().findBySliceCriteria(criteria);
            then(codingRuleAssembler).should().toSliceResult(codingRules, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            CodingRuleSearchParams searchParams = createDefaultSearchParams();
            List<CodingRule> emptyList = List.of();

            given(codingRuleQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(codingRuleReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(codingRuleAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            CodingRuleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(codingRuleQueryFactory).should().createSliceCriteria(searchParams);
            then(codingRuleReadManager).should().findBySliceCriteria(criteria);
            then(codingRuleAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private CodingRuleSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return CodingRuleSearchParams.of(cursorParams);
    }
}
