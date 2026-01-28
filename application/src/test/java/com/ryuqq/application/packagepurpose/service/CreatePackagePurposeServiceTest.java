package com.ryuqq.application.packagepurpose.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.factory.command.PackagePurposeCommandFactory;
import com.ryuqq.application.packagepurpose.manager.PackagePurposePersistenceManager;
import com.ryuqq.application.packagepurpose.validator.PackagePurposeValidator;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeDuplicateCodeException;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
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
 * CreatePackagePurposeService 단위 테스트
 *
 * <p>PackagePurpose 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreatePackagePurposeService 단위 테스트")
class CreatePackagePurposeServiceTest {

    @Mock private PackagePurposeValidator packagePurposeValidator;

    @Mock private PackagePurposeCommandFactory packagePurposeCommandFactory;

    @Mock private PackagePurposePersistenceManager packagePurposePersistenceManager;

    @Mock private PackagePurpose packagePurpose;

    private CreatePackagePurposeService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreatePackagePurposeService(
                        packagePurposeValidator,
                        packagePurposeCommandFactory,
                        packagePurposePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 PackagePurpose 생성")
        void execute_WithValidCommand_ShouldCreatePackagePurpose() {
            // given
            CreatePackagePurposeCommand command = createDefaultCommand();
            PackageStructureId structureId = PackageStructureId.of(command.structureId());
            PurposeCode purposeCode = PurposeCode.of(command.code());
            PackagePurposeId savedId = PackagePurposeId.of(1L);

            willDoNothing()
                    .given(packagePurposeValidator)
                    .validateNotDuplicate(structureId, purposeCode);
            given(packagePurposeCommandFactory.create(command)).willReturn(packagePurpose);
            given(packagePurposePersistenceManager.persist(packagePurpose)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(packagePurposeValidator).should().validateNotDuplicate(structureId, purposeCode);
            then(packagePurposeCommandFactory).should().create(command);
            then(packagePurposePersistenceManager).should().persist(packagePurpose);
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            CreatePackagePurposeCommand command = createDefaultCommand();
            PackageStructureId structureId = PackageStructureId.of(command.structureId());
            PurposeCode purposeCode = PurposeCode.of(command.code());

            willThrow(new PackagePurposeDuplicateCodeException(structureId.value(), command.code()))
                    .given(packagePurposeValidator)
                    .validateNotDuplicate(structureId, purposeCode);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(PackagePurposeDuplicateCodeException.class);

            then(packagePurposeCommandFactory).shouldHaveNoInteractions();
            then(packagePurposePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreatePackagePurposeCommand createDefaultCommand() {
        return new CreatePackagePurposeCommand(
                1L, "AGGREGATE", "Aggregate Root", "Aggregate Root 패키지 목적");
    }
}
