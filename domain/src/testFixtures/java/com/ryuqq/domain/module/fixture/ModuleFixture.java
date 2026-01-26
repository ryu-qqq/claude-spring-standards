package com.ryuqq.domain.module.fixture;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.BuildIdentifier;
import com.ryuqq.domain.module.vo.ModuleDescription;
import com.ryuqq.domain.module.vo.ModuleName;
import com.ryuqq.domain.module.vo.ModulePath;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Module Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 Module 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ModuleFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private static final Long DEFAULT_LAYER_ID = 1L;

    private ModuleFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 Module Fixture (신규 생성, 루트 모듈) */
    public static Module defaultNewModule() {
        return Module.forNew(
                LayerId.of(DEFAULT_LAYER_ID),
                null,
                ModuleName.of("domain"),
                ModuleDescription.of("도메인 레이어 모듈"),
                ModulePath.of("domain"),
                BuildIdentifier.of(":domain"),
                FIXED_CLOCK.instant());
    }

    /** 기존 Module Fixture (저장된 상태, 루트 모듈) */
    public static Module defaultExistingModule() {
        Instant now = FIXED_CLOCK.instant();
        return Module.of(
                ModuleId.of(1L),
                LayerId.of(DEFAULT_LAYER_ID),
                null,
                ModuleName.of("domain"),
                ModuleDescription.of("도메인 레이어 모듈"),
                ModulePath.of("domain"),
                BuildIdentifier.of(":domain"),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 루트 모듈 (부모 없음) */
    public static Module rootModule() {
        return defaultExistingModule();
    }

    /** 자식 모듈 (부모 있음) */
    public static Module childModule() {
        Instant now = FIXED_CLOCK.instant();
        return Module.of(
                ModuleId.of(2L),
                LayerId.of(2L),
                ModuleId.of(1L),
                ModuleName.of("rest-api"),
                ModuleDescription.of("REST API 어댑터 모듈"),
                ModulePath.of("adapter-in/rest-api"),
                BuildIdentifier.of(":adapter-in:rest-api"),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 삭제된 Module */
    public static Module deletedModule() {
        Instant now = FIXED_CLOCK.instant();
        return Module.of(
                ModuleId.of(3L),
                LayerId.of(DEFAULT_LAYER_ID),
                null,
                ModuleName.of("deleted-module"),
                ModuleDescription.of("삭제된 모듈"),
                ModulePath.of("deleted-module"),
                BuildIdentifier.of(":deleted-module"),
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /** 특정 Layer의 Module */
    public static Module moduleWithLayerId(Long layerId) {
        Instant now = FIXED_CLOCK.instant();
        return Module.of(
                ModuleId.of(4L),
                LayerId.of(layerId),
                null,
                ModuleName.of("layer-module"),
                ModuleDescription.of("레이어 모듈"),
                ModulePath.of("layer-module"),
                BuildIdentifier.of(":layer-module"),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 설명이 없는 Module */
    public static Module moduleWithoutDescription() {
        Instant now = FIXED_CLOCK.instant();
        return Module.of(
                ModuleId.of(5L),
                LayerId.of(DEFAULT_LAYER_ID),
                null,
                ModuleName.of("no-description"),
                ModuleDescription.empty(),
                ModulePath.of("no-description"),
                BuildIdentifier.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 커스텀 Module 생성 */
    public static Module customModule(
            Long id,
            Long layerId,
            Long parentModuleId,
            String name,
            String description,
            String modulePath,
            String buildIdentifier,
            Instant createdAt,
            Instant updatedAt) {
        return Module.of(
                ModuleId.of(id),
                LayerId.of(layerId),
                parentModuleId != null ? ModuleId.of(parentModuleId) : null,
                ModuleName.of(name),
                description != null ? ModuleDescription.of(description) : ModuleDescription.empty(),
                ModulePath.of(modulePath),
                buildIdentifier != null
                        ? BuildIdentifier.of(buildIdentifier)
                        : BuildIdentifier.empty(),
                DeletionStatus.active(),
                createdAt,
                updatedAt);
    }

    /** 특정 부모를 가진 Module 생성 */
    public static Module moduleWithParent(Long parentModuleId) {
        Instant now = FIXED_CLOCK.instant();
        return Module.of(
                ModuleId.of(6L),
                LayerId.of(2L),
                ModuleId.of(parentModuleId),
                ModuleName.of("child-module"),
                ModuleDescription.of("자식 모듈"),
                ModulePath.of("parent/child"),
                BuildIdentifier.of(":parent:child"),
                DeletionStatus.active(),
                now,
                now);
    }
}
