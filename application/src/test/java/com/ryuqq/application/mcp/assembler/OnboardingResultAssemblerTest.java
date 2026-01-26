package com.ryuqq.application.mcp.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.mcp.dto.response.OnboardingContextsResult;
import com.ryuqq.application.mcp.dto.response.OnboardingResult;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.fixture.OnboardingContextFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OnboardingResultAssembler 단위 테스트
 *
 * <p>OnboardingContext → MCP Result DTO 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("OnboardingResultAssembler 단위 테스트")
class OnboardingResultAssemblerTest {

    private OnboardingResultAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingResultAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - OnboardingContext를 OnboardingResult로 변환")
        void toResult_WithValidContext_ShouldReturnResult() {
            // given
            OnboardingContext context = OnboardingContextFixture.defaultExistingOnboardingContext();

            // when
            OnboardingResult result = sut.toResult(context);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(context.idValue());
            assertThat(result.contextType()).isEqualTo(context.contextTypeName());
            assertThat(result.title()).isEqualTo(context.titleValue());
            assertThat(result.content()).isEqualTo(context.contentValue());
            assertThat(result.priority()).isEqualTo(context.priorityValue());
        }

        @Test
        @DisplayName("성공 - ZERO_TOLERANCE 타입 OnboardingContext 변환")
        void toResult_WithZeroToleranceContext_ShouldReturnResult() {
            // given
            OnboardingContext context = OnboardingContextFixture.zeroToleranceOnboardingContext();

            // when
            OnboardingResult result = sut.toResult(context);

            // then
            assertThat(result).isNotNull();
            assertThat(result.contextType()).isEqualTo("ZERO_TOLERANCE");
            assertThat(result.title()).isEqualTo("Zero-Tolerance 규칙");
        }

        @Test
        @DisplayName("성공 - MCP_USAGE 타입 OnboardingContext 변환")
        void toResult_WithMcpUsageContext_ShouldReturnResult() {
            // given
            OnboardingContext context = OnboardingContextFixture.mcpUsageOnboardingContext();

            // when
            OnboardingResult result = sut.toResult(context);

            // then
            assertThat(result).isNotNull();
            assertThat(result.contextType()).isEqualTo("MCP_USAGE");
            assertThat(result.title()).isEqualTo("MCP 사용법");
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - OnboardingContext 목록을 OnboardingResult 목록으로 변환")
        void toResults_WithValidContexts_ShouldReturnResults() {
            // given
            OnboardingContext context1 =
                    OnboardingContextFixture.defaultExistingOnboardingContext();
            OnboardingContext context2 = OnboardingContextFixture.zeroToleranceOnboardingContext();
            List<OnboardingContext> contexts = List.of(context1, context2);

            // when
            List<OnboardingResult> results = sut.toResults(contexts);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).contextType()).isEqualTo("SUMMARY");
            assertThat(results.get(1).contextType()).isEqualTo("ZERO_TOLERANCE");
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<OnboardingContext> contexts = List.of();

            // when
            List<OnboardingResult> results = sut.toResults(contexts);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toOnboardingContextsResult 메서드")
    class ToOnboardingContextsResult {

        @Test
        @DisplayName("성공 - OnboardingContext 목록을 OnboardingContextsResult로 변환")
        void toOnboardingContextsResult_WithValidContexts_ShouldReturnResult() {
            // given
            OnboardingContext context1 =
                    OnboardingContextFixture.defaultExistingOnboardingContext();
            OnboardingContext context2 = OnboardingContextFixture.zeroToleranceOnboardingContext();
            OnboardingContext context3 = OnboardingContextFixture.rulesIndexOnboardingContext();
            List<OnboardingContext> contexts = List.of(context1, context2, context3);

            // when
            OnboardingContextsResult result = sut.toOnboardingContextsResult(contexts);

            // then
            assertThat(result).isNotNull();
            assertThat(result.contexts()).hasSize(3);
            assertThat(result.totalCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toOnboardingContextsResult_WithEmptyList_ShouldReturnEmptyResult() {
            // given
            List<OnboardingContext> contexts = List.of();

            // when
            OnboardingContextsResult result = sut.toOnboardingContextsResult(contexts);

            // then
            assertThat(result).isNotNull();
            assertThat(result.contexts()).isEmpty();
            assertThat(result.totalCount()).isZero();
        }
    }
}
