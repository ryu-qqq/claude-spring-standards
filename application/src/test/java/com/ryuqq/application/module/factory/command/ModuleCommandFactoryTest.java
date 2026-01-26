package com.ryuqq.application.module.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.module.dto.command.CreateModuleCommand;
import com.ryuqq.application.module.dto.command.UpdateModuleCommand;
import com.ryuqq.application.module.fixture.CreateModuleCommandFixture;
import com.ryuqq.application.module.fixture.UpdateModuleCommandFixture;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.aggregate.ModuleUpdateData;
import com.ryuqq.domain.module.id.ModuleId;
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
 * ModuleCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ModuleCommandFactory 단위 테스트")
class ModuleCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ModuleCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ModuleCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateModuleCommand로 Module 생성")
        void create_WithValidCommand_ShouldReturnModule() {
            // given
            CreateModuleCommand command = CreateModuleCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Module result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.modulePath().value()).isEqualTo(command.modulePath());
            assertThat(result.buildIdentifier().value()).isEqualTo(command.buildIdentifier());
            assertThat(result.layerId().value()).isEqualTo(command.layerId());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateModuleCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateModuleCommand command = UpdateModuleCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ModuleId, ModuleUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.moduleId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.updateData().name().value()).isEqualTo(command.name());
            assertThat(result.updateData().modulePath().value()).isEqualTo(command.modulePath());
            assertThat(result.updateData().buildIdentifier().value())
                    .isEqualTo(command.buildIdentifier());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
