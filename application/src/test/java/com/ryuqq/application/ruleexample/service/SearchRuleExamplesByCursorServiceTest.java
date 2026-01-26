package com.ryuqq.application.ruleexample.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.ruleexample.assembler.RuleExampleAssembler;
import com.ryuqq.application.ruleexample.dto.query.RuleExampleSearchParams;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import com.ryuqq.application.ruleexample.factory.query.RuleExampleQueryFactory;
import com.ryuqq.application.ruleexample.manager.RuleExampleReadManager;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
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
 * SearchRuleExamplesByCursorService 단위 테스트
 *
 * <p>RuleExample 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchRuleExamplesByCursorService 단위 테스트")
class SearchRuleExamplesByCursorServiceTest {

    @Mock private RuleExampleQueryFactory ruleExampleQueryFactory;

    @Mock private RuleExampleReadManager ruleExampleReadManager;

    @Mock private RuleExampleAssembler ruleExampleAssembler;

    @Mock private RuleExampleSliceCriteria criteria;

    @Mock private RuleExample ruleExample;

    @Mock private RuleExampleSliceResult sliceResult;

    private SearchRuleExamplesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchRuleExamplesByCursorService(
                        ruleExampleQueryFactory, ruleExampleReadManager, ruleExampleAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 RuleExample 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            RuleExampleSearchParams searchParams = createDefaultSearchParams();
            List<RuleExample> ruleExamples = List.of(ruleExample);

            given(ruleExampleQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(ruleExampleReadManager.findBySliceCriteria(criteria)).willReturn(ruleExamples);
            given(ruleExampleAssembler.toSliceResult(ruleExamples, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            RuleExampleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(ruleExampleQueryFactory).should().createSliceCriteria(searchParams);
            then(ruleExampleReadManager).should().findBySliceCriteria(criteria);
            then(ruleExampleAssembler).should().toSliceResult(ruleExamples, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            RuleExampleSearchParams searchParams = createDefaultSearchParams();
            List<RuleExample> emptyList = List.of();

            given(ruleExampleQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(ruleExampleReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(ruleExampleAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            RuleExampleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(ruleExampleQueryFactory).should().createSliceCriteria(searchParams);
            then(ruleExampleReadManager).should().findBySliceCriteria(criteria);
            then(ruleExampleAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private RuleExampleSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return RuleExampleSearchParams.of(cursorParams);
    }
}
