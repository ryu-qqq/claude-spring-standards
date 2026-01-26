package com.ryuqq.application.architecture.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import com.ryuqq.application.architecture.factory.command.ArchitectureCommandFactory;
import com.ryuqq.application.architecture.manager.ArchitecturePersistenceManager;
import com.ryuqq.application.architecture.validator.ArchitectureValidator;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.exception.ArchitectureDuplicateNameException;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateArchitectureService 단위 테스트
 *
 * <p>Architecture 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateArchitectureService 단위 테스트")
class CreateArchitectureServiceTest {

    @Mock private ArchitectureValidator architectureValidator;

    @Mock private ArchitectureCommandFactory architectureCommandFactory;

    @Mock private ArchitecturePersistenceManager architecturePersistenceManager;

    @Mock private Architecture architecture;

    private CreateArchitectureService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateArchitectureService(
                        architectureValidator,
                        architectureCommandFactory,
                        architecturePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Architecture 생성")
        void execute_WithValidCommand_ShouldCreateArchitecture() {
            // given
            CreateArchitectureCommand command = createDefaultCommand();
            TechStackId techStackId = TechStackId.of(command.techStackId());
            ArchitectureName architectureName = ArchitectureName.of(command.name());
            Long expectedId = 1L;

            willDoNothing().given(architectureValidator).validateTechStackExists(techStackId);
            willDoNothing().given(architectureValidator).validateNameNotDuplicate(architectureName);
            given(architectureCommandFactory.create(command)).willReturn(architecture);
            given(architecturePersistenceManager.persist(architecture)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(architectureValidator).should().validateTechStackExists(techStackId);
            then(architectureValidator).should().validateNameNotDuplicate(architectureName);
            then(architectureCommandFactory).should().create(command);
            then(architecturePersistenceManager).should().persist(architecture);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 TechStack인 경우")
        void execute_WhenTechStackNotFound_ShouldThrowException() {
            // given
            CreateArchitectureCommand command = createDefaultCommand();
            TechStackId techStackId = TechStackId.of(command.techStackId());

            willThrow(new TechStackNotFoundException(techStackId.value()))
                    .given(architectureValidator)
                    .validateTechStackExists(techStackId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TechStackNotFoundException.class);

            then(architectureCommandFactory).shouldHaveNoInteractions();
            then(architecturePersistenceManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우")
        void execute_WhenNameDuplicate_ShouldThrowException() {
            // given
            CreateArchitectureCommand command = createDefaultCommand();
            TechStackId techStackId = TechStackId.of(command.techStackId());
            ArchitectureName architectureName = ArchitectureName.of(command.name());

            willDoNothing().given(architectureValidator).validateTechStackExists(techStackId);
            willThrow(new ArchitectureDuplicateNameException(command.name()))
                    .given(architectureValidator)
                    .validateNameNotDuplicate(architectureName);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ArchitectureDuplicateNameException.class);

            then(architectureCommandFactory).shouldHaveNoInteractions();
            then(architecturePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateArchitectureCommand createDefaultCommand() {
        return new CreateArchitectureCommand(
                1L,
                "hexagonal-multimodule",
                "HEXAGONAL",
                "Hexagonal architecture with multimodule",
                List.of("DIP", "SRP", "OCP"),
                List.of());
    }
}
