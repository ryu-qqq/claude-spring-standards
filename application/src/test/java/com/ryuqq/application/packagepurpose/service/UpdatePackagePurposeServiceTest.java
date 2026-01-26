package com.ryuqq.application.packagepurpose.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.factory.command.PackagePurposeCommandFactory;
import com.ryuqq.application.packagepurpose.manager.PackagePurposePersistenceManager;
import com.ryuqq.application.packagepurpose.validator.PackagePurposeValidator;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurposeUpdateData;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeDuplicateCodeException;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
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
 * UpdatePackagePurposeService 단위 테스트
 *
 * <p>PackagePurpose 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdatePackagePurposeService 단위 테스트")
class UpdatePackagePurposeServiceTest {

    @Mock private PackagePurposeValidator packagePurposeValidator;

    @Mock private PackagePurposeCommandFactory packagePurposeCommandFactory;

    @Mock private PackagePurposePersistenceManager packagePurposePersistenceManager;

    @Mock private PackagePurpose packagePurpose;

    @Mock private PackagePurposeUpdateData updateData;

    private UpdatePackagePurposeService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdatePackagePurposeService(
                        packagePurposeValidator,
                        packagePurposeCommandFactory,
                        packagePurposePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 PackagePurpose 수정")
        void execute_WithValidCommand_ShouldUpdatePackagePurpose() {
            // given
            UpdatePackagePurposeCommand command = createDefaultCommand();
            PackagePurposeId packagePurposeId = PackagePurposeId.of(command.packagePurposeId());
            Instant changedAt = Instant.now();
            UpdateContext<PackagePurposeId, PackagePurposeUpdateData> context =
                    new UpdateContext<>(packagePurposeId, updateData, changedAt);

            given(packagePurposeCommandFactory.createUpdateContext(command)).willReturn(context);
            given(packagePurposeValidator.validateAndGetForUpdate(packagePurposeId, updateData))
                    .willReturn(packagePurpose);
            willDoNothing().given(packagePurpose).update(updateData, changedAt);
            given(packagePurposePersistenceManager.persist(packagePurpose))
                    .willReturn(packagePurposeId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(packagePurposeCommandFactory).should().createUpdateContext(command);
            then(packagePurposeValidator)
                    .should()
                    .validateAndGetForUpdate(packagePurposeId, updateData);
            then(packagePurpose).should().update(updateData, changedAt);
            then(packagePurposePersistenceManager).should().persist(packagePurpose);
        }

        @Test
        @DisplayName("실패 - 코드가 중복되는 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            UpdatePackagePurposeCommand command = createDefaultCommand();
            PackagePurposeId packagePurposeId = PackagePurposeId.of(command.packagePurposeId());
            Instant changedAt = Instant.now();
            UpdateContext<PackagePurposeId, PackagePurposeUpdateData> context =
                    new UpdateContext<>(packagePurposeId, updateData, changedAt);

            given(packagePurposeCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new PackagePurposeDuplicateCodeException(1L, command.code()))
                    .given(packagePurposeValidator)
                    .validateAndGetForUpdate(packagePurposeId, updateData);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(PackagePurposeDuplicateCodeException.class);

            then(packagePurposePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdatePackagePurposeCommand createDefaultCommand() {
        return new UpdatePackagePurposeCommand(
                1L,
                "AGGREGATE",
                "Aggregate Root",
                "Aggregate Root 패키지 목적",
                List.of("AGGREGATE"),
                "{Name}",
                null);
    }
}
