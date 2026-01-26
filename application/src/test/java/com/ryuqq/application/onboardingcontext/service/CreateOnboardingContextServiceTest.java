package com.ryuqq.application.onboardingcontext.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.onboardingcontext.dto.command.CreateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.factory.command.OnboardingContextCommandFactory;
import com.ryuqq.application.onboardingcontext.manager.OnboardingContextPersistenceManager;
import com.ryuqq.application.onboardingcontext.validator.OnboardingContextValidator;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateOnboardingContextService 단위 테스트
 *
 * <p>OnboardingContext 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateOnboardingContextService 단위 테스트")
class CreateOnboardingContextServiceTest {

    @Mock private OnboardingContextValidator onboardingContextValidator;

    @Mock private OnboardingContextCommandFactory onboardingContextCommandFactory;

    @Mock private OnboardingContextPersistenceManager onboardingContextPersistenceManager;

    @Mock private OnboardingContext onboardingContext;

    private CreateOnboardingContextService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateOnboardingContextService(
                        onboardingContextValidator,
                        onboardingContextCommandFactory,
                        onboardingContextPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 OnboardingContext 생성")
        void execute_WithValidCommand_ShouldCreateOnboardingContext() {
            // given
            CreateOnboardingContextCommand command = createDefaultCommand();
            TechStackId techStackId = TechStackId.of(command.techStackId());
            Long savedId = 1L;

            willDoNothing().given(onboardingContextValidator).validateTechStackExists(techStackId);
            given(onboardingContextCommandFactory.create(command)).willReturn(onboardingContext);
            given(onboardingContextPersistenceManager.persist(onboardingContext))
                    .willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId);

            then(onboardingContextValidator).should().validateTechStackExists(techStackId);
            then(onboardingContextCommandFactory).should().create(command);
            then(onboardingContextPersistenceManager).should().persist(onboardingContext);
        }

        @Test
        @DisplayName("실패 - TechStack이 존재하지 않는 경우")
        void execute_WhenTechStackNotExists_ShouldThrowException() {
            // given
            CreateOnboardingContextCommand command = createDefaultCommand();
            TechStackId techStackId = TechStackId.of(command.techStackId());

            willThrow(new TechStackNotFoundException(techStackId.value()))
                    .given(onboardingContextValidator)
                    .validateTechStackExists(techStackId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TechStackNotFoundException.class);

            then(onboardingContextCommandFactory).shouldHaveNoInteractions();
            then(onboardingContextPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateOnboardingContextCommand createDefaultCommand() {
        return new CreateOnboardingContextCommand(
                1L, 1L, "SUMMARY", "프로젝트 개요", "# Spring Boot 3.5.x + Java 21 프로젝트...", 0);
    }
}
