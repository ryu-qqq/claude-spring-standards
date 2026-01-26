package com.ryuqq.application.feedbackqueue.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.feedbackqueue.port.out.FeedbackQueueQueryPort;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackQueueNotFoundException;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FeedbackQueueReadManager 단위 테스트
 *
 * <p>FeedbackQueue 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("FeedbackQueueReadManager 단위 테스트")
class FeedbackQueueReadManagerTest {

    @Mock private FeedbackQueueQueryPort feedbackQueueQueryPort;

    @Mock private FeedbackQueue feedbackQueue;

    @Mock private FeedbackQueueSliceCriteria criteria;

    private FeedbackQueueReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new FeedbackQueueReadManager(feedbackQueueQueryPort);
    }

    @Nested
    @DisplayName("getById(FeedbackQueueId) 메서드")
    class GetByIdWithVo {

        @Test
        @DisplayName("성공 - FeedbackQueueId로 조회")
        void getById_WithValidId_ShouldReturnFeedbackQueue() {
            // given
            FeedbackQueueId id = FeedbackQueueId.of(1L);
            given(feedbackQueueQueryPort.findById(id)).willReturn(Optional.of(feedbackQueue));

            // when
            FeedbackQueue result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(feedbackQueue);
            then(feedbackQueueQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            FeedbackQueueId id = FeedbackQueueId.of(999L);
            given(feedbackQueueQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(FeedbackQueueNotFoundException.class);
            then(feedbackQueueQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("getById(Long) 메서드")
    class GetByIdWithLong {

        @Test
        @DisplayName("성공 - Long ID로 조회")
        void getById_WithValidLongId_ShouldReturnFeedbackQueue() {
            // given
            Long id = 1L;
            given(feedbackQueueQueryPort.findById(id)).willReturn(Optional.of(feedbackQueue));

            // when
            FeedbackQueue result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(feedbackQueue);
            then(feedbackQueueQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Long ID로 예외 발생")
        void getById_WithNonExistentLongId_ShouldThrowException() {
            // given
            Long id = 999L;
            given(feedbackQueueQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(FeedbackQueueNotFoundException.class);
            then(feedbackQueueQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 FeedbackQueue 조회")
        void findById_WithValidId_ShouldReturnFeedbackQueue() {
            // given
            FeedbackQueueId id = FeedbackQueueId.of(1L);
            given(feedbackQueueQueryPort.findById(id)).willReturn(Optional.of(feedbackQueue));

            // when
            FeedbackQueue result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(feedbackQueue);
            then(feedbackQueueQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 null 반환")
        void findById_WithNonExistentId_ShouldReturnNull() {
            // given
            FeedbackQueueId id = FeedbackQueueId.of(999L);
            given(feedbackQueueQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            FeedbackQueue result = sut.findById(id);

            // then
            assertThat(result).isNull();
            then(feedbackQueueQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findByStatus 메서드")
    class FindByStatus {

        @Test
        @DisplayName("성공 - 상태별 FeedbackQueue 목록 조회")
        void findByStatus_WithStatus_ShouldReturnList() {
            // given
            FeedbackStatus status = FeedbackStatus.PENDING;
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);
            given(feedbackQueueQueryPort.findByStatus(status)).willReturn(feedbackQueues);

            // when
            List<FeedbackQueue> result = sut.findByStatus(status);

            // then
            assertThat(result).hasSize(1).containsExactly(feedbackQueue);
            then(feedbackQueueQueryPort).should().findByStatus(status);
        }
    }

    @Nested
    @DisplayName("findPendingFeedbacks 메서드")
    class FindPendingFeedbacks {

        @Test
        @DisplayName("성공 - PENDING 상태 FeedbackQueue 목록 조회")
        void findPendingFeedbacks_ShouldReturnList() {
            // given
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);
            given(feedbackQueueQueryPort.findPendingFeedbacks()).willReturn(feedbackQueues);

            // when
            List<FeedbackQueue> result = sut.findPendingFeedbacks();

            // then
            assertThat(result).hasSize(1).containsExactly(feedbackQueue);
            then(feedbackQueueQueryPort).should().findPendingFeedbacks();
        }
    }

    @Nested
    @DisplayName("findHumanReviewRequiredFeedbacks 메서드")
    class FindHumanReviewRequiredFeedbacks {

        @Test
        @DisplayName("성공 - Human 승인 필요 FeedbackQueue 목록 조회")
        void findHumanReviewRequiredFeedbacks_ShouldReturnList() {
            // given
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);
            given(feedbackQueueQueryPort.findHumanReviewRequiredFeedbacks())
                    .willReturn(feedbackQueues);

            // when
            List<FeedbackQueue> result = sut.findHumanReviewRequiredFeedbacks();

            // then
            assertThat(result).hasSize(1).containsExactly(feedbackQueue);
            then(feedbackQueueQueryPort).should().findHumanReviewRequiredFeedbacks();
        }
    }

    @Nested
    @DisplayName("findAutoMergeableFeedbacks 메서드")
    class FindAutoMergeableFeedbacks {

        @Test
        @DisplayName("성공 - 자동 머지 가능한 FeedbackQueue 목록 조회")
        void findAutoMergeableFeedbacks_ShouldReturnList() {
            // given
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);
            given(feedbackQueueQueryPort.findAutoMergeableFeedbacks()).willReturn(feedbackQueues);

            // when
            List<FeedbackQueue> result = sut.findAutoMergeableFeedbacks();

            // then
            assertThat(result).hasSize(1).containsExactly(feedbackQueue);
            then(feedbackQueueQueryPort).should().findAutoMergeableFeedbacks();
        }
    }

    @Nested
    @DisplayName("findBySlice 메서드")
    class FindBySlice {

        @Test
        @DisplayName("성공 - 커서 기반 슬라이스 조회")
        void findBySlice_WithParams_ShouldReturnList() {
            // given
            FeedbackStatus status = FeedbackStatus.PENDING;
            Long cursorId = 10L;
            int fetchSize = 20;
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);
            given(feedbackQueueQueryPort.findBySlice(status, cursorId, fetchSize))
                    .willReturn(feedbackQueues);

            // when
            List<FeedbackQueue> result = sut.findBySlice(status, cursorId, fetchSize);

            // then
            assertThat(result).hasSize(1).containsExactly(feedbackQueue);
            then(feedbackQueueQueryPort).should().findBySlice(status, cursorId, fetchSize);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - SliceCriteria 기반 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);
            given(feedbackQueueQueryPort.findBySliceCriteria(criteria)).willReturn(feedbackQueues);

            // when
            List<FeedbackQueue> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(feedbackQueue);
            then(feedbackQueueQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findByTargetType 메서드")
    class FindByTargetType {

        @Test
        @DisplayName("성공 - 대상 타입별 FeedbackQueue 목록 조회")
        void findByTargetType_WithTargetType_ShouldReturnList() {
            // given
            FeedbackTargetType targetType = FeedbackTargetType.CODING_RULE;
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);
            given(feedbackQueueQueryPort.findByTargetType(targetType)).willReturn(feedbackQueues);

            // when
            List<FeedbackQueue> result = sut.findByTargetType(targetType);

            // then
            assertThat(result).hasSize(1).containsExactly(feedbackQueue);
            then(feedbackQueueQueryPort).should().findByTargetType(targetType);
        }
    }

    @Nested
    @DisplayName("findByTarget 메서드")
    class FindByTarget {

        @Test
        @DisplayName("성공 - 특정 대상에 대한 FeedbackQueue 목록 조회")
        void findByTarget_WithTargetTypeAndId_ShouldReturnList() {
            // given
            FeedbackTargetType targetType = FeedbackTargetType.CODING_RULE;
            Long targetId = 1L;
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue);
            given(feedbackQueueQueryPort.findByTarget(targetType, targetId))
                    .willReturn(feedbackQueues);

            // when
            List<FeedbackQueue> result = sut.findByTarget(targetType, targetId);

            // then
            assertThat(result).hasSize(1).containsExactly(feedbackQueue);
            then(feedbackQueueQueryPort).should().findByTarget(targetType, targetId);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID 확인")
        void existsById_WhenExists_ShouldReturnTrue() {
            // given
            FeedbackQueueId id = FeedbackQueueId.of(1L);
            given(feedbackQueueQueryPort.findById(id)).willReturn(Optional.of(feedbackQueue));

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(feedbackQueueQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 확인")
        void existsById_WhenNotExists_ShouldReturnFalse() {
            // given
            FeedbackQueueId id = FeedbackQueueId.of(999L);
            given(feedbackQueueQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
            then(feedbackQueueQueryPort).should().findById(id);
        }
    }
}
