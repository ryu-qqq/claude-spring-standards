package com.ryuqq.application.onboardingcontext.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.onboardingcontext.dto.command.CreateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.dto.command.UpdateOnboardingContextCommand;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContextUpdateData;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
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
 * OnboardingContextCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("OnboardingContextCommandFactory 단위 테스트")
class OnboardingContextCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private OnboardingContextCommandFactory sut;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new OnboardingContextCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateCommand로 OnboardingContext 생성")
        void create_WithValidCommand_ShouldReturnOnboardingContext() {
            // given
            CreateOnboardingContextCommand command = createDefaultCommand();
            given(timeProvider.now()).willReturn(NOW);

            // when
            OnboardingContext result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.techStackIdValue()).isEqualTo(command.techStackId());
            assertThat(result.contextTypeName()).isEqualTo(command.contextType());
            assertThat(result.titleValue()).isEqualTo(command.title());
            assertThat(result.contentValue()).isEqualTo(command.content());
            assertThat(result.priorityValue()).isEqualTo(command.priority());
        }
    }

    @Nested
    @DisplayName("createUpdateData 메서드")
    class CreateUpdateData {

        @Test
        @DisplayName("성공 - UpdateCommand로 UpdateData 생성")
        void createUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateOnboardingContextCommand command = createUpdateCommand();

            // when
            OnboardingContextUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.contextType()).isEqualTo(ContextType.SUMMARY);
            assertThat(result.title().value()).isEqualTo(command.title());
            assertThat(result.content().value()).isEqualTo(command.content());
            assertThat(result.priority().value()).isEqualTo(command.priority());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateOnboardingContextCommand command = createUpdateCommand();
            given(timeProvider.now()).willReturn(NOW);

            // when
            UpdateContext<OnboardingContextId, OnboardingContextUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.id());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(NOW);
        }
    }

    private CreateOnboardingContextCommand createDefaultCommand() {
        return new CreateOnboardingContextCommand(
                1L, 1L, "SUMMARY", "프로젝트 개요", "# Spring Boot 3.5.x + Java 21 프로젝트...", 0);
    }

    private UpdateOnboardingContextCommand createUpdateCommand() {
        return new UpdateOnboardingContextCommand(
                1L, "SUMMARY", "프로젝트 개요 (수정됨)", "# Updated Spring Boot 3.5.x + Java 21 프로젝트...", 0);
    }
}
