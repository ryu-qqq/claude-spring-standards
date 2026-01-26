package com.ryuqq.application.feedbackqueue.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.feedbackqueue.port.out.FeedbackQueueCommandPort;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FeedbackQueuePersistenceManager 단위 테스트
 *
 * <p>FeedbackQueue 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("FeedbackQueuePersistenceManager 단위 테스트")
class FeedbackQueuePersistenceManagerTest {

    @Mock private FeedbackQueueCommandPort feedbackQueueCommandPort;

    @Mock private FeedbackQueue feedbackQueue;

    private FeedbackQueuePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new FeedbackQueuePersistenceManager(feedbackQueueCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - FeedbackQueue 영속화")
        void persist_WithFeedbackQueue_ShouldReturnId() {
            // given
            FeedbackQueueId expectedId = FeedbackQueueId.of(1L);
            given(feedbackQueueCommandPort.persist(feedbackQueue)).willReturn(expectedId);

            // when
            FeedbackQueueId result = sut.persist(feedbackQueue);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(feedbackQueueCommandPort).should().persist(feedbackQueue);
        }
    }
}
