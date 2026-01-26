package com.ryuqq.application.feedbackqueue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.feedbackqueue.assembler.FeedbackQueueAssembler;
import com.ryuqq.application.feedbackqueue.dto.query.GetPendingFeedbacksQuery;
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
 * GetPendingFeedbacksService 단위 테스트
 *
 * <p>PENDING 피드백 목록 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("GetPendingFeedbacksService 단위 테스트")
class GetPendingFeedbacksServiceTest {

    @Mock private FeedbackQueueQueryFactory feedbackQueueQueryFactory;

    @Mock private FeedbackQueueReadManager feedbackQueueReadManager;

    @Mock private FeedbackQueueAssembler feedbackQueueAssembler;

    @Mock private FeedbackQueueSliceCriteria criteria;

    @Mock private FeedbackQueue feedbackQueue;

    @Mock private FeedbackQueueSliceResult sliceResult;

    private GetPendingFeedbacksService sut;

    @BeforeEach
    void setUp() {
        sut =
                new GetPendingFeedbacksService(
                        feedbackQueueQueryFactory,
                        feedbackQueueReadManager,
                        feedbackQueueAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Query로 PENDING 피드백 목록 조회")
        void execute_WithValidQuery_ShouldReturnSliceResult() {
            // given
            GetPendingFeedbacksQuery query = GetPendingFeedbacksQuery.firstPage(20);
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);

            given(feedbackQueueQueryFactory.toSliceCriteria(query)).willReturn(criteria);
            given(feedbackQueueReadManager.findBySliceCriteria(criteria))
                    .willReturn(feedbackQueues);
            given(feedbackQueueAssembler.toSliceResult(feedbackQueues, query.size()))
                    .willReturn(sliceResult);

            // when
            FeedbackQueueSliceResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(feedbackQueueQueryFactory).should().toSliceCriteria(query);
            then(feedbackQueueReadManager).should().findBySliceCriteria(criteria);
            then(feedbackQueueAssembler).should().toSliceResult(feedbackQueues, query.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            GetPendingFeedbacksQuery query = GetPendingFeedbacksQuery.firstPage(20);
            List<FeedbackQueue> emptyList = List.of();

            given(feedbackQueueQueryFactory.toSliceCriteria(query)).willReturn(criteria);
            given(feedbackQueueReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(feedbackQueueAssembler.toSliceResult(emptyList, query.size()))
                    .willReturn(sliceResult);

            // when
            FeedbackQueueSliceResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(feedbackQueueQueryFactory).should().toSliceCriteria(query);
            then(feedbackQueueReadManager).should().findBySliceCriteria(criteria);
            then(feedbackQueueAssembler).should().toSliceResult(emptyList, query.size());
        }
    }
}
