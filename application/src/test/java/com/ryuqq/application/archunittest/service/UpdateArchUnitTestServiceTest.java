package com.ryuqq.application.archunittest.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.archunittest.factory.command.ArchUnitTestCommandFactory;
import com.ryuqq.application.archunittest.manager.ArchUnitTestPersistenceManager;
import com.ryuqq.application.archunittest.validator.ArchUnitTestValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTestUpdateData;
import com.ryuqq.domain.archunittest.exception.ArchUnitTestDuplicateCodeException;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
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
 * UpdateArchUnitTestService 단위 테스트
 *
 * <p>ArchUnitTest 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateArchUnitTestService 단위 테스트")
class UpdateArchUnitTestServiceTest {

    @Mock private ArchUnitTestValidator archUnitTestValidator;

    @Mock private ArchUnitTestCommandFactory archUnitTestCommandFactory;

    @Mock private ArchUnitTestPersistenceManager archUnitTestPersistenceManager;

    @Mock private ArchUnitTest archUnitTest;

    @Mock private ArchUnitTestUpdateData updateData;

    private UpdateArchUnitTestService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateArchUnitTestService(
                        archUnitTestValidator,
                        archUnitTestCommandFactory,
                        archUnitTestPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ArchUnitTest 수정 (코드 변경 있음)")
        void execute_WithValidCommandAndCodeChange_ShouldUpdateArchUnitTest() {
            // given
            UpdateArchUnitTestCommand command = createDefaultCommand();
            ArchUnitTestId archUnitTestId = ArchUnitTestId.of(command.archUnitTestId());
            PackageStructureId structureId = PackageStructureId.of(1L);
            String newCode = "ARCH-002";
            Instant changedAt = Instant.now();
            UpdateContext<ArchUnitTestId, ArchUnitTestUpdateData> context =
                    new UpdateContext<>(archUnitTestId, updateData, changedAt);

            given(archUnitTestCommandFactory.createUpdateContext(command)).willReturn(context);
            given(archUnitTestValidator.findExistingOrThrow(archUnitTestId))
                    .willReturn(archUnitTest);
            given(archUnitTestCommandFactory.toCode(command)).willReturn(newCode);
            given(archUnitTest.structureId()).willReturn(structureId);
            willDoNothing()
                    .given(archUnitTestValidator)
                    .validateNotDuplicateExcluding(
                            any(PackageStructureId.class), eq(newCode), any(ArchUnitTestId.class));
            willDoNothing().given(archUnitTest).update(updateData, changedAt);
            given(archUnitTestPersistenceManager.persist(archUnitTest)).willReturn(archUnitTestId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(archUnitTestCommandFactory).should().createUpdateContext(command);
            then(archUnitTestValidator).should().findExistingOrThrow(archUnitTestId);
            then(archUnitTestCommandFactory).should().toCode(command);
            then(archUnitTestValidator)
                    .should()
                    .validateNotDuplicateExcluding(structureId, newCode, archUnitTestId);
            then(archUnitTest).should().update(updateData, changedAt);
            then(archUnitTestPersistenceManager).should().persist(archUnitTest);
        }

        @Test
        @DisplayName("성공 - 유효한 Command로 ArchUnitTest 수정 (코드 변경 없음)")
        void execute_WithValidCommandAndNoCodeChange_ShouldUpdateWithoutCodeValidation() {
            // given
            UpdateArchUnitTestCommand command = createDefaultCommand();
            ArchUnitTestId archUnitTestId = ArchUnitTestId.of(command.archUnitTestId());
            Instant changedAt = Instant.now();
            UpdateContext<ArchUnitTestId, ArchUnitTestUpdateData> context =
                    new UpdateContext<>(archUnitTestId, updateData, changedAt);

            given(archUnitTestCommandFactory.createUpdateContext(command)).willReturn(context);
            given(archUnitTestValidator.findExistingOrThrow(archUnitTestId))
                    .willReturn(archUnitTest);
            given(archUnitTestCommandFactory.toCode(command)).willReturn(null);
            willDoNothing().given(archUnitTest).update(updateData, changedAt);
            given(archUnitTestPersistenceManager.persist(archUnitTest)).willReturn(archUnitTestId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(archUnitTestValidator).shouldHaveNoMoreInteractions();
            then(archUnitTest).should().update(updateData, changedAt);
            then(archUnitTestPersistenceManager).should().persist(archUnitTest);
        }

        @Test
        @DisplayName("실패 - 코드가 중복되는 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            UpdateArchUnitTestCommand command = createDefaultCommand();
            ArchUnitTestId archUnitTestId = ArchUnitTestId.of(command.archUnitTestId());
            PackageStructureId structureId = PackageStructureId.of(1L);
            String newCode = "ARCH-002";
            Instant changedAt = Instant.now();
            UpdateContext<ArchUnitTestId, ArchUnitTestUpdateData> context =
                    new UpdateContext<>(archUnitTestId, updateData, changedAt);

            given(archUnitTestCommandFactory.createUpdateContext(command)).willReturn(context);
            given(archUnitTestValidator.findExistingOrThrow(archUnitTestId))
                    .willReturn(archUnitTest);
            given(archUnitTestCommandFactory.toCode(command)).willReturn(newCode);
            given(archUnitTest.structureId()).willReturn(structureId);
            willThrow(new ArchUnitTestDuplicateCodeException(structureId, newCode))
                    .given(archUnitTestValidator)
                    .validateNotDuplicateExcluding(
                            any(PackageStructureId.class), eq(newCode), any(ArchUnitTestId.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ArchUnitTestDuplicateCodeException.class);

            then(archUnitTestPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateArchUnitTestCommand createDefaultCommand() {
        return new UpdateArchUnitTestCommand(
                1L,
                "ARCH-001",
                "Domain Layer Dependency Test",
                "도메인 레이어 의존성 검증",
                "DomainLayerArchTest",
                "domainLayerShouldNotDependOnOtherLayers",
                "ArchRuleDefinition.noClasses()...",
                "CRITICAL");
    }
}
