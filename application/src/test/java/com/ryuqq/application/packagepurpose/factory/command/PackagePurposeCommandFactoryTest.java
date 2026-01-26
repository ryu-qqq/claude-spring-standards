package com.ryuqq.application.packagepurpose.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.fixture.CreatePackagePurposeCommandFixture;
import com.ryuqq.application.packagepurpose.fixture.UpdatePackagePurposeCommandFixture;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurposeUpdateData;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
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
 * PackagePurposeCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("PackagePurposeCommandFactory 단위 테스트")
class PackagePurposeCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private PackagePurposeCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new PackagePurposeCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreatePackagePurposeCommand로 PackagePurpose 생성")
        void create_WithValidCommand_ShouldReturnPackagePurpose() {
            // given
            CreatePackagePurposeCommand command =
                    CreatePackagePurposeCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            PackagePurpose result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.structureId().value()).isEqualTo(command.structureId());
            assertThat(result.code().value()).isEqualTo(command.code());
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.description()).isEqualTo(command.description());
        }
    }

    @Nested
    @DisplayName("toUpdateData 메서드")
    class ToUpdateData {

        @Test
        @DisplayName("성공 - UpdatePackagePurposeCommand로 PackagePurposeUpdateData 생성")
        void toUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdatePackagePurposeCommand command =
                    UpdatePackagePurposeCommandFixture.defaultCommand();

            // when
            PackagePurposeUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.code().value()).isEqualTo(command.code());
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.description()).isEqualTo(command.description());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdatePackagePurposeCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdatePackagePurposeCommand command =
                    UpdatePackagePurposeCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<PackagePurposeId, PackagePurposeUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.packagePurposeId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
