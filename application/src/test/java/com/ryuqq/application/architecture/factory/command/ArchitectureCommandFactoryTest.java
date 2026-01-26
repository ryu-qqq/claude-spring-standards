package com.ryuqq.application.architecture.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import com.ryuqq.application.architecture.fixture.CreateArchitectureCommandFixture;
import com.ryuqq.application.architecture.fixture.UpdateArchitectureCommandFixture;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.aggregate.ArchitectureUpdateData;
import com.ryuqq.domain.architecture.id.ArchitectureId;
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
 * ArchitectureCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ArchitectureCommandFactory 단위 테스트")
class ArchitectureCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ArchitectureCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ArchitectureCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateArchitectureCommand로 Architecture 생성")
        void create_WithValidCommand_ShouldReturnArchitecture() {
            // given
            CreateArchitectureCommand command = CreateArchitectureCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Architecture result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.patternType().name()).isEqualTo(command.patternType());
            assertThat(result.patternDescription().value()).isEqualTo(command.patternDescription());
            assertThat(result.techStackId().value()).isEqualTo(command.techStackId());
        }
    }

    @Nested
    @DisplayName("createUpdateData 메서드")
    class CreateUpdateData {

        @Test
        @DisplayName("성공 - UpdateArchitectureCommand로 ArchitectureUpdateData 생성")
        void createUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateArchitectureCommand command = UpdateArchitectureCommandFixture.defaultCommand();

            // when
            ArchitectureUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.patternType().name()).isEqualTo(command.patternType());
            assertThat(result.patternDescription().value()).isEqualTo(command.patternDescription());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateArchitectureCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateArchitectureCommand command = UpdateArchitectureCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ArchitectureId, ArchitectureUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.id());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.updateData().name().value()).isEqualTo(command.name());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
