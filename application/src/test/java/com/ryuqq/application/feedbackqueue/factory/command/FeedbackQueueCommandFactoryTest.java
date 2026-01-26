package com.ryuqq.application.feedbackqueue.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.fixture.CreateFeedbackCommandFixture;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FeedbackQueueCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("FeedbackQueueCommandFactory 단위 테스트")
class FeedbackQueueCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private FeedbackQueueCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new FeedbackQueueCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateFeedbackCommand로 FeedbackQueue 생성")
        void create_WithValidCommand_ShouldReturnFeedbackQueue() {
            // given
            CreateFeedbackCommand command = CreateFeedbackCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            FeedbackQueue result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.targetType().name()).isEqualTo(command.targetType());
            assertThat(result.feedbackType().name()).isEqualTo(command.feedbackType());
            assertThat(result.payload().value()).isEqualTo(command.payload());
        }

        @Test
        @DisplayName("성공 - RULE_EXAMPLE ADD 피드백 생성")
        void create_WithRuleExampleAddCommand_ShouldReturnFeedbackQueue() {
            // given
            CreateFeedbackCommand command = CreateFeedbackCommandFixture.ruleExampleAddCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            FeedbackQueue result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.targetType().name()).isEqualTo("RULE_EXAMPLE");
            assertThat(result.feedbackType().name()).isEqualTo("ADD");
        }

        @Test
        @DisplayName("성공 - CLASS_TEMPLATE ADD 피드백 생성")
        void create_WithClassTemplateAddCommand_ShouldReturnFeedbackQueue() {
            // given
            CreateFeedbackCommand command = CreateFeedbackCommandFixture.classTemplateAddCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            FeedbackQueue result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.targetType().name()).isEqualTo("CLASS_TEMPLATE");
        }
    }

    @Nested
    @DisplayName("create with RiskLevel 메서드")
    class CreateWithRiskLevel {

        @Test
        @DisplayName("성공 - RiskLevel을 명시하여 FeedbackQueue 생성")
        void create_WithRiskLevel_ShouldReturnFeedbackQueueWithRiskLevel() {
            // given
            CreateFeedbackCommand command = CreateFeedbackCommandFixture.defaultCommand();
            RiskLevel riskLevel = RiskLevel.HIGH;
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            FeedbackQueue result = sut.create(command, riskLevel);

            // then
            assertThat(result).isNotNull();
            assertThat(result.riskLevel()).isEqualTo(RiskLevel.HIGH);
        }

        @Test
        @DisplayName("성공 - SAFE RiskLevel로 FeedbackQueue 생성")
        void create_WithSafeRiskLevel_ShouldReturnFeedbackQueueWithSafeRisk() {
            // given
            CreateFeedbackCommand command = CreateFeedbackCommandFixture.codingRuleAddCommand();
            RiskLevel riskLevel = RiskLevel.SAFE;
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            FeedbackQueue result = sut.create(command, riskLevel);

            // then
            assertThat(result).isNotNull();
            assertThat(result.riskLevel()).isEqualTo(RiskLevel.SAFE);
        }
    }

    @Nested
    @DisplayName("now 메서드")
    class Now {

        @Test
        @DisplayName("성공 - 현재 시간 반환")
        void now_ShouldReturnCurrentTime() {
            // given
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Instant result = sut.now();

            // then
            assertThat(result).isEqualTo(FIXED_TIME);
        }
    }
}
