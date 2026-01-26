package com.ryuqq.domain.convention.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.fixture.ConventionFixture;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Convention Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("Convention Aggregate")
class ConventionTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateConvention {

        @Test
        @DisplayName("신규 Convention 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            ConventionVersion version = ConventionVersion.of("1.0.0");
            String description = "도메인 모듈 코딩 컨벤션";
            Instant now = FIXED_CLOCK.instant();

            // when
            Convention convention = Convention.forNew(moduleId, version, description, now);

            // then
            assertThat(convention.isNew()).isTrue();
            assertThat(convention.moduleId()).isEqualTo(moduleId);
            assertThat(convention.version()).isEqualTo(version);
            assertThat(convention.description()).isEqualTo(description);
            assertThat(convention.isActive()).isTrue();
            assertThat(convention.deletionStatus().isDeleted()).isFalse();
            assertThat(convention.createdAt()).isEqualTo(now);
            assertThat(convention.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 Convention은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            Convention convention = ConventionFixture.defaultNewConvention();

            // then
            assertThat(convention.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("버전이 null이면 기본 버전 사용")
        void forNew_WithNullVersion_ShouldUseDefaultVersion() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            ConventionVersion nullVersion = ConventionVersion.of(null);
            String description = "도메인 모듈 코딩 컨벤션";
            Instant now = FIXED_CLOCK.instant();

            // when
            Convention convention = Convention.forNew(moduleId, nullVersion, description, now);

            // then
            assertThat(convention.version().value()).isEqualTo("1.0.0");
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            Convention convention = ConventionFixture.defaultNewConvention();
            ConventionId id = ConventionId.of(1L);

            // when
            convention.assignId(id);

            // then
            assertThat(convention.id()).isEqualTo(id);
            assertThat(convention.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            Convention convention = ConventionFixture.defaultExistingConvention();
            ConventionId newId = ConventionId.of(2L);

            // when & then
            assertThatThrownBy(() -> convention.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("상태 변경")
    class StatusChange {

        @Test
        @DisplayName("비활성화 성공")
        void deactivate_ShouldChangeToInactive() {
            // given
            Convention convention = ConventionFixture.defaultExistingConvention();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            convention.deactivate(updateTime);

            // then
            assertThat(convention.isActive()).isFalse();
            assertThat(convention.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("활성화 성공")
        void activate_ShouldChangeToActive() {
            // given
            Convention convention = ConventionFixture.inactiveConvention();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            convention.activate(updateTime);

            // then
            assertThat(convention.isActive()).isTrue();
            assertThat(convention.updatedAt()).isEqualTo(updateTime);
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteConvention {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            Convention convention = ConventionFixture.defaultExistingConvention();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            convention.delete(deleteTime);

            // then
            assertThat(convention.isDeleted()).isTrue();
            assertThat(convention.deletionStatus().isDeleted()).isTrue();
            assertThat(convention.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(convention.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 Convention 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            Convention convention = ConventionFixture.deletedConvention();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            convention.restore(restoreTime);

            // then
            assertThat(convention.isDeleted()).isFalse();
            assertThat(convention.deletionStatus().isActive()).isTrue();
            assertThat(convention.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            Convention activeConvention = ConventionFixture.defaultExistingConvention();
            Convention deletedConvention = ConventionFixture.deletedConvention();

            // when & then
            assertThat(activeConvention.isDeleted()).isFalse();
            assertThat(deletedConvention.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteConvention {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            ConventionId id = ConventionId.of(1L);
            ModuleId moduleId = ModuleId.of(1L);
            ConventionVersion version = ConventionVersion.of("1.0.0");
            String description = "도메인 모듈 코딩 컨벤션";
            boolean active = true;
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            Convention convention =
                    Convention.reconstitute(
                            id,
                            moduleId,
                            version,
                            description,
                            active,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(convention.id()).isEqualTo(id);
            assertThat(convention.moduleId()).isEqualTo(moduleId);
            assertThat(convention.version()).isEqualTo(version);
            assertThat(convention.description()).isEqualTo(description);
            assertThat(convention.isActive()).isEqualTo(active);
            assertThat(convention.createdAt()).isEqualTo(createdAt);
            assertThat(convention.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("모듈별 생성")
    class ModuleCreation {

        @Test
        @DisplayName("DOMAIN 모듈 ID Convention 생성")
        void forNew_WithDomainModuleId_ShouldSucceed() {
            // when
            Convention convention =
                    ConventionFixture.conventionWithModuleId(ConventionFixture.defaultModuleId());

            // then
            assertThat(convention.moduleId().value())
                    .isEqualTo(ConventionFixture.defaultModuleId());
        }

        @Test
        @DisplayName("APPLICATION 모듈 ID Convention 생성")
        void forNew_WithApplicationModuleId_ShouldSucceed() {
            // when
            Convention convention =
                    ConventionFixture.conventionWithModuleId(
                            ConventionFixture.applicationModuleId());

            // then
            assertThat(convention.moduleId().value())
                    .isEqualTo(ConventionFixture.applicationModuleId());
        }

        @Test
        @DisplayName("REST_API 모듈 ID Convention 생성")
        void forNew_WithRestApiModuleId_ShouldSucceed() {
            // when
            Convention convention =
                    ConventionFixture.conventionWithModuleId(ConventionFixture.restApiModuleId());

            // then
            assertThat(convention.moduleId().value())
                    .isEqualTo(ConventionFixture.restApiModuleId());
        }
    }

    @Nested
    @DisplayName("위임 메서드")
    class DelegationMethods {

        @Test
        @DisplayName("moduleIdValue 위임 메서드")
        void moduleIdValue_ShouldReturnPrimitiveValue() {
            // given
            Convention convention = ConventionFixture.defaultExistingConvention();

            // when
            Long moduleIdValue = convention.moduleIdValue();

            // then
            assertThat(moduleIdValue).isEqualTo(ConventionFixture.defaultModuleId());
        }

        @Test
        @DisplayName("versionValue 위임 메서드")
        void versionValue_ShouldReturnStringValue() {
            // given
            Convention convention = ConventionFixture.defaultExistingConvention();

            // when
            String versionValue = convention.versionValue();

            // then
            assertThat(versionValue).isEqualTo("1.0.0");
        }
    }
}
