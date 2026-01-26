package com.ryuqq.application.onboardingcontext.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextResult;
import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextSliceResult;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.fixture.OnboardingContextFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OnboardingContextAssembler 단위 테스트
 *
 * <p>Domain → Response DTO 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("OnboardingContextAssembler 단위 테스트")
class OnboardingContextAssemblerTest {

    private OnboardingContextAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingContextAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - OnboardingContext를 OnboardingContextResult로 변환")
        void toResult_WithOnboardingContext_ShouldReturnResult() {
            // given
            OnboardingContext onboardingContext =
                    OnboardingContextFixture.defaultExistingOnboardingContext();

            // when
            OnboardingContextResult result = sut.toResult(onboardingContext);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(onboardingContext.idValue());
            assertThat(result.techStackId()).isEqualTo(onboardingContext.techStackIdValue());
            assertThat(result.contextType()).isEqualTo(onboardingContext.contextTypeName());
            assertThat(result.title()).isEqualTo(onboardingContext.titleValue());
            assertThat(result.content()).isEqualTo(onboardingContext.contentValue());
            assertThat(result.priority()).isEqualTo(onboardingContext.priorityValue());
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - OnboardingContext 목록을 Result 목록으로 변환")
        void toResults_WithOnboardingContexts_ShouldReturnResults() {
            // given
            OnboardingContext onboardingContext1 =
                    OnboardingContextFixture.defaultExistingOnboardingContext();
            OnboardingContext onboardingContext2 =
                    OnboardingContextFixture.zeroToleranceOnboardingContext();
            List<OnboardingContext> onboardingContexts =
                    List.of(onboardingContext1, onboardingContext2);

            // when
            List<OnboardingContextResult> results = sut.toResults(onboardingContexts);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).id()).isEqualTo(onboardingContext1.idValue());
            assertThat(results.get(1).id()).isEqualTo(onboardingContext2.idValue());
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoMoreData_ShouldReturnSliceWithoutNext() {
            // given
            List<OnboardingContext> onboardingContexts =
                    List.of(OnboardingContextFixture.defaultExistingOnboardingContext());
            int size = 20;

            // when
            OnboardingContextSliceResult result = sut.toSliceResult(onboardingContexts, size);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.sliceMeta().hasNext()).isFalse();
        }

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenMoreDataExists_ShouldReturnSliceWithNext() {
            // given
            List<OnboardingContext> onboardingContexts =
                    List.of(
                            OnboardingContextFixture.defaultExistingOnboardingContext(),
                            OnboardingContextFixture.zeroToleranceOnboardingContext(),
                            OnboardingContextFixture.rulesIndexOnboardingContext());
            int size = 2;

            // when
            OnboardingContextSliceResult result = sut.toSliceResult(onboardingContexts, size);

            // then
            assertThat(result.content()).hasSize(2);
            assertThat(result.sliceMeta().hasNext()).isTrue();
        }
    }
}
