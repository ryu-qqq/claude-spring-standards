package com.ryuqq.application.convention.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;
import com.ryuqq.application.convention.factory.command.ConventionCommandFactory;
import com.ryuqq.application.convention.fixture.UpdateConventionCommandFixture;
import com.ryuqq.application.convention.manager.ConventionPersistenceManager;
import com.ryuqq.application.convention.validator.ConventionValidator;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.aggregate.ConventionUpdateData;
import com.ryuqq.domain.convention.exception.ConventionDuplicateException;
import com.ryuqq.domain.convention.exception.ConventionNotFoundException;
import com.ryuqq.domain.convention.fixture.ConventionFixture;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
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
 * UpdateConventionService 단위 테스트
 *
 * <p>Convention 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateConventionService 단위 테스트")
class UpdateConventionServiceTest {

    @Mock private ConventionValidator conventionValidator;

    @Mock private ConventionCommandFactory conventionCommandFactory;

    @Mock private ConventionPersistenceManager conventionPersistenceManager;

    private UpdateConventionService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateConventionService(
                        conventionValidator,
                        conventionCommandFactory,
                        conventionPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Convention 수정")
        void execute_WithValidCommand_ShouldUpdateConvention() {
            // given
            UpdateConventionCommand command = UpdateConventionCommandFixture.defaultCommand();
            ConventionId conventionId = ConventionId.of(command.id());
            ModuleId moduleId = ModuleId.of(command.moduleId());
            ConventionVersion version = ConventionVersion.of(command.version());
            Convention convention = ConventionFixture.defaultExistingConvention();
            Instant changedAt = Instant.now();

            ConventionUpdateData updateData =
                    new ConventionUpdateData(
                            moduleId, version, command.description(), command.active());

            UpdateContext<ConventionId, ConventionUpdateData> context =
                    new UpdateContext<>(conventionId, updateData, changedAt);

            given(conventionCommandFactory.createUpdateContext(command)).willReturn(context);
            willDoNothing()
                    .given(conventionValidator)
                    .validateNotDuplicateExcluding(moduleId, version, conventionId);
            given(conventionValidator.findExistingOrThrow(conventionId)).willReturn(convention);

            // when
            sut.execute(command);

            // then
            then(conventionCommandFactory).should().createUpdateContext(command);
            then(conventionValidator)
                    .should()
                    .validateNotDuplicateExcluding(moduleId, version, conventionId);
            then(conventionValidator).should().findExistingOrThrow(conventionId);
            then(conventionPersistenceManager).should().persist(convention);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 Convention인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateConventionCommand command = UpdateConventionCommandFixture.defaultCommand();
            ConventionId conventionId = ConventionId.of(command.id());
            ModuleId moduleId = ModuleId.of(command.moduleId());
            ConventionVersion version = ConventionVersion.of(command.version());
            Instant changedAt = Instant.now();

            ConventionUpdateData updateData =
                    new ConventionUpdateData(
                            moduleId, version, command.description(), command.active());

            UpdateContext<ConventionId, ConventionUpdateData> context =
                    new UpdateContext<>(conventionId, updateData, changedAt);

            given(conventionCommandFactory.createUpdateContext(command)).willReturn(context);
            willDoNothing()
                    .given(conventionValidator)
                    .validateNotDuplicateExcluding(moduleId, version, conventionId);
            willThrow(new ConventionNotFoundException(command.id()))
                    .given(conventionValidator)
                    .findExistingOrThrow(conventionId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ConventionNotFoundException.class);

            then(conventionPersistenceManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 중복된 모듈+버전인 경우")
        void execute_WhenDuplicate_ShouldThrowException() {
            // given
            UpdateConventionCommand command = UpdateConventionCommandFixture.defaultCommand();
            ConventionId conventionId = ConventionId.of(command.id());
            ModuleId moduleId = ModuleId.of(command.moduleId());
            ConventionVersion version = ConventionVersion.of(command.version());
            Instant changedAt = Instant.now();

            ConventionUpdateData updateData =
                    new ConventionUpdateData(
                            moduleId, version, command.description(), command.active());

            UpdateContext<ConventionId, ConventionUpdateData> context =
                    new UpdateContext<>(conventionId, updateData, changedAt);

            given(conventionCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new ConventionDuplicateException(moduleId, command.version()))
                    .given(conventionValidator)
                    .validateNotDuplicateExcluding(moduleId, version, conventionId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ConventionDuplicateException.class);

            then(conventionValidator).shouldHaveNoMoreInteractions();
            then(conventionPersistenceManager).shouldHaveNoInteractions();
        }
    }
}
