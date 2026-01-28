package com.ryuqq.application.packagestructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;
import com.ryuqq.application.packagestructure.factory.command.PackageStructureCommandFactory;
import com.ryuqq.application.packagestructure.manager.PackageStructurePersistenceManager;
import com.ryuqq.application.packagestructure.validator.PackageStructureValidator;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.exception.PackageStructureDuplicateException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreatePackageStructureService 단위 테스트
 *
 * <p>PackageStructure 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreatePackageStructureService 단위 테스트")
class CreatePackageStructureServiceTest {

    @Mock private PackageStructureValidator packageStructureValidator;

    @Mock private PackageStructureCommandFactory packageStructureCommandFactory;

    @Mock private PackageStructurePersistenceManager packageStructurePersistenceManager;

    @Mock private PackageStructure packageStructure;

    private CreatePackageStructureService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreatePackageStructureService(
                        packageStructureValidator,
                        packageStructureCommandFactory,
                        packageStructurePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 PackageStructure 생성")
        void execute_WithValidCommand_ShouldCreatePackageStructure() {
            // given
            CreatePackageStructureCommand command = createDefaultCommand();
            ModuleId moduleId = ModuleId.of(command.moduleId());
            PathPattern pathPattern = PathPattern.of(command.pathPattern());
            PackageStructureId savedId = PackageStructureId.of(1L);

            willDoNothing()
                    .given(packageStructureValidator)
                    .validateNotDuplicate(moduleId, pathPattern);
            given(packageStructureCommandFactory.create(command)).willReturn(packageStructure);
            given(packageStructurePersistenceManager.persist(packageStructure)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(packageStructureValidator).should().validateNotDuplicate(moduleId, pathPattern);
            then(packageStructureCommandFactory).should().create(command);
            then(packageStructurePersistenceManager).should().persist(packageStructure);
        }

        @Test
        @DisplayName("실패 - 중복된 경로 패턴인 경우")
        void execute_WhenPathDuplicate_ShouldThrowException() {
            // given
            CreatePackageStructureCommand command = createDefaultCommand();
            ModuleId moduleId = ModuleId.of(command.moduleId());
            PathPattern pathPattern = PathPattern.of(command.pathPattern());

            willThrow(
                            new PackageStructureDuplicateException(
                                    moduleId.value(), command.pathPattern()))
                    .given(packageStructureValidator)
                    .validateNotDuplicate(moduleId, pathPattern);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(PackageStructureDuplicateException.class);

            then(packageStructureCommandFactory).shouldHaveNoInteractions();
            then(packageStructurePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreatePackageStructureCommand createDefaultCommand() {
        return new CreatePackageStructureCommand(1L, "{domain}/aggregate", "Aggregate package");
    }
}
