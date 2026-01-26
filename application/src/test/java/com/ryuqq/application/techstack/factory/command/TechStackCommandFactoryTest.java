package com.ryuqq.application.techstack.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import com.ryuqq.application.techstack.dto.command.UpdateTechStackCommand;
import com.ryuqq.application.techstack.fixture.CreateTechStackCommandFixture;
import com.ryuqq.application.techstack.fixture.UpdateTechStackCommandFixture;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.aggregate.TechStackUpdateData;
import com.ryuqq.domain.techstack.id.TechStackId;
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
 * TechStackCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("TechStackCommandFactory 단위 테스트")
class TechStackCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private TechStackCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new TechStackCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateTechStackCommand로 TechStack 생성")
        void create_WithValidCommand_ShouldReturnTechStack() {
            // given
            CreateTechStackCommand command = CreateTechStackCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            TechStack result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.languageType().name()).isEqualTo(command.languageType());
            assertThat(result.languageVersion().value()).isEqualTo(command.languageVersion());
            assertThat(result.frameworkType().name()).isEqualTo(command.frameworkType());
            assertThat(result.frameworkVersion().value()).isEqualTo(command.frameworkVersion());
            assertThat(result.platformType().name()).isEqualTo(command.platformType());
            assertThat(result.runtimeEnvironment().name()).isEqualTo(command.runtimeEnvironment());
            assertThat(result.buildToolType().name()).isEqualTo(command.buildToolType());
        }

        @Test
        @DisplayName("성공 - 언어 기능이 포함된 Command로 생성")
        void create_WithLanguageFeatures_ShouldReturnTechStackWithFeatures() {
            // given
            CreateTechStackCommand command =
                    CreateTechStackCommandFixture.commandWithLanguageFeatures();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            TechStack result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.languageFeatures().values())
                    .containsExactlyElementsOf(command.languageFeatures());
        }

        @Test
        @DisplayName("성공 - 프레임워크 모듈이 포함된 Command로 생성")
        void create_WithFrameworkModules_ShouldReturnTechStackWithModules() {
            // given
            CreateTechStackCommand command =
                    CreateTechStackCommandFixture.commandWithFrameworkModules();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            TechStack result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.frameworkModules().values())
                    .containsExactlyElementsOf(command.frameworkModules());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateTechStackCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateTechStackCommand command = UpdateTechStackCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<TechStackId, TechStackUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.id());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.updateData().name().value()).isEqualTo(command.name());
            assertThat(result.updateData().status().name()).isEqualTo(command.status());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("성공 - Deprecated 상태로 UpdateContext 생성")
        void
                createUpdateContext_WithDeprecateCommand_ShouldReturnUpdateContextWithDeprecatedStatus() {
            // given
            UpdateTechStackCommand command = UpdateTechStackCommandFixture.deprecateCommand(1L);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<TechStackId, TechStackUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updateData().status().name()).isEqualTo("DEPRECATED");
        }
    }
}
