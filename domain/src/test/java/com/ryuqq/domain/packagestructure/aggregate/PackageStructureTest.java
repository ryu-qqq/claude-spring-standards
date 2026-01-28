package com.ryuqq.domain.packagestructure.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.fixture.PackageStructureFixture;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PackageStructure Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("PackageStructure Aggregate")
class PackageStructureTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreatePackageStructure {

        @Test
        @DisplayName("신규 PackageStructure 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            PathPattern pathPattern = PathPattern.of("com.example.domain.{bc}.aggregate");
            String description = "기본 설명";
            Instant now = FIXED_CLOCK.instant();

            // when
            PackageStructure packageStructure =
                    PackageStructure.forNew(moduleId, pathPattern, description, now);

            // then
            assertThat(packageStructure.isNew()).isTrue();
            assertThat(packageStructure.moduleId()).isEqualTo(moduleId);
            assertThat(packageStructure.pathPattern()).isEqualTo(pathPattern);
            assertThat(packageStructure.description()).isEqualTo(description);
            assertThat(packageStructure.deletionStatus().isDeleted()).isFalse();
            assertThat(packageStructure.createdAt()).isEqualTo(now);
            assertThat(packageStructure.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 PackageStructure는 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            PackageStructure packageStructure = PackageStructureFixture.forNew();

            // then
            assertThat(packageStructure.id().isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            PackageStructure packageStructure = PackageStructureFixture.forNew();
            PackageStructureId id = PackageStructureFixture.nextPackageStructureId();

            // when
            packageStructure.assignId(id);

            // then
            assertThat(packageStructure.id()).isEqualTo(id);
            assertThat(packageStructure.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            PackageStructure packageStructure =
                    PackageStructureFixture.defaultExistingPackageStructure();
            PackageStructureId newId = PackageStructureFixture.nextPackageStructureId();

            // when & then
            assertThatThrownBy(() -> packageStructure.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeletePackageStructure {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            PackageStructure packageStructure =
                    PackageStructureFixture.defaultExistingPackageStructure();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            packageStructure.delete(deleteTime);

            // then
            assertThat(packageStructure.isDeleted()).isTrue();
            assertThat(packageStructure.deletionStatus().isDeleted()).isTrue();
            assertThat(packageStructure.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(packageStructure.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 PackageStructure 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            PackageStructure packageStructure = PackageStructureFixture.deletedPackageStructure();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            packageStructure.restore(restoreTime);

            // then
            assertThat(packageStructure.isDeleted()).isFalse();
            assertThat(packageStructure.deletionStatus().isActive()).isTrue();
            assertThat(packageStructure.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            PackageStructure activePackageStructure =
                    PackageStructureFixture.defaultExistingPackageStructure();
            PackageStructure deletedPackageStructure =
                    PackageStructureFixture.deletedPackageStructure();

            // when & then
            assertThat(activePackageStructure.isDeleted()).isFalse();
            assertThat(deletedPackageStructure.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstitutePackageStructure {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            PackageStructureId id = PackageStructureFixture.nextPackageStructureId();
            ModuleId moduleId = ModuleId.of(1L);
            PathPattern pathPattern = PathPattern.of("com.example.domain.{bc}.aggregate");
            String description = "기본 설명";
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            PackageStructure packageStructure =
                    PackageStructure.reconstitute(
                            id,
                            moduleId,
                            pathPattern,
                            description,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(packageStructure.id()).isEqualTo(id);
            assertThat(packageStructure.moduleId()).isEqualTo(moduleId);
            assertThat(packageStructure.pathPattern()).isEqualTo(pathPattern);
            assertThat(packageStructure.description()).isEqualTo(description);
            assertThat(packageStructure.createdAt()).isEqualTo(createdAt);
            assertThat(packageStructure.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
