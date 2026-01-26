package com.ryuqq.application.convention.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.convention.dto.command.CreateConventionCommand;
import com.ryuqq.application.convention.factory.command.ConventionCommandFactory;
import com.ryuqq.application.convention.manager.ConventionPersistenceManager;
import com.ryuqq.application.convention.validator.ConventionValidator;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.exception.ConventionDuplicateException;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateConventionService 단위 테스트
 *
 * <p>Convention 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateConventionService 단위 테스트")
class CreateConventionServiceTest {

    @Mock private ConventionValidator conventionValidator;

    @Mock private ConventionCommandFactory conventionCommandFactory;

    @Mock private ConventionPersistenceManager conventionPersistenceManager;

    @Mock private Convention convention;

    private CreateConventionService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateConventionService(
                        conventionValidator,
                        conventionCommandFactory,
                        conventionPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Convention 생성")
        void execute_WithValidCommand_ShouldCreateConvention() {
            // given
            CreateConventionCommand command = createDefaultCommand();
            ModuleId moduleId = ModuleId.of(command.moduleId());
            ConventionVersion version = ConventionVersion.of(command.version());
            Long expectedId = 1L;

            willDoNothing().given(conventionValidator).validateNotDuplicate(moduleId, version);
            given(conventionCommandFactory.create(command)).willReturn(convention);
            given(conventionPersistenceManager.persist(convention)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(conventionValidator).should().validateNotDuplicate(moduleId, version);
            then(conventionCommandFactory).should().create(command);
            then(conventionPersistenceManager).should().persist(convention);
        }

        @Test
        @DisplayName("실패 - 중복된 버전인 경우")
        void execute_WhenVersionDuplicate_ShouldThrowException() {
            // given
            CreateConventionCommand command = createDefaultCommand();
            ModuleId moduleId = ModuleId.of(command.moduleId());
            ConventionVersion version = ConventionVersion.of(command.version());

            willThrow(new ConventionDuplicateException(moduleId, version.value()))
                    .given(conventionValidator)
                    .validateNotDuplicate(moduleId, version);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ConventionDuplicateException.class);

            then(conventionCommandFactory).shouldHaveNoInteractions();
            then(conventionPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateConventionCommand createDefaultCommand() {
        return new CreateConventionCommand(1L, "1.0.0", "Domain layer convention");
    }
}
