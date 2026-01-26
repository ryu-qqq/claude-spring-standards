package com.ryuqq.domain.convention.fixture;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Convention Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 Convention 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ConventionFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    /** 기본 Module ID (domain 모듈 역할) */
    private static final Long DEFAULT_MODULE_ID = 1L;

    /** application 모듈 역할의 Module ID */
    private static final Long APPLICATION_MODULE_ID = 2L;

    /** persistence-mysql 모듈 역할의 Module ID */
    private static final Long PERSISTENCE_MODULE_ID = 3L;

    /** rest-api 모듈 역할의 Module ID */
    private static final Long REST_API_MODULE_ID = 4L;

    private ConventionFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 Convention Fixture (신규 생성) */
    public static Convention defaultNewConvention() {
        return Convention.forNew(
                ModuleId.of(DEFAULT_MODULE_ID),
                ConventionVersion.of("1.0.0"),
                "도메인 모듈 코딩 컨벤션",
                FIXED_CLOCK.instant());
    }

    /** 기존 Convention Fixture (저장된 상태) */
    public static Convention defaultExistingConvention() {
        Instant now = FIXED_CLOCK.instant();
        return Convention.of(
                ConventionId.of(1L),
                ModuleId.of(DEFAULT_MODULE_ID),
                ConventionVersion.of("1.0.0"),
                "도메인 모듈 코딩 컨벤션",
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 활성 상태 Convention */
    public static Convention activeConvention() {
        return defaultExistingConvention();
    }

    /** 비활성 상태 Convention */
    public static Convention inactiveConvention() {
        Instant now = FIXED_CLOCK.instant();
        return Convention.of(
                ConventionId.of(2L),
                ModuleId.of(APPLICATION_MODULE_ID),
                ConventionVersion.of("1.0.0"),
                "애플리케이션 모듈 코딩 컨벤션",
                false,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 삭제된 Convention */
    public static Convention deletedConvention() {
        Instant now = FIXED_CLOCK.instant();
        return Convention.of(
                ConventionId.of(3L),
                ModuleId.of(PERSISTENCE_MODULE_ID),
                ConventionVersion.of("1.0.0"),
                "영속성 모듈 코딩 컨벤션",
                true,
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /** 특정 모듈 ID의 Convention */
    public static Convention conventionWithModuleId(Long moduleId) {
        Instant now = FIXED_CLOCK.instant();
        return Convention.of(
                ConventionId.of(4L),
                ModuleId.of(moduleId),
                ConventionVersion.of("1.0.0"),
                "모듈 " + moduleId + " 코딩 컨벤션",
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 특정 버전의 Convention */
    public static Convention conventionWithVersion(String version) {
        Instant now = FIXED_CLOCK.instant();
        return Convention.of(
                ConventionId.of(5L),
                ModuleId.of(DEFAULT_MODULE_ID),
                ConventionVersion.of(version),
                "도메인 모듈 코딩 컨벤션",
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 커스텀 Convention 생성 */
    public static Convention customConvention(
            Long id,
            Long moduleId,
            String version,
            String description,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        return Convention.of(
                ConventionId.of(id),
                ModuleId.of(moduleId),
                ConventionVersion.of(version),
                description,
                active,
                DeletionStatus.active(),
                createdAt,
                updatedAt);
    }

    /** 기본 Module ID 반환 (테스트용) */
    public static Long defaultModuleId() {
        return DEFAULT_MODULE_ID;
    }

    /** APPLICATION Module ID 반환 (테스트용) */
    public static Long applicationModuleId() {
        return APPLICATION_MODULE_ID;
    }

    /** PERSISTENCE Module ID 반환 (테스트용) */
    public static Long persistenceModuleId() {
        return PERSISTENCE_MODULE_ID;
    }

    /** REST_API Module ID 반환 (테스트용) */
    public static Long restApiModuleId() {
        return REST_API_MODULE_ID;
    }
}
