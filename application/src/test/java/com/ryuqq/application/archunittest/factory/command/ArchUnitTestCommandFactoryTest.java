package com.ryuqq.application.archunittest.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTestUpdateData;
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
 * ArchUnitTestCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ArchUnitTestCommandFactory 단위 테스트")
class ArchUnitTestCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ArchUnitTestCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ArchUnitTestCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateArchUnitTestCommand로 ArchUnitTest 생성")
        void create_WithValidCommand_ShouldReturnArchUnitTest() {
            // given
            CreateArchUnitTestCommand command =
                    new CreateArchUnitTestCommand(
                            1L,
                            "ARCH-001",
                            "도메인 레이어 의존성 테스트",
                            "도메인 레이어는 외부 의존성을 가져서는 안됩니다",
                            "DomainLayerArchTest",
                            "domainShouldNotDependOnAdapter",
                            "@Test void test() {}",
                            "BLOCKER");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ArchUnitTest result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.structureId().value()).isEqualTo(command.structureId());
            assertThat(result.code()).isEqualTo(command.code());
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.description().value()).isEqualTo(command.description());
            assertThat(result.testClassName()).isEqualTo(command.testClassName());
            assertThat(result.testMethodName()).isEqualTo(command.testMethodName());
            assertThat(result.testCode().value()).isEqualTo(command.testCode());
            assertThat(result.severity().name()).isEqualTo(command.severity());
        }

        @Test
        @DisplayName("성공 - nullable 필드 없이 ArchUnitTest 생성")
        void create_WithoutNullableFields_ShouldReturnArchUnitTestWithNulls() {
            // given
            CreateArchUnitTestCommand command =
                    new CreateArchUnitTestCommand(
                            1L,
                            "ARCH-002",
                            "기본 테스트",
                            null,
                            "TestClass",
                            "testMethod",
                            "@Test void test() {}",
                            null);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ArchUnitTest result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.description()).isNull();
            assertThat(result.severity()).isNull();
        }

        @Test
        @DisplayName("성공 - Instant를 직접 전달하여 ArchUnitTest 생성")
        void create_WithExplicitTime_ShouldReturnArchUnitTestWithGivenTime() {
            // given
            CreateArchUnitTestCommand command =
                    new CreateArchUnitTestCommand(
                            1L,
                            "ARCH-003",
                            "시간 테스트",
                            null,
                            "TestClass",
                            "testMethod",
                            "@Test void t() {}",
                            "MAJOR");
            Instant customTime = Instant.parse("2024-06-01T12:00:00Z");

            // when
            ArchUnitTest result = sut.create(command, customTime);

            // then
            assertThat(result).isNotNull();
            assertThat(result.createdAt()).isEqualTo(customTime);
        }
    }

    @Nested
    @DisplayName("toUpdateData 메서드")
    class ToUpdateData {

        @Test
        @DisplayName("성공 - UpdateArchUnitTestCommand로 ArchUnitTestUpdateData 생성")
        void toUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateArchUnitTestCommand command =
                    new UpdateArchUnitTestCommand(
                            1L,
                            "ARCH-001-UPDATED",
                            "수정된 테스트 이름",
                            "수정된 설명",
                            "UpdatedTestClass",
                            "updatedTestMethod",
                            "@Test void updated() {}",
                            "CRITICAL");

            // when
            ArchUnitTestUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.code()).isEqualTo(command.code());
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.description().value()).isEqualTo(command.description());
            assertThat(result.testClassName()).isEqualTo(command.testClassName());
            assertThat(result.testMethodName()).isEqualTo(command.testMethodName());
            assertThat(result.testCode().value()).isEqualTo(command.testCode());
            assertThat(result.severity().name()).isEqualTo(command.severity());
        }

        @Test
        @DisplayName("성공 - 부분 업데이트 Command로 ArchUnitTestUpdateData 생성")
        void toUpdateData_WithPartialCommand_ShouldReturnPartialUpdateData() {
            // given
            UpdateArchUnitTestCommand command =
                    new UpdateArchUnitTestCommand(
                            1L, "ARCH-001", null, null, null, null, null, null);

            // when
            ArchUnitTestUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.code()).isEqualTo("ARCH-001");
            assertThat(result.name()).isNull();
            assertThat(result.description()).isNull();
            assertThat(result.testCode()).isNull();
            assertThat(result.severity()).isNull();
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateArchUnitTestCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateArchUnitTestCommand command =
                    new UpdateArchUnitTestCommand(
                            1L,
                            "ARCH-001",
                            "테스트",
                            "설명",
                            "TestClass",
                            "testMethod",
                            "@Test void t() {}",
                            "MINOR");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ArchUnitTestId, ArchUnitTestUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.archUnitTestId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }

    @Nested
    @DisplayName("toStructureId 메서드")
    class ToStructureId {

        @Test
        @DisplayName("성공 - CreateCommand에서 PackageStructureId 추출")
        void toStructureId_WithValidCommand_ShouldReturnPackageStructureId() {
            // given
            CreateArchUnitTestCommand command =
                    new CreateArchUnitTestCommand(
                            5L,
                            "ARCH-001",
                            "테스트",
                            null,
                            "TestClass",
                            "testMethod",
                            "@Test void t() {}",
                            null);

            // when
            PackageStructureId result = sut.toStructureId(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("toCode 메서드")
    class ToCode {

        @Test
        @DisplayName("성공 - CreateCommand에서 코드 추출")
        void toCode_WithCreateCommand_ShouldReturnCode() {
            // given
            CreateArchUnitTestCommand command =
                    new CreateArchUnitTestCommand(
                            1L,
                            "ARCH-001",
                            "테스트",
                            null,
                            "TestClass",
                            "testMethod",
                            "@Test void t() {}",
                            null);

            // when
            String result = sut.toCode(command);

            // then
            assertThat(result).isEqualTo("ARCH-001");
        }

        @Test
        @DisplayName("성공 - UpdateCommand에서 코드 추출")
        void toCode_WithUpdateCommand_ShouldReturnCode() {
            // given
            UpdateArchUnitTestCommand command =
                    new UpdateArchUnitTestCommand(
                            1L, "ARCH-002", "테스트", null, "TestClass", "testMethod", null, null);

            // when
            String result = sut.toCode(command);

            // then
            assertThat(result).isEqualTo("ARCH-002");
        }
    }

    @Nested
    @DisplayName("toArchUnitTestId 메서드")
    class ToArchUnitTestId {

        @Test
        @DisplayName("성공 - UpdateCommand에서 ArchUnitTestId 추출")
        void toArchUnitTestId_WithValidCommand_ShouldReturnArchUnitTestId() {
            // given
            UpdateArchUnitTestCommand command =
                    new UpdateArchUnitTestCommand(
                            10L, "ARCH-001", "테스트", null, "TestClass", "testMethod", null, null);

            // when
            ArchUnitTestId result = sut.toArchUnitTestId(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(10L);
        }
    }
}
