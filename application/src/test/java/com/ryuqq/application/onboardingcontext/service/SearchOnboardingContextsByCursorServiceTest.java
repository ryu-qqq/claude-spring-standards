package com.ryuqq.application.onboardingcontext.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.onboardingcontext.assembler.OnboardingContextAssembler;
import com.ryuqq.application.onboardingcontext.dto.query.OnboardingContextSearchParams;
import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextResult;
import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextSliceResult;
import com.ryuqq.application.onboardingcontext.factory.query.OnboardingContextQueryFactory;
import com.ryuqq.application.onboardingcontext.manager.OnboardingContextReadManager;
import com.ryuqq.domain.common.vo.SliceMeta;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
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
 * SearchOnboardingContextsByCursorService 단위 테스트
 *
 * <p>OnboardingContext 복합 조건 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchOnboardingContextsByCursorService 단위 테스트")
class SearchOnboardingContextsByCursorServiceTest {

    @Mock private OnboardingContextReadManager onboardingContextReadManager;

    @Mock private OnboardingContextQueryFactory onboardingContextQueryFactory;

    @Mock private OnboardingContextAssembler onboardingContextAssembler;

    @Mock private OnboardingContext onboardingContext;

    @Mock private OnboardingContextSliceCriteria criteria;

    @Mock private OnboardingContextResult onboardingContextResult;

    private SearchOnboardingContextsByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchOnboardingContextsByCursorService(
                        onboardingContextReadManager,
                        onboardingContextQueryFactory,
                        onboardingContextAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 검색 파라미터로 OnboardingContext 목록 조회")
        void execute_WithSearchParams_ShouldReturnSliceResult() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            OnboardingContextSearchParams searchParams =
                    OnboardingContextSearchParams.of(cursorParams);

            List<OnboardingContext> onboardingContexts = List.of(onboardingContext);
            List<OnboardingContextResult> results = List.of(onboardingContextResult);
            OnboardingContextSliceResult expectedResult =
                    new OnboardingContextSliceResult(results, SliceMeta.of(20, false));

            given(onboardingContextQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(onboardingContextReadManager.findBySliceCriteria(criteria))
                    .willReturn(onboardingContexts);
            given(onboardingContextAssembler.toSliceResult(onboardingContexts, 20))
                    .willReturn(expectedResult);

            // when
            OnboardingContextSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(expectedResult);

            then(onboardingContextQueryFactory).should().createSliceCriteria(searchParams);
            then(onboardingContextReadManager).should().findBySliceCriteria(criteria);
            then(onboardingContextAssembler).should().toSliceResult(onboardingContexts, 20);
        }

        @Test
        @DisplayName("성공 - 빈 결과 반환")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            OnboardingContextSearchParams searchParams =
                    OnboardingContextSearchParams.of(cursorParams);

            List<OnboardingContext> emptyList = List.of();
            OnboardingContextSliceResult expectedResult =
                    new OnboardingContextSliceResult(List.of(), SliceMeta.of(20, false));

            given(onboardingContextQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(onboardingContextReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(onboardingContextAssembler.toSliceResult(emptyList, 20))
                    .willReturn(expectedResult);

            // when
            OnboardingContextSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.sliceMeta().hasNext()).isFalse();
        }
    }
}
