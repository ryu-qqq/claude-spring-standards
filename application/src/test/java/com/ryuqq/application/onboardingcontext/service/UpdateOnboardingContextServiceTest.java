package com.ryuqq.application.onboardingcontext.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.onboardingcontext.dto.command.UpdateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.factory.command.OnboardingContextCommandFactory;
import com.ryuqq.application.onboardingcontext.manager.OnboardingContextPersistenceManager;
import com.ryuqq.application.onboardingcontext.validator.OnboardingContextValidator;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContextUpdateData;
import com.ryuqq.domain.onboardingcontext.exception.OnboardingContextNotFoundException;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
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
 * UpdateOnboardingContextService 단위 테스트
 *
 * <p>OnboardingContext 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateOnboardingContextService 단위 테스트")
class UpdateOnboardingContextServiceTest {

    @Mock private OnboardingContextValidator onboardingContextValidator;

    @Mock private OnboardingContextCommandFactory onboardingContextCommandFactory;

    @Mock private OnboardingContextPersistenceManager onboardingContextPersistenceManager;

    @Mock private OnboardingContext onboardingContext;

    @Mock private OnboardingContextUpdateData updateData;

    private UpdateOnboardingContextService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateOnboardingContextService(
                        onboardingContextValidator,
                        onboardingContextCommandFactory,
                        onboardingContextPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 OnboardingContext 수정")
        void execute_WithValidCommand_ShouldUpdateOnboardingContext() {
            // given
            UpdateOnboardingContextCommand command = createDefaultCommand();
            OnboardingContextId id = OnboardingContextId.of(command.id());
            Instant now = Instant.now();
            UpdateContext<OnboardingContextId, OnboardingContextUpdateData> context =
                    new UpdateContext<>(id, updateData, now);

            given(onboardingContextCommandFactory.createUpdateContext(command)).willReturn(context);
            given(onboardingContextValidator.findExistingOrThrow(id)).willReturn(onboardingContext);
            given(onboardingContextPersistenceManager.persist(onboardingContext)).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(onboardingContextCommandFactory).should().createUpdateContext(command);
            then(onboardingContextValidator).should().findExistingOrThrow(id);
            then(onboardingContext).should().update(updateData, now);
            then(onboardingContextPersistenceManager).should().persist(onboardingContext);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 OnboardingContext 수정 시도")
        void execute_WhenOnboardingContextNotExists_ShouldThrowException() {
            // given
            UpdateOnboardingContextCommand command = createDefaultCommand();
            OnboardingContextId id = OnboardingContextId.of(command.id());
            UpdateContext<OnboardingContextId, OnboardingContextUpdateData> context =
                    new UpdateContext<>(id, updateData, Instant.now());

            given(onboardingContextCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new OnboardingContextNotFoundException(id.value()))
                    .given(onboardingContextValidator)
                    .findExistingOrThrow(id);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(OnboardingContextNotFoundException.class);

            then(onboardingContextPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateOnboardingContextCommand createDefaultCommand() {
        return new UpdateOnboardingContextCommand(
                1L, "SUMMARY", "프로젝트 개요 (수정됨)", "# Updated Spring Boot 3.5.x + Java 21 프로젝트...", 0);
    }
}
