package com.ryuqq.application.packagestructure.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;
import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructureUpdateData;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
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
 * PackageStructureCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("PackageStructureCommandFactory 단위 테스트")
class PackageStructureCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private PackageStructureCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new PackageStructureCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreatePackageStructureCommand로 PackageStructure 생성")
        void create_WithValidCommand_ShouldReturnPackageStructure() {
            // given
            CreatePackageStructureCommand command =
                    new CreatePackageStructureCommand(
                            1L,
                            "aggregate",
                            List.of("AGGREGATE"),
                            "{Domain}",
                            "",
                            "도메인 애그리게이트 패키지");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            PackageStructure result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleId().value()).isEqualTo(command.moduleId());
            assertThat(result.pathPattern().value()).isEqualTo(command.pathPattern());
            assertThat(result.allowedClassTypes().values())
                    .containsExactlyElementsOf(command.allowedClassTypes());
            assertThat(result.namingPattern().value()).isEqualTo(command.namingPattern());
            assertThat(result.description()).isEqualTo(command.description());
        }
    }

    @Nested
    @DisplayName("toUpdateData 메서드")
    class ToUpdateData {

        @Test
        @DisplayName("성공 - UpdatePackageStructureCommand로 PackageStructureUpdateData 생성")
        void toUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdatePackageStructureCommand command =
                    new UpdatePackageStructureCommand(
                            1L, "updated/path", List.of("VALUE_OBJECT"), "{VO}", "VO", "수정된 설명");

            // when
            PackageStructureUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.pathPattern().value()).isEqualTo(command.pathPattern());
            assertThat(result.allowedClassTypes().values())
                    .containsExactlyElementsOf(command.allowedClassTypes());
            assertThat(result.namingPattern().value()).isEqualTo(command.namingPattern());
            assertThat(result.namingSuffix().value()).isEqualTo(command.namingSuffix());
            assertThat(result.description()).isEqualTo(command.description());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdatePackageStructureCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdatePackageStructureCommand command =
                    new UpdatePackageStructureCommand(
                            1L, "updated/path", List.of("VALUE_OBJECT"), "{VO}", "VO", "수정된 설명");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<PackageStructureId, PackageStructureUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.packageStructureId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
