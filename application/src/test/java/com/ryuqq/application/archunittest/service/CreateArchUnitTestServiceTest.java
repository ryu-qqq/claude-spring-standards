package com.ryuqq.application.archunittest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.factory.command.ArchUnitTestCommandFactory;
import com.ryuqq.application.archunittest.manager.ArchUnitTestPersistenceManager;
import com.ryuqq.application.archunittest.validator.ArchUnitTestValidator;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.exception.ArchUnitTestDuplicateCodeException;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateArchUnitTestService 단위 테스트
 *
 * <p>ArchUnitTest 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateArchUnitTestService 단위 테스트")
class CreateArchUnitTestServiceTest {

    @Mock private ArchUnitTestValidator archUnitTestValidator;

    @Mock private ArchUnitTestCommandFactory archUnitTestCommandFactory;

    @Mock private ArchUnitTestPersistenceManager archUnitTestPersistenceManager;

    @Mock private ArchUnitTest archUnitTest;

    private CreateArchUnitTestService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateArchUnitTestService(
                        archUnitTestValidator,
                        archUnitTestCommandFactory,
                        archUnitTestPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ArchUnitTest 생성")
        void execute_WithValidCommand_ShouldCreateArchUnitTest() {
            // given
            CreateArchUnitTestCommand command = createDefaultCommand();
            PackageStructureId structureId = PackageStructureId.of(command.structureId());
            String code = command.code();
            ArchUnitTestId savedId = ArchUnitTestId.of(1L);

            given(archUnitTestCommandFactory.toStructureId(command)).willReturn(structureId);
            given(archUnitTestCommandFactory.toCode(command)).willReturn(code);
            willDoNothing().given(archUnitTestValidator).validateNotDuplicate(structureId, code);
            given(archUnitTestCommandFactory.create(command)).willReturn(archUnitTest);
            given(archUnitTestPersistenceManager.persist(archUnitTest)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(archUnitTestCommandFactory).should().toStructureId(command);
            then(archUnitTestCommandFactory).should().toCode(command);
            then(archUnitTestValidator).should().validateNotDuplicate(structureId, code);
            then(archUnitTestCommandFactory).should().create(command);
            then(archUnitTestPersistenceManager).should().persist(archUnitTest);
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            CreateArchUnitTestCommand command = createDefaultCommand();
            PackageStructureId structureId = PackageStructureId.of(command.structureId());
            String code = command.code();

            given(archUnitTestCommandFactory.toStructureId(command)).willReturn(structureId);
            given(archUnitTestCommandFactory.toCode(command)).willReturn(code);
            willThrow(new ArchUnitTestDuplicateCodeException(structureId, code))
                    .given(archUnitTestValidator)
                    .validateNotDuplicate(structureId, code);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ArchUnitTestDuplicateCodeException.class);

            then(archUnitTestPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateArchUnitTestCommand createDefaultCommand() {
        return new CreateArchUnitTestCommand(
                1L,
                "ARCH-001",
                "Domain layer should not depend on Application",
                "Domain layer dependency test",
                "DomainLayerArchTest",
                "domainShouldNotDependOnApplication",
                "@ArchTest\nvoid domainShouldNotDependOnApplication() { }",
                "BLOCKER");
    }
}
