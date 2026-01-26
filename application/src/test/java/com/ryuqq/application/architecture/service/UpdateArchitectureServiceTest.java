package com.ryuqq.application.architecture.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import com.ryuqq.application.architecture.factory.command.ArchitectureCommandFactory;
import com.ryuqq.application.architecture.fixture.UpdateArchitectureCommandFixture;
import com.ryuqq.application.architecture.manager.ArchitecturePersistenceManager;
import com.ryuqq.application.architecture.validator.ArchitectureValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.aggregate.ArchitectureUpdateData;
import com.ryuqq.domain.architecture.exception.ArchitectureDuplicateNameException;
import com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException;
import com.ryuqq.domain.architecture.fixture.ArchitectureFixture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
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
 * UpdateArchitectureService 단위 테스트
 *
 * <p>Architecture 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateArchitectureService 단위 테스트")
class UpdateArchitectureServiceTest {

    @Mock private ArchitectureValidator architectureValidator;

    @Mock private ArchitectureCommandFactory architectureCommandFactory;

    @Mock private ArchitecturePersistenceManager architecturePersistenceManager;

    @Mock private ArchitectureUpdateData updateData;

    private UpdateArchitectureService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateArchitectureService(
                        architectureValidator,
                        architectureCommandFactory,
                        architecturePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Architecture 수정")
        void execute_WithValidCommand_ShouldUpdateArchitecture() {
            // given
            UpdateArchitectureCommand command = UpdateArchitectureCommandFixture.defaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.id());
            ArchitectureName architectureName = ArchitectureName.of(command.name());
            Architecture architecture = ArchitectureFixture.defaultExistingArchitecture();
            Instant changedAt = Instant.now();

            UpdateContext<ArchitectureId, ArchitectureUpdateData> context =
                    new UpdateContext<>(architectureId, updateData, changedAt);

            given(architectureCommandFactory.createUpdateContext(command)).willReturn(context);
            willDoNothing()
                    .given(architectureValidator)
                    .validateNameNotDuplicateExcluding(architectureName, architectureId);
            given(architectureValidator.findExistingOrThrow(architectureId))
                    .willReturn(architecture);

            // when
            sut.execute(command);

            // then
            then(architectureCommandFactory).should().createUpdateContext(command);
            then(architectureValidator)
                    .should()
                    .validateNameNotDuplicateExcluding(architectureName, architectureId);
            then(architectureValidator).should().findExistingOrThrow(architectureId);
            then(architecturePersistenceManager).should().persist(architecture);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Architecture인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateArchitectureCommand command = UpdateArchitectureCommandFixture.defaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.id());
            ArchitectureName architectureName = ArchitectureName.of(command.name());
            Instant changedAt = Instant.now();

            UpdateContext<ArchitectureId, ArchitectureUpdateData> context =
                    new UpdateContext<>(architectureId, updateData, changedAt);

            given(architectureCommandFactory.createUpdateContext(command)).willReturn(context);
            willDoNothing()
                    .given(architectureValidator)
                    .validateNameNotDuplicateExcluding(architectureName, architectureId);
            willThrow(new ArchitectureNotFoundException(command.id()))
                    .given(architectureValidator)
                    .findExistingOrThrow(architectureId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ArchitectureNotFoundException.class);

            then(architecturePersistenceManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우")
        void execute_WhenNameDuplicate_ShouldThrowException() {
            // given
            UpdateArchitectureCommand command = UpdateArchitectureCommandFixture.defaultCommand();
            ArchitectureId architectureId = ArchitectureId.of(command.id());
            ArchitectureName architectureName = ArchitectureName.of(command.name());
            Instant changedAt = Instant.now();

            UpdateContext<ArchitectureId, ArchitectureUpdateData> context =
                    new UpdateContext<>(architectureId, updateData, changedAt);

            given(architectureCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new ArchitectureDuplicateNameException(command.name()))
                    .given(architectureValidator)
                    .validateNameNotDuplicateExcluding(architectureName, architectureId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ArchitectureDuplicateNameException.class);

            then(architectureValidator).shouldHaveNoMoreInteractions();
            then(architecturePersistenceManager).shouldHaveNoInteractions();
        }
    }
}
