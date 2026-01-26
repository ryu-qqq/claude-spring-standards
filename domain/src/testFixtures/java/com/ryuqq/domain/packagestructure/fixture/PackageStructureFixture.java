package com.ryuqq.domain.packagestructure.fixture;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagepurpose.vo.AllowedClassTypes;
import com.ryuqq.domain.packagepurpose.vo.NamingPattern;
import com.ryuqq.domain.packagepurpose.vo.NamingSuffix;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PackageStructure Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 PackageStructure 객체 생성 유틸리티
 *
 * @author ryu-qqq
 */
public final class PackageStructureFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1L);

    private PackageStructureFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 다음 ModuleId 생성
     *
     * @return ModuleId
     */
    public static ModuleId nextModuleId() {
        return ModuleId.of(ID_SEQUENCE.getAndIncrement());
    }

    /**
     * 다음 PackageStructureId 생성
     *
     * @return PackageStructureId
     */
    public static PackageStructureId nextPackageStructureId() {
        return PackageStructureId.of(ID_SEQUENCE.getAndIncrement());
    }

    /**
     * 신규 PackageStructure 생성 (ID 미할당)
     *
     * @return 신규 PackageStructure
     */
    public static PackageStructure forNew() {
        return PackageStructure.forNew(
                nextModuleId(),
                PathPattern.of("com.example.domain.{bc}.aggregate"),
                AllowedClassTypes.empty(),
                NamingPattern.empty(),
                NamingSuffix.empty(),
                "기본 설명",
                FIXED_CLOCK.instant());
    }

    /**
     * 기존 PackageStructure 복원 (기본 설정)
     *
     * @return 복원된 PackageStructure
     */
    public static PackageStructure reconstitute() {
        return reconstitute(nextPackageStructureId());
    }

    /**
     * 지정된 ID로 PackageStructure 복원
     *
     * @param id PackageStructureId
     * @return 복원된 PackageStructure
     */
    public static PackageStructure reconstitute(PackageStructureId id) {
        Instant now = FIXED_CLOCK.instant();
        return PackageStructure.reconstitute(
                id,
                nextModuleId(),
                PathPattern.of("com.example.domain.{bc}.aggregate"),
                AllowedClassTypes.empty(),
                NamingPattern.empty(),
                NamingSuffix.empty(),
                "기본 설명",
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기본 기존 PackageStructure (저장된 상태)
     *
     * @return 기존 PackageStructure
     */
    public static PackageStructure defaultExistingPackageStructure() {
        Instant now = FIXED_CLOCK.instant();
        return PackageStructure.of(
                nextPackageStructureId(),
                nextModuleId(),
                PathPattern.of("com.example.domain.{bc}.aggregate"),
                AllowedClassTypes.empty(),
                NamingPattern.empty(),
                NamingSuffix.empty(),
                "기본 설명",
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 삭제된 PackageStructure
     *
     * @return 삭제된 PackageStructure
     */
    public static PackageStructure deletedPackageStructure() {
        Instant now = FIXED_CLOCK.instant();
        return PackageStructure.reconstitute(
                nextPackageStructureId(),
                nextModuleId(),
                PathPattern.of("com.example.domain.{bc}.aggregate"),
                AllowedClassTypes.empty(),
                NamingPattern.empty(),
                NamingSuffix.empty(),
                "기본 설명",
                DeletionStatus.deletedAt(now),
                now,
                now);
    }
}
