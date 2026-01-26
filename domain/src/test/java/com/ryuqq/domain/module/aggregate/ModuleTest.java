package com.ryuqq.domain.module.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.fixture.ModuleFixture;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.BuildIdentifier;
import com.ryuqq.domain.module.vo.ModuleDescription;
import com.ryuqq.domain.module.vo.ModuleName;
import com.ryuqq.domain.module.vo.ModulePath;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Module Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("Module Aggregate")
class ModuleTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private static final Long DEFAULT_LAYER_ID = 1L;

    @Nested
    @DisplayName("생성")
    class CreateModule {

        @Test
        @DisplayName("신규 Module 생성 성공 (루트 모듈)")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            LayerId layerId = LayerId.of(DEFAULT_LAYER_ID);
            ModuleId parentModuleId = null;
            ModuleName name = ModuleName.of("domain");
            ModuleDescription description = ModuleDescription.of("도메인 레이어 모듈");
            ModulePath modulePath = ModulePath.of("domain");
            BuildIdentifier buildIdentifier = BuildIdentifier.of(":domain");
            Instant now = FIXED_CLOCK.instant();

            // when
            Module module =
                    Module.forNew(
                            layerId,
                            parentModuleId,
                            name,
                            description,
                            modulePath,
                            buildIdentifier,
                            now);

            // then
            assertThat(module.isNew()).isTrue();
            assertThat(module.layerId()).isEqualTo(layerId);
            assertThat(module.parentModuleId()).isNull();
            assertThat(module.name()).isEqualTo(name);
            assertThat(module.description()).isEqualTo(description);
            assertThat(module.modulePath()).isEqualTo(modulePath);
            assertThat(module.buildIdentifier()).isEqualTo(buildIdentifier);
            assertThat(module.deletionStatus().isDeleted()).isFalse();
            assertThat(module.createdAt()).isEqualTo(now);
            assertThat(module.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 Module 생성 성공 (자식 모듈)")
        void forNew_WithParentModule_ShouldSucceed() {
            // given
            LayerId layerId = LayerId.of(2L);
            ModuleId parentModuleId = ModuleId.of(1L);
            ModuleName name = ModuleName.of("rest-api");
            ModuleDescription description = ModuleDescription.of("REST API 어댑터 모듈");
            ModulePath modulePath = ModulePath.of("adapter-in/rest-api");
            BuildIdentifier buildIdentifier = BuildIdentifier.of(":adapter-in:rest-api");
            Instant now = FIXED_CLOCK.instant();

            // when
            Module module =
                    Module.forNew(
                            layerId,
                            parentModuleId,
                            name,
                            description,
                            modulePath,
                            buildIdentifier,
                            now);

            // then
            assertThat(module.parentModuleId()).isEqualTo(parentModuleId);
            assertThat(module.hasParent()).isTrue();
            assertThat(module.isRoot()).isFalse();
        }

        @Test
        @DisplayName("신규 Module은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            Module module = ModuleFixture.defaultNewModule();

            // then
            assertThat(module.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("설명이 null이면 빈 설명 사용")
        void forNew_WithNullDescription_ShouldUseEmptyDescription() {
            // given
            LayerId layerId = LayerId.of(DEFAULT_LAYER_ID);
            ModuleName name = ModuleName.of("no-description");
            ModuleDescription nullDescription = ModuleDescription.empty();
            ModulePath modulePath = ModulePath.of("no-description");
            BuildIdentifier buildIdentifier = BuildIdentifier.of(":no-description");
            Instant now = FIXED_CLOCK.instant();

            // when
            Module module =
                    Module.forNew(
                            layerId, null, name, nullDescription, modulePath, buildIdentifier, now);

            // then
            assertThat(module.description().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            Module module = ModuleFixture.defaultNewModule();
            ModuleId id = ModuleId.of(1L);

            // when
            module.assignId(id);

            // then
            assertThat(module.id()).isEqualTo(id);
            assertThat(module.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            Module module = ModuleFixture.defaultExistingModule();
            ModuleId newId = ModuleId.of(2L);

            // when & then
            assertThatThrownBy(() -> module.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateModule {

        @Test
        @DisplayName("Module 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            Module module = ModuleFixture.defaultExistingModule();
            ModuleUpdateData updateData =
                    new ModuleUpdateData(
                            ModuleId.of(2L),
                            ModuleName.of("updated-domain"),
                            ModuleDescription.of("업데이트된 설명"),
                            ModulePath.of("updated-domain"),
                            BuildIdentifier.of(":updated-domain"));
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            module.update(updateData, updateTime);

            // then
            assertThat(module.parentModuleId()).isEqualTo(updateData.parentModuleId());
            assertThat(module.name()).isEqualTo(updateData.name());
            assertThat(module.description()).isEqualTo(updateData.description());
            assertThat(module.modulePath()).isEqualTo(updateData.modulePath());
            assertThat(module.buildIdentifier()).isEqualTo(updateData.buildIdentifier());
            assertThat(module.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("부모 모듈을 null로 변경하여 루트 모듈로 변경")
        void update_WithNullParent_ShouldBecomeRoot() {
            // given
            Module module = ModuleFixture.childModule();
            ModuleUpdateData updateData =
                    new ModuleUpdateData(
                            null,
                            ModuleName.of("root-module"),
                            ModuleDescription.of("루트 모듈"),
                            ModulePath.of("root-module"),
                            BuildIdentifier.of(":root-module"));
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            module.update(updateData, updateTime);

            // then
            assertThat(module.isRoot()).isTrue();
            assertThat(module.hasParent()).isFalse();
        }
    }

    @Nested
    @DisplayName("계층 구조")
    class Hierarchy {

        @Test
        @DisplayName("루트 모듈 여부 확인")
        void isRoot_WhenNoParent_ShouldReturnTrue() {
            // given
            Module rootModule = ModuleFixture.rootModule();
            Module childModule = ModuleFixture.childModule();

            // when & then
            assertThat(rootModule.isRoot()).isTrue();
            assertThat(childModule.isRoot()).isFalse();
        }

        @Test
        @DisplayName("부모 모듈 여부 확인")
        void hasParent_WhenHasParent_ShouldReturnTrue() {
            // given
            Module rootModule = ModuleFixture.rootModule();
            Module childModule = ModuleFixture.childModule();

            // when & then
            assertThat(rootModule.hasParent()).isFalse();
            assertThat(childModule.hasParent()).isTrue();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteModule {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            Module module = ModuleFixture.defaultExistingModule();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            module.delete(deleteTime);

            // then
            assertThat(module.isDeleted()).isTrue();
            assertThat(module.deletionStatus().isDeleted()).isTrue();
            assertThat(module.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(module.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 Module 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            Module module = ModuleFixture.deletedModule();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            module.restore(restoreTime);

            // then
            assertThat(module.isDeleted()).isFalse();
            assertThat(module.deletionStatus().isActive()).isTrue();
            assertThat(module.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            Module activeModule = ModuleFixture.defaultExistingModule();
            Module deletedModule = ModuleFixture.deletedModule();

            // when & then
            assertThat(activeModule.isDeleted()).isFalse();
            assertThat(deletedModule.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteModule {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            ModuleId id = ModuleId.of(1L);
            LayerId layerId = LayerId.of(DEFAULT_LAYER_ID);
            ModuleId parentModuleId = null;
            ModuleName name = ModuleName.of("domain");
            ModuleDescription description = ModuleDescription.of("도메인 레이어 모듈");
            ModulePath modulePath = ModulePath.of("domain");
            BuildIdentifier buildIdentifier = BuildIdentifier.of(":domain");
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            Module module =
                    Module.reconstitute(
                            id,
                            layerId,
                            parentModuleId,
                            name,
                            description,
                            modulePath,
                            buildIdentifier,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(module.id()).isEqualTo(id);
            assertThat(module.layerId()).isEqualTo(layerId);
            assertThat(module.parentModuleId()).isEqualTo(parentModuleId);
            assertThat(module.name()).isEqualTo(name);
            assertThat(module.description()).isEqualTo(description);
            assertThat(module.modulePath()).isEqualTo(modulePath);
            assertThat(module.buildIdentifier()).isEqualTo(buildIdentifier);
            assertThat(module.createdAt()).isEqualTo(createdAt);
            assertThat(module.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("레이어별 생성")
    class LayerCreation {

        @Test
        @DisplayName("특정 Layer의 Module 생성")
        void forNew_WithLayerId_ShouldSucceed() {
            // given
            Long layerIdValue = 3L;

            // when
            Module module = ModuleFixture.moduleWithLayerId(layerIdValue);

            // then
            assertThat(module.layerIdValue()).isEqualTo(layerIdValue);
        }
    }
}
