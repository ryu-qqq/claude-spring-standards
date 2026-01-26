package com.ryuqq.application.packagestructure.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;
import com.ryuqq.application.packagestructure.factory.command.PackageStructureCommandFactory;
import com.ryuqq.application.packagestructure.manager.PackageStructurePersistenceManager;
import com.ryuqq.application.packagestructure.validator.PackageStructureValidator;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructureUpdateData;
import com.ryuqq.domain.packagestructure.exception.PackageStructureDuplicateException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.time.Instant;
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
 * UpdatePackageStructureService 단위 테스트
 *
 * <p>PackageStructure 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdatePackageStructureService 단위 테스트")
class UpdatePackageStructureServiceTest {

    @Mock private PackageStructureValidator packageStructureValidator;

    @Mock private PackageStructureCommandFactory packageStructureCommandFactory;

    @Mock private PackageStructurePersistenceManager packageStructurePersistenceManager;

    @Mock private PackageStructure packageStructure;

    @Mock private PackageStructureUpdateData updateData;

    private UpdatePackageStructureService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdatePackageStructureService(
                        packageStructureValidator,
                        packageStructureCommandFactory,
                        packageStructurePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 PackageStructure 수정")
        void execute_WithValidCommand_ShouldUpdatePackageStructure() {
            // given
            UpdatePackageStructureCommand command = createDefaultCommand();
            PackageStructureId packageStructureId =
                    PackageStructureId.of(command.packageStructureId());
            ModuleId moduleId = ModuleId.of(1L);
            PathPattern pathPattern = PathPattern.of(command.pathPattern());
            Instant changedAt = Instant.now();
            UpdateContext<PackageStructureId, PackageStructureUpdateData> context =
                    new UpdateContext<>(packageStructureId, updateData, changedAt);

            given(packageStructureCommandFactory.createUpdateContext(command)).willReturn(context);
            given(packageStructureValidator.findExistingOrThrow(packageStructureId))
                    .willReturn(packageStructure);
            given(packageStructure.moduleId()).willReturn(moduleId);
            willDoNothing()
                    .given(packageStructureValidator)
                    .validateNotDuplicateExcluding(
                            any(ModuleId.class),
                            any(PathPattern.class),
                            any(PackageStructureId.class));
            willDoNothing().given(packageStructure).update(updateData, changedAt);
            given(packageStructurePersistenceManager.persist(packageStructure))
                    .willReturn(packageStructureId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(packageStructureCommandFactory).should().createUpdateContext(command);
            then(packageStructureValidator).should().findExistingOrThrow(packageStructureId);
            then(packageStructureValidator)
                    .should()
                    .validateNotDuplicateExcluding(moduleId, pathPattern, packageStructureId);
            then(packageStructure).should().update(updateData, changedAt);
            then(packageStructurePersistenceManager).should().persist(packageStructure);
        }

        @Test
        @DisplayName("실패 - 경로 패턴이 중복되는 경우")
        void execute_WhenPathDuplicate_ShouldThrowException() {
            // given
            UpdatePackageStructureCommand command = createDefaultCommand();
            PackageStructureId packageStructureId =
                    PackageStructureId.of(command.packageStructureId());
            ModuleId moduleId = ModuleId.of(1L);
            PathPattern pathPattern = PathPattern.of(command.pathPattern());
            Instant changedAt = Instant.now();
            UpdateContext<PackageStructureId, PackageStructureUpdateData> context =
                    new UpdateContext<>(packageStructureId, updateData, changedAt);

            given(packageStructureCommandFactory.createUpdateContext(command)).willReturn(context);
            given(packageStructureValidator.findExistingOrThrow(packageStructureId))
                    .willReturn(packageStructure);
            given(packageStructure.moduleId()).willReturn(moduleId);
            willThrow(new PackageStructureDuplicateException(moduleId.value(), pathPattern.value()))
                    .given(packageStructureValidator)
                    .validateNotDuplicateExcluding(
                            any(ModuleId.class),
                            any(PathPattern.class),
                            any(PackageStructureId.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(PackageStructureDuplicateException.class);

            then(packageStructurePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdatePackageStructureCommand createDefaultCommand() {
        return new UpdatePackageStructureCommand(
                1L,
                "domain/aggregate",
                List.of("AGGREGATE", "ENTITY"),
                "{Name}",
                null,
                "Aggregate 패키지 구조");
    }
}
