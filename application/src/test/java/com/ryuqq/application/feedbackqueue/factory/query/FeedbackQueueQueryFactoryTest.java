package com.ryuqq.application.feedbackqueue.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.query.GetAwaitingHumanReviewQuery;
import com.ryuqq.application.feedbackqueue.dto.query.GetPendingFeedbacksQuery;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * FeedbackQueueQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("FeedbackQueueQueryFactory 단위 테스트")
class FeedbackQueueQueryFactoryTest {

    private FeedbackQueueQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new FeedbackQueueQueryFactory();
    }

    @Nested
    @DisplayName("toSliceCriteria - GetPendingFeedbacksQuery")
    class ToSliceCriteriaFromPendingQuery {

        @Test
        @DisplayName("성공 - GetPendingFeedbacksQuery를 SliceCriteria로 변환")
        void toSliceCriteria_WithPendingQuery_ShouldReturnCriteria() {
            // given
            GetPendingFeedbacksQuery query = new GetPendingFeedbacksQuery(null, null, 20);

            // when
            FeedbackQueueSliceCriteria result = sut.toSliceCriteria(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).containsExactly(FeedbackStatus.PENDING);
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("성공 - 타겟 타입 필터 포함 Criteria 생성")
        void toSliceCriteria_WithTargetTypeFilter_ShouldReturnCriteriaWithTargetType() {
            // given
            GetPendingFeedbacksQuery query = new GetPendingFeedbacksQuery("RULE_EXAMPLE", null, 20);

            // when
            FeedbackQueueSliceCriteria result = sut.toSliceCriteria(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.targetTypes()).containsExactly(FeedbackTargetType.RULE_EXAMPLE);
        }

        @Test
        @DisplayName("성공 - 커서 포함 Criteria 생성")
        void toSliceCriteria_WithCursor_ShouldReturnCriteriaWithCursor() {
            // given
            GetPendingFeedbacksQuery query = new GetPendingFeedbacksQuery(null, 100L, 20);

            // when
            FeedbackQueueSliceCriteria result = sut.toSliceCriteria(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("toSliceCriteria - GetAwaitingHumanReviewQuery")
    class ToSliceCriteriaFromAwaitingQuery {

        @Test
        @DisplayName("성공 - GetAwaitingHumanReviewQuery를 SliceCriteria로 변환")
        void toSliceCriteria_WithAwaitingQuery_ShouldReturnCriteria() {
            // given
            GetAwaitingHumanReviewQuery query = new GetAwaitingHumanReviewQuery(null, null, 20);

            // when
            FeedbackQueueSliceCriteria result = sut.toSliceCriteria(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).containsExactly(FeedbackStatus.LLM_APPROVED);
            assertThat(result.riskLevels()).containsExactly(RiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("성공 - 타겟 타입 필터 포함 Criteria 생성")
        void toSliceCriteria_WithTargetTypeFilter_ShouldReturnCriteriaWithTargetType() {
            // given
            GetAwaitingHumanReviewQuery query =
                    new GetAwaitingHumanReviewQuery("CLASS_TEMPLATE", null, 20);

            // when
            FeedbackQueueSliceCriteria result = sut.toSliceCriteria(query);

            // then
            assertThat(result).isNotNull();
            assertThat(result.targetTypes()).containsExactly(FeedbackTargetType.CLASS_TEMPLATE);
        }
    }

    @Nested
    @DisplayName("createSliceCriteria - FeedbackQueueSearchParams")
    class CreateSliceCriteriaFromSearchParams {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            FeedbackQueueSearchParams searchParams =
                    FeedbackQueueSearchParams.of(cursorParams, null, null, null, null, null);

            // when
            FeedbackQueueSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("성공 - 커서 기반 페이징 Criteria 생성")
        void createSliceCriteria_WithCursor_ShouldReturnCriteriaWithCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("100", 20);
            FeedbackQueueSearchParams searchParams =
                    FeedbackQueueSearchParams.of(cursorParams, null, null, null, null, null);

            // when
            FeedbackQueueSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - 상태 필터 포함 Criteria 생성")
        void createSliceCriteria_WithStatuses_ShouldReturnCriteriaWithStatuses() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            FeedbackQueueSearchParams searchParams =
                    FeedbackQueueSearchParams.of(
                            cursorParams,
                            List.of("PENDING", "LLM_APPROVED"),
                            null,
                            null,
                            null,
                            null);

            // when
            FeedbackQueueSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses())
                    .containsExactlyInAnyOrder(FeedbackStatus.PENDING, FeedbackStatus.LLM_APPROVED);
        }

        @Test
        @DisplayName("성공 - 리스크 레벨 필터 포함 Criteria 생성")
        void createSliceCriteria_WithRiskLevels_ShouldReturnCriteriaWithRiskLevels() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            FeedbackQueueSearchParams searchParams =
                    FeedbackQueueSearchParams.of(
                            cursorParams, null, null, null, List.of("SAFE", "MEDIUM"), null);

            // when
            FeedbackQueueSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.riskLevels())
                    .containsExactlyInAnyOrder(RiskLevel.SAFE, RiskLevel.MEDIUM);
        }
    }

    @Nested
    @DisplayName("toFeedbackQueueId 메서드")
    class ToFeedbackQueueId {

        @Test
        @DisplayName("성공 - Long을 FeedbackQueueId로 변환")
        void toFeedbackQueueId_WithValidId_ShouldReturnFeedbackQueueId() {
            // given
            Long feedbackId = 1L;

            // when
            FeedbackQueueId result = sut.toFeedbackQueueId(feedbackId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(feedbackId);
        }
    }

    @Nested
    @DisplayName("toTargetType 메서드")
    class ToTargetType {

        @Test
        @DisplayName("성공 - String을 FeedbackTargetType으로 변환")
        void toTargetType_WithValidString_ShouldReturnTargetType() {
            // given
            String targetType = "RULE_EXAMPLE";

            // when
            FeedbackTargetType result = sut.toTargetType(targetType);

            // then
            assertThat(result).isEqualTo(FeedbackTargetType.RULE_EXAMPLE);
        }

        @Test
        @DisplayName("성공 - null 입력 시 null 반환")
        void toTargetType_WithNull_ShouldReturnNull() {
            // when
            FeedbackTargetType result = sut.toTargetType(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("성공 - 빈 문자열 입력 시 null 반환")
        void toTargetType_WithBlank_ShouldReturnNull() {
            // when
            FeedbackTargetType result = sut.toTargetType("  ");

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("toStatus 메서드")
    class ToStatus {

        @Test
        @DisplayName("성공 - String을 FeedbackStatus로 변환")
        void toStatus_WithValidString_ShouldReturnStatus() {
            // given
            String status = "PENDING";

            // when
            FeedbackStatus result = sut.toStatus(status);

            // then
            assertThat(result).isEqualTo(FeedbackStatus.PENDING);
        }

        @Test
        @DisplayName("성공 - null 입력 시 null 반환")
        void toStatus_WithNull_ShouldReturnNull() {
            // when
            FeedbackStatus result = sut.toStatus(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("toRiskLevel 메서드")
    class ToRiskLevel {

        @Test
        @DisplayName("성공 - String을 RiskLevel로 변환")
        void toRiskLevel_WithValidString_ShouldReturnRiskLevel() {
            // given
            String riskLevel = "HIGH";

            // when
            RiskLevel result = sut.toRiskLevel(riskLevel);

            // then
            assertThat(result).isEqualTo(RiskLevel.HIGH);
        }

        @Test
        @DisplayName("성공 - null 입력 시 null 반환")
        void toRiskLevel_WithNull_ShouldReturnNull() {
            // when
            RiskLevel result = sut.toRiskLevel(null);

            // then
            assertThat(result).isNull();
        }
    }
}
