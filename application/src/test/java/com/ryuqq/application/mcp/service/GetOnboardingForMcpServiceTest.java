package com.ryuqq.application.mcp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.mcp.assembler.OnboardingResultAssembler;
import com.ryuqq.application.mcp.dto.query.GetOnboardingQuery;
import com.ryuqq.application.mcp.dto.response.OnboardingContextsResult;
import com.ryuqq.application.onboardingcontext.manager.OnboardingContextReadManager;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.techstack.id.TechStackId;
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
 * GetOnboardingForMcpService 단위 테스트
 *
 * <p>MCP get_onboarding_context Tool용 Onboarding 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("GetOnboardingForMcpService 단위 테스트")
class GetOnboardingForMcpServiceTest {

    @Mock private OnboardingContextReadManager onboardingContextReadManager;

    @Mock private OnboardingResultAssembler onboardingResultAssembler;

    @Mock private OnboardingContext onboardingContext;

    @Mock private OnboardingContextsResult onboardingContextsResult;

    private GetOnboardingForMcpService sut;

    @BeforeEach
    void setUp() {
        sut =
                new GetOnboardingForMcpService(
                        onboardingContextReadManager, onboardingResultAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Query로 Onboarding Context 조회")
        void execute_WithValidQuery_ShouldReturnResult() {
            // given
            GetOnboardingQuery query =
                    new GetOnboardingQuery(1L, 1L, List.of("SUMMARY", "ZERO_TOLERANCE"));
            List<OnboardingContext> contexts = List.of(onboardingContext);
            List<ContextType> contextTypes =
                    List.of(ContextType.SUMMARY, ContextType.ZERO_TOLERANCE);

            given(onboardingContextReadManager.findForMcp(TechStackId.of(1L), 1L, contextTypes))
                    .willReturn(contexts);
            given(onboardingResultAssembler.toOnboardingContextsResult(contexts))
                    .willReturn(onboardingContextsResult);

            // when
            OnboardingContextsResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(onboardingContextsResult);

            then(onboardingContextReadManager)
                    .should()
                    .findForMcp(TechStackId.of(1L), 1L, contextTypes);
            then(onboardingResultAssembler).should().toOnboardingContextsResult(contexts);
        }

        @Test
        @DisplayName("성공 - contextTypes가 null인 경우")
        void execute_WithNullContextTypes_ShouldReturnResult() {
            // given
            GetOnboardingQuery query = new GetOnboardingQuery(1L, 1L, null);
            List<OnboardingContext> contexts = List.of(onboardingContext);

            given(onboardingContextReadManager.findForMcp(TechStackId.of(1L), 1L, null))
                    .willReturn(contexts);
            given(onboardingResultAssembler.toOnboardingContextsResult(contexts))
                    .willReturn(onboardingContextsResult);

            // when
            OnboardingContextsResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(onboardingContextsResult);

            then(onboardingContextReadManager).should().findForMcp(TechStackId.of(1L), 1L, null);
            then(onboardingResultAssembler).should().toOnboardingContextsResult(contexts);
        }

        @Test
        @DisplayName("성공 - contextTypes가 빈 목록인 경우")
        void execute_WithEmptyContextTypes_ShouldReturnResult() {
            // given
            GetOnboardingQuery query = new GetOnboardingQuery(1L, null, List.of());
            List<OnboardingContext> contexts = List.of();

            given(onboardingContextReadManager.findForMcp(TechStackId.of(1L), null, null))
                    .willReturn(contexts);
            given(onboardingResultAssembler.toOnboardingContextsResult(contexts))
                    .willReturn(onboardingContextsResult);

            // when
            OnboardingContextsResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(onboardingContextsResult);

            then(onboardingContextReadManager).should().findForMcp(TechStackId.of(1L), null, null);
            then(onboardingResultAssembler).should().toOnboardingContextsResult(contexts);
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptyResult() {
            // given
            GetOnboardingQuery query = new GetOnboardingQuery(1L, 1L, List.of("SUMMARY"));
            List<OnboardingContext> emptyList = List.of();
            List<ContextType> contextTypes = List.of(ContextType.SUMMARY);

            given(onboardingContextReadManager.findForMcp(TechStackId.of(1L), 1L, contextTypes))
                    .willReturn(emptyList);
            given(onboardingResultAssembler.toOnboardingContextsResult(emptyList))
                    .willReturn(onboardingContextsResult);

            // when
            OnboardingContextsResult result = sut.execute(query);

            // then
            assertThat(result).isEqualTo(onboardingContextsResult);

            then(onboardingContextReadManager)
                    .should()
                    .findForMcp(TechStackId.of(1L), 1L, contextTypes);
            then(onboardingResultAssembler).should().toOnboardingContextsResult(emptyList);
        }
    }
}
