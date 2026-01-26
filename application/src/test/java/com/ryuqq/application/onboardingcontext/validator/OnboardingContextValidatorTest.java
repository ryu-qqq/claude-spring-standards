package com.ryuqq.application.onboardingcontext.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.onboardingcontext.manager.OnboardingContextReadManager;
import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.exception.OnboardingContextNotFoundException;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.id.TechStackId;
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
 * OnboardingContextValidator 단위 테스트
 *
 * <p>OnboardingContext 검증기의 검증 로직을 테스트합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("OnboardingContextValidator 단위 테스트")
class OnboardingContextValidatorTest {

    @Mock private OnboardingContextReadManager onboardingContextReadManager;

    @Mock private TechStackReadManager techStackReadManager;

    @Mock private OnboardingContext onboardingContext;

    private OnboardingContextValidator sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingContextValidator(onboardingContextReadManager, techStackReadManager);
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공 - 존재하는 ID인 경우")
        void validateExists_WhenExists_ShouldNotThrow() {
            // given
            OnboardingContextId id = OnboardingContextId.of(1L);
            given(onboardingContextReadManager.existsById(id)).willReturn(true);

            // when & then
            sut.validateExists(id);

            then(onboardingContextReadManager).should().existsById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID인 경우")
        void validateExists_WhenNotExists_ShouldThrowException() {
            // given
            OnboardingContextId id = OnboardingContextId.of(999L);
            given(onboardingContextReadManager.existsById(id)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateExists(id))
                    .isInstanceOf(OnboardingContextNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 OnboardingContext 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnOnboardingContext() {
            // given
            OnboardingContextId id = OnboardingContextId.of(1L);
            given(onboardingContextReadManager.findById(id))
                    .willReturn(Optional.of(onboardingContext));

            // when
            OnboardingContext result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(onboardingContext);
            then(onboardingContextReadManager).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외 발생")
        void findExistingOrThrow_WhenNotExists_ShouldThrowException() {
            // given
            OnboardingContextId id = OnboardingContextId.of(999L);
            given(onboardingContextReadManager.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(OnboardingContextNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateTechStackExists 메서드")
    class ValidateTechStackExists {

        @Test
        @DisplayName("성공 - TechStack이 존재하는 경우")
        void validateTechStackExists_WhenExists_ShouldNotThrow() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            given(techStackReadManager.existsById(techStackId)).willReturn(true);

            // when & then
            sut.validateTechStackExists(techStackId);

            then(techStackReadManager).should().existsById(techStackId);
        }

        @Test
        @DisplayName("실패 - TechStack이 존재하지 않는 경우")
        void validateTechStackExists_WhenNotExists_ShouldThrowException() {
            // given
            TechStackId techStackId = TechStackId.of(999L);
            given(techStackReadManager.existsById(techStackId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateTechStackExists(techStackId))
                    .isInstanceOf(TechStackNotFoundException.class);
        }
    }
}
