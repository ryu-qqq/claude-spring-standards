package com.ryuqq.application.convention.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.convention.dto.command.CreateConventionCommand;
import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;
import com.ryuqq.application.convention.fixture.CreateConventionCommandFixture;
import com.ryuqq.application.convention.fixture.UpdateConventionCommandFixture;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.aggregate.ConventionUpdateData;
import com.ryuqq.domain.convention.id.ConventionId;
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
 * ConventionCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ConventionCommandFactory 단위 테스트")
class ConventionCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ConventionCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ConventionCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateConventionCommand로 Convention 생성")
        void create_WithValidCommand_ShouldReturnConvention() {
            // given
            CreateConventionCommand command = CreateConventionCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Convention result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleId().value()).isEqualTo(command.moduleId());
            assertThat(result.version().value()).isEqualTo(command.version());
            assertThat(result.description()).isEqualTo(command.description());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateConventionCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateConventionCommand command = UpdateConventionCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ConventionId, ConventionUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.id());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.updateData().moduleId().value()).isEqualTo(command.moduleId());
            assertThat(result.updateData().version().value()).isEqualTo(command.version());
            assertThat(result.updateData().description()).isEqualTo(command.description());
            assertThat(result.updateData().active()).isEqualTo(command.active());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("성공 - 비활성화 Command로 UpdateContext 생성")
        void createUpdateContext_WithDeactivateCommand_ShouldReturnUpdateContextWithInactive() {
            // given
            UpdateConventionCommand command = UpdateConventionCommandFixture.deactivateCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ConventionId, ConventionUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updateData().active()).isFalse();
        }
    }
}
