package com.ryuqq.application.feedbackqueue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.feedbackqueue.assembler.FeedbackQueueAssembler;
import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import com.ryuqq.application.feedbackqueue.factory.query.FeedbackQueueQueryFactory;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueueReadManager;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
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
 * SearchFeedbacksByCursorService 단위 테스트
 *
 * <p>FeedbackQueue 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchFeedbacksByCursorService 단위 테스트")
class SearchFeedbacksByCursorServiceTest {

    @Mock private FeedbackQueueQueryFactory queryFactory;

    @Mock private FeedbackQueueReadManager readManager;

    @Mock private FeedbackQueueAssembler assembler;

    @Mock private FeedbackQueueSliceCriteria criteria;

    @Mock private FeedbackQueue feedbackQueue;

    @Mock private FeedbackQueueSliceResult sliceResult;

    private SearchFeedbacksByCursorService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchFeedbacksByCursorService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 FeedbackQueue 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            FeedbackQueueSearchParams searchParams = createDefaultSearchParams();
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);

            given(queryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(readManager.findBySliceCriteria(criteria)).willReturn(feedbackQueues);
            given(criteria.size()).willReturn(20);
            given(assembler.toSliceResult(feedbackQueues, 20)).willReturn(sliceResult);

            // when
            FeedbackQueueSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(queryFactory).should().createSliceCriteria(searchParams);
            then(readManager).should().findBySliceCriteria(criteria);
            then(assembler).should().toSliceResult(feedbackQueues, 20);
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            FeedbackQueueSearchParams searchParams = createDefaultSearchParams();
            List<FeedbackQueue> emptyList = List.of();

            given(queryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(readManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(criteria.size()).willReturn(20);
            given(assembler.toSliceResult(emptyList, 20)).willReturn(sliceResult);

            // when
            FeedbackQueueSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(queryFactory).should().createSliceCriteria(searchParams);
            then(readManager).should().findBySliceCriteria(criteria);
            then(assembler).should().toSliceResult(emptyList, 20);
        }
    }

    private FeedbackQueueSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return FeedbackQueueSearchParams.of(cursorParams, null, null, null, null, null);
    }
}
