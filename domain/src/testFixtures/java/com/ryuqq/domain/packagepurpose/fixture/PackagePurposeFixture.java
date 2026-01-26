package com.ryuqq.domain.packagepurpose.fixture;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * PackagePurpose Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 PackagePurpose 객체 생성 유틸리티
 *
 * @author ryu-qqq
 */
public final class PackagePurposeFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private PackagePurposeFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 신규 PackagePurpose 생성 (ID 미할당)
     *
     * @return 신규 PackagePurpose
     */
    public static PackagePurpose forNew() {
        return PackagePurpose.forNew(
                PackagePurposeVoFixtures.defaultStructureId(),
                PackagePurposeVoFixtures.defaultPurposeCode(),
                PackagePurposeVoFixtures.defaultPurposeName(),
                "기본 설명",
                PackagePurposeVoFixtures.defaultAllowedClassTypes(),
                PackagePurposeVoFixtures.defaultNamingPattern(),
                PackagePurposeVoFixtures.defaultNamingSuffix(),
                FIXED_CLOCK.instant());
    }

    /**
     * 기존 PackagePurpose 복원 (기본 설정)
     *
     * @return 복원된 PackagePurpose
     */
    public static PackagePurpose reconstitute() {
        return reconstitute(PackagePurposeVoFixtures.nextPackagePurposeId());
    }

    /**
     * 지정된 ID로 PackagePurpose 복원
     *
     * @param id PackagePurposeId
     * @return 복원된 PackagePurpose
     */
    public static PackagePurpose reconstitute(PackagePurposeId id) {
        Instant now = FIXED_CLOCK.instant();
        return PackagePurpose.reconstitute(
                id,
                PackagePurposeVoFixtures.defaultStructureId(),
                PackagePurposeVoFixtures.defaultPurposeCode(),
                PackagePurposeVoFixtures.defaultPurposeName(),
                "기본 설명",
                PackagePurposeVoFixtures.defaultAllowedClassTypes(),
                PackagePurposeVoFixtures.defaultNamingPattern(),
                PackagePurposeVoFixtures.defaultNamingSuffix(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기본 기존 PackagePurpose (저장된 상태)
     *
     * @return 기존 PackagePurpose
     */
    public static PackagePurpose defaultExistingPackagePurpose() {
        Instant now = FIXED_CLOCK.instant();
        return PackagePurpose.of(
                PackagePurposeVoFixtures.nextPackagePurposeId(),
                PackagePurposeVoFixtures.defaultStructureId(),
                PackagePurposeVoFixtures.defaultPurposeCode(),
                PackagePurposeVoFixtures.defaultPurposeName(),
                "기본 설명",
                PackagePurposeVoFixtures.defaultAllowedClassTypes(),
                PackagePurposeVoFixtures.defaultNamingPattern(),
                PackagePurposeVoFixtures.defaultNamingSuffix(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 삭제된 PackagePurpose
     *
     * @return 삭제된 PackagePurpose
     */
    public static PackagePurpose deletedPackagePurpose() {
        Instant now = FIXED_CLOCK.instant();
        return PackagePurpose.reconstitute(
                PackagePurposeVoFixtures.nextPackagePurposeId(),
                PackagePurposeVoFixtures.defaultStructureId(),
                PackagePurposeVoFixtures.defaultPurposeCode(),
                PackagePurposeVoFixtures.defaultPurposeName(),
                "기본 설명",
                PackagePurposeVoFixtures.defaultAllowedClassTypes(),
                PackagePurposeVoFixtures.defaultNamingPattern(),
                PackagePurposeVoFixtures.defaultNamingSuffix(),
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /**
     * 특정 패키지 구조의 PackagePurpose
     *
     * @param structureId PackageStructureId
     * @return PackagePurpose
     */
    public static PackagePurpose packagePurposeForStructureId(PackageStructureId structureId) {
        Instant now = FIXED_CLOCK.instant();
        return PackagePurpose.reconstitute(
                PackagePurposeVoFixtures.nextPackagePurposeId(),
                structureId,
                PackagePurposeVoFixtures.defaultPurposeCode(),
                PackagePurposeVoFixtures.defaultPurposeName(),
                "기본 설명",
                PackagePurposeVoFixtures.defaultAllowedClassTypes(),
                PackagePurposeVoFixtures.defaultNamingPattern(),
                PackagePurposeVoFixtures.defaultNamingSuffix(),
                DeletionStatus.active(),
                now,
                now);
    }
}
