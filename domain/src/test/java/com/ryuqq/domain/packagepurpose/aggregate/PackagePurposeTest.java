package com.ryuqq.domain.packagepurpose.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagepurpose.fixture.PackagePurposeFixture;
import com.ryuqq.domain.packagepurpose.fixture.PackagePurposeVoFixtures;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagepurpose.vo.PurposeName;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PackagePurpose Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("PackagePurpose Aggregate")
class PackagePurposeTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreatePackagePurpose {

        @Test
        @DisplayName("신규 PackagePurpose 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            PackageStructureId structureId = PackagePurposeVoFixtures.defaultStructureId();
            PurposeCode code = PackagePurposeVoFixtures.defaultPurposeCode();
            PurposeName name = PackagePurposeVoFixtures.defaultPurposeName();
            String description = "기본 설명";
            Instant now = FIXED_CLOCK.instant();

            // when
            PackagePurpose packagePurpose =
                    PackagePurpose.forNew(structureId, code, name, description, now);

            // then
            assertThat(packagePurpose.isNew()).isTrue();
            assertThat(packagePurpose.structureId()).isEqualTo(structureId);
            assertThat(packagePurpose.code()).isEqualTo(code);
            assertThat(packagePurpose.name()).isEqualTo(name);
            assertThat(packagePurpose.description()).isEqualTo(description);
            assertThat(packagePurpose.deletionStatus().isDeleted()).isFalse();
            assertThat(packagePurpose.createdAt()).isEqualTo(now);
            assertThat(packagePurpose.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 PackagePurpose는 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            PackagePurpose packagePurpose = PackagePurposeFixture.forNew();

            // then
            assertThat(packagePurpose.id().isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            PackagePurpose packagePurpose = PackagePurposeFixture.forNew();
            PackagePurposeId id = PackagePurposeVoFixtures.nextPackagePurposeId();

            // when
            packagePurpose.assignId(id);

            // then
            assertThat(packagePurpose.id()).isEqualTo(id);
            assertThat(packagePurpose.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            PackagePurpose packagePurpose = PackagePurposeFixture.defaultExistingPackagePurpose();
            PackagePurposeId newId = PackagePurposeVoFixtures.nextPackagePurposeId();

            // when & then
            assertThatThrownBy(() -> packagePurpose.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdatePackagePurpose {

        @Test
        @DisplayName("PackagePurpose 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            PackagePurpose packagePurpose = PackagePurposeFixture.defaultExistingPackagePurpose();
            PurposeCode newCode = PurposeCode.of("UPDATED_CODE");
            PurposeName newName = PurposeName.of("업데이트된 이름");
            String newDescription = "업데이트된 설명";
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            PackagePurposeUpdateData updateData =
                    new PackagePurposeUpdateData(newCode, newName, newDescription);
            packagePurpose.update(updateData, updateTime);

            // then
            assertThat(packagePurpose.code()).isEqualTo(newCode);
            assertThat(packagePurpose.name()).isEqualTo(newName);
            assertThat(packagePurpose.description()).isEqualTo(newDescription);
            assertThat(packagePurpose.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("null description으로 수정 성공")
        void update_WithNullDescription_ShouldSucceed() {
            // given
            PackagePurpose packagePurpose = PackagePurposeFixture.defaultExistingPackagePurpose();
            PurposeCode newCode = PurposeCode.of("NEW_CODE");
            PurposeName newName = PurposeName.of("새 이름");
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            PackagePurposeUpdateData updateData =
                    new PackagePurposeUpdateData(newCode, newName, null);
            packagePurpose.update(updateData, updateTime);

            // then
            assertThat(packagePurpose.code()).isEqualTo(newCode);
            assertThat(packagePurpose.name()).isEqualTo(newName);
            assertThat(packagePurpose.description()).isNull();
            assertThat(packagePurpose.updatedAt()).isEqualTo(updateTime);
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeletePackagePurpose {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            PackagePurpose packagePurpose = PackagePurposeFixture.defaultExistingPackagePurpose();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            packagePurpose.delete(deleteTime);

            // then
            assertThat(packagePurpose.isDeleted()).isTrue();
            assertThat(packagePurpose.deletionStatus().isDeleted()).isTrue();
            assertThat(packagePurpose.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(packagePurpose.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 PackagePurpose 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            PackagePurpose packagePurpose = PackagePurposeFixture.deletedPackagePurpose();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            packagePurpose.restore(restoreTime);

            // then
            assertThat(packagePurpose.isDeleted()).isFalse();
            assertThat(packagePurpose.deletionStatus().isActive()).isTrue();
            assertThat(packagePurpose.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            PackagePurpose activePackagePurpose =
                    PackagePurposeFixture.defaultExistingPackagePurpose();
            PackagePurpose deletedPackagePurpose = PackagePurposeFixture.deletedPackagePurpose();

            // when & then
            assertThat(activePackagePurpose.isDeleted()).isFalse();
            assertThat(deletedPackagePurpose.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstitutePackagePurpose {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            PackagePurposeId id = PackagePurposeVoFixtures.nextPackagePurposeId();
            PackageStructureId structureId = PackagePurposeVoFixtures.defaultStructureId();
            PurposeCode code = PackagePurposeVoFixtures.defaultPurposeCode();
            PurposeName name = PackagePurposeVoFixtures.defaultPurposeName();
            String description = "기본 설명";
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            PackagePurpose packagePurpose =
                    PackagePurpose.reconstitute(
                            id,
                            structureId,
                            code,
                            name,
                            description,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(packagePurpose.id()).isEqualTo(id);
            assertThat(packagePurpose.structureId()).isEqualTo(structureId);
            assertThat(packagePurpose.code()).isEqualTo(code);
            assertThat(packagePurpose.name()).isEqualTo(name);
            assertThat(packagePurpose.description()).isEqualTo(description);
            assertThat(packagePurpose.createdAt()).isEqualTo(createdAt);
            assertThat(packagePurpose.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
