package com.ryuqq.domain.archunittest.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.archunittest.fixture.ArchUnitTestFixture;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestDescription;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestName;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.archunittest.vo.TestCode;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ArchUnitTest Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("ArchUnitTest Aggregate")
class ArchUnitTestTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateArchUnitTest {

        @Test
        @DisplayName("신규 ArchUnitTest 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            String code = "ARCH-001";
            ArchUnitTestName name = ArchUnitTestName.of("Lombok 금지 검증");
            ArchUnitTestDescription description =
                    ArchUnitTestDescription.of("Domain 레이어에서 Lombok 사용 금지");
            String testClassName = "DomainArchTest";
            String testMethodName = "lombok_should_not_be_used";
            TestCode testCode = TestCode.of("@ArchTest\nstatic final ArchRule test = ...");
            ArchUnitTestSeverity severity = ArchUnitTestSeverity.BLOCKER;
            Instant now = FIXED_CLOCK.instant();

            // when
            ArchUnitTest archUnitTest =
                    ArchUnitTest.forNew(
                            structureId,
                            code,
                            name,
                            description,
                            testClassName,
                            testMethodName,
                            testCode,
                            severity,
                            now);

            // then
            assertThat(archUnitTest.isNew()).isTrue();
            assertThat(archUnitTest.structureId()).isEqualTo(structureId);
            assertThat(archUnitTest.code()).isEqualTo(code);
            assertThat(archUnitTest.name()).isEqualTo(name);
            assertThat(archUnitTest.description()).isEqualTo(description);
            assertThat(archUnitTest.testClassName()).isEqualTo(testClassName);
            assertThat(archUnitTest.testMethodName()).isEqualTo(testMethodName);
            assertThat(archUnitTest.testCode()).isEqualTo(testCode);
            assertThat(archUnitTest.severity()).isEqualTo(severity);
            assertThat(archUnitTest.deletionStatus().isDeleted()).isFalse();
            assertThat(archUnitTest.createdAt()).isEqualTo(now);
            assertThat(archUnitTest.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 ArchUnitTest는 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            ArchUnitTest archUnitTest = ArchUnitTestFixture.defaultNewArchUnitTest();

            // then
            assertThat(archUnitTest.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("structureId가 null이면 예외 발생")
        void forNew_WithNullStructureId_ShouldThrow() {
            // given
            String code = "ARCH-001";
            ArchUnitTestName name = ArchUnitTestName.of("Test");
            TestCode testCode = TestCode.of("@ArchTest\nstatic final ArchRule test = ...");
            Instant now = FIXED_CLOCK.instant();

            // when & then
            assertThatThrownBy(
                            () ->
                                    ArchUnitTest.forNew(
                                            null, code, name, null, null, null, testCode, null,
                                            now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("structureId must not be null");
        }

        @Test
        @DisplayName("code가 null이면 예외 발생")
        void forNew_WithNullCode_ShouldThrow() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            ArchUnitTestName name = ArchUnitTestName.of("Test");
            TestCode testCode = TestCode.of("@ArchTest\nstatic final ArchRule test = ...");
            Instant now = FIXED_CLOCK.instant();

            // when & then
            assertThatThrownBy(
                            () ->
                                    ArchUnitTest.forNew(
                                            structureId,
                                            null,
                                            name,
                                            null,
                                            null,
                                            null,
                                            testCode,
                                            null,
                                            now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("code must not be null or blank");
        }

        @Test
        @DisplayName("testCode가 null이면 예외 발생")
        void forNew_WithNullTestCode_ShouldThrow() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            String code = "ARCH-001";
            ArchUnitTestName name = ArchUnitTestName.of("Test");
            Instant now = FIXED_CLOCK.instant();

            // when & then
            assertThatThrownBy(
                            () ->
                                    ArchUnitTest.forNew(
                                            structureId,
                                            code,
                                            name,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("testCode must not be null");
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            ArchUnitTest archUnitTest = ArchUnitTestFixture.defaultNewArchUnitTest();
            ArchUnitTestId id = ArchUnitTestId.of(1L);

            // when
            archUnitTest.assignId(id);

            // then
            assertThat(archUnitTest.id()).isEqualTo(id);
            assertThat(archUnitTest.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            ArchUnitTest archUnitTest = ArchUnitTestFixture.defaultExistingArchUnitTest();
            ArchUnitTestId newId = ArchUnitTestId.of(2L);

            // when & then
            assertThatThrownBy(() -> archUnitTest.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateArchUnitTest {

        @Test
        @DisplayName("ArchUnitTest 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            ArchUnitTest archUnitTest = ArchUnitTestFixture.defaultExistingArchUnitTest();
            ArchUnitTestUpdateData updateData =
                    ArchUnitTestUpdateData.builder()
                            .code("ARCH-002")
                            .name(ArchUnitTestName.of("업데이트된 테스트"))
                            .description(ArchUnitTestDescription.of("업데이트된 설명"))
                            .testCode(TestCode.of("@ArchTest\nstatic final ArchRule updated = ..."))
                            .severity(ArchUnitTestSeverity.INFO)
                            .build();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            archUnitTest.update(updateData, updateTime);

            // then
            assertThat(archUnitTest.code()).isEqualTo(updateData.code());
            assertThat(archUnitTest.name()).isEqualTo(updateData.name());
            assertThat(archUnitTest.description()).isEqualTo(updateData.description());
            assertThat(archUnitTest.testCode()).isEqualTo(updateData.testCode());
            assertThat(archUnitTest.severity()).isEqualTo(updateData.severity());
            assertThat(archUnitTest.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("null 필드는 기존 값 유지")
        void update_WithNullFields_ShouldKeepExistingValues() {
            // given
            ArchUnitTest archUnitTest = ArchUnitTestFixture.defaultExistingArchUnitTest();
            String originalCode = archUnitTest.code();
            ArchUnitTestName originalName = archUnitTest.name();
            ArchUnitTestUpdateData updateData =
                    ArchUnitTestUpdateData.builder()
                            .testCode(TestCode.of("@ArchTest\nstatic final ArchRule updated = ..."))
                            .build();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            archUnitTest.update(updateData, updateTime);

            // then
            assertThat(archUnitTest.code()).isEqualTo(originalCode);
            assertThat(archUnitTest.name()).isEqualTo(originalName);
            assertThat(archUnitTest.testCode()).isEqualTo(updateData.testCode());
        }
    }

    @Nested
    @DisplayName("Helper 메서드")
    class HelperMethods {

        @Test
        @DisplayName("테스트 클래스명 여부 확인")
        void hasTestClassName_ShouldReturnCorrectStatus() {
            // given
            ArchUnitTest withClassName = ArchUnitTestFixture.defaultExistingArchUnitTest();
            ArchUnitTest withoutClassName = ArchUnitTestFixture.archUnitTestWithoutTestNames();

            // when & then
            assertThat(withClassName.hasTestClassName()).isTrue();
            assertThat(withoutClassName.hasTestClassName()).isFalse();
        }

        @Test
        @DisplayName("테스트 메서드명 여부 확인")
        void hasTestMethodName_ShouldReturnCorrectStatus() {
            // given
            ArchUnitTest withMethodName = ArchUnitTestFixture.defaultExistingArchUnitTest();
            ArchUnitTest withoutMethodName = ArchUnitTestFixture.archUnitTestWithoutTestNames();

            // when & then
            assertThat(withMethodName.hasTestMethodName()).isTrue();
            assertThat(withoutMethodName.hasTestMethodName()).isFalse();
        }

        @Test
        @DisplayName("심각도 여부 확인")
        void hasSeverity_ShouldReturnCorrectStatus() {
            // given
            ArchUnitTest withSeverity = ArchUnitTestFixture.defaultExistingArchUnitTest();
            ArchUnitTest withoutSeverity = ArchUnitTestFixture.archUnitTestWithoutSeverity();

            // when & then
            assertThat(withSeverity.hasSeverity()).isTrue();
            assertThat(withoutSeverity.hasSeverity()).isFalse();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteArchUnitTest {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            ArchUnitTest archUnitTest = ArchUnitTestFixture.defaultExistingArchUnitTest();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            archUnitTest.delete(deleteTime);

            // then
            assertThat(archUnitTest.isDeleted()).isTrue();
            assertThat(archUnitTest.deletionStatus().isDeleted()).isTrue();
            assertThat(archUnitTest.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(archUnitTest.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 ArchUnitTest 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            ArchUnitTest archUnitTest = ArchUnitTestFixture.deletedArchUnitTest();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            archUnitTest.restore(restoreTime);

            // then
            assertThat(archUnitTest.isDeleted()).isFalse();
            assertThat(archUnitTest.deletionStatus().isActive()).isTrue();
            assertThat(archUnitTest.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            ArchUnitTest activeTest = ArchUnitTestFixture.defaultExistingArchUnitTest();
            ArchUnitTest deletedTest = ArchUnitTestFixture.deletedArchUnitTest();

            // when & then
            assertThat(activeTest.isDeleted()).isFalse();
            assertThat(deletedTest.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteArchUnitTest {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            ArchUnitTestId id = ArchUnitTestId.of(1L);
            PackageStructureId structureId = PackageStructureId.of(1L);
            String code = "ARCH-001";
            ArchUnitTestName name = ArchUnitTestName.of("Lombok 금지 검증");
            ArchUnitTestDescription description =
                    ArchUnitTestDescription.of("Domain 레이어에서 Lombok 사용 금지");
            String testClassName = "DomainArchTest";
            String testMethodName = "lombok_should_not_be_used";
            TestCode testCode = TestCode.of("@ArchTest\nstatic final ArchRule test = ...");
            ArchUnitTestSeverity severity = ArchUnitTestSeverity.BLOCKER;
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            ArchUnitTest archUnitTest =
                    ArchUnitTest.reconstitute(
                            id,
                            structureId,
                            code,
                            name,
                            description,
                            testClassName,
                            testMethodName,
                            testCode,
                            severity,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(archUnitTest.id()).isEqualTo(id);
            assertThat(archUnitTest.structureId()).isEqualTo(structureId);
            assertThat(archUnitTest.code()).isEqualTo(code);
            assertThat(archUnitTest.name()).isEqualTo(name);
            assertThat(archUnitTest.createdAt()).isEqualTo(createdAt);
            assertThat(archUnitTest.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
