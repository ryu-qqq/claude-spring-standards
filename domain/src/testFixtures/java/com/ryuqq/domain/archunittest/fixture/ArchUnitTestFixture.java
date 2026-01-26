package com.ryuqq.domain.archunittest.fixture;

import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
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

/**
 * ArchUnitTest Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 ArchUnitTest 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ArchUnitTestFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private ArchUnitTestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 ArchUnitTest Fixture (신규 생성) */
    public static ArchUnitTest defaultNewArchUnitTest() {
        return ArchUnitTest.forNew(
                PackageStructureId.of(1L),
                "ARCH-001",
                ArchUnitTestName.of("Lombok 금지 검증"),
                ArchUnitTestDescription.of("Domain 레이어에서 Lombok 사용 금지"),
                "DomainArchTest",
                "lombok_should_not_be_used",
                TestCode.of("@ArchTest\nstatic final ArchRule lombok_should_not_be_used = ..."),
                ArchUnitTestSeverity.BLOCKER,
                FIXED_CLOCK.instant());
    }

    /** 기존 ArchUnitTest Fixture (저장된 상태) */
    public static ArchUnitTest defaultExistingArchUnitTest() {
        Instant now = FIXED_CLOCK.instant();
        return ArchUnitTest.of(
                ArchUnitTestId.of(1L),
                PackageStructureId.of(1L),
                "ARCH-001",
                ArchUnitTestName.of("Lombok 금지 검증"),
                ArchUnitTestDescription.of("Domain 레이어에서 Lombok 사용 금지"),
                "DomainArchTest",
                "lombok_should_not_be_used",
                TestCode.of("@ArchTest\nstatic final ArchRule lombok_should_not_be_used = ..."),
                ArchUnitTestSeverity.BLOCKER,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 패키지 구조가 다른 ArchUnitTest */
    public static ArchUnitTest archUnitTestWithDifferentStructure() {
        Instant now = FIXED_CLOCK.instant();
        return ArchUnitTest.of(
                ArchUnitTestId.of(2L),
                PackageStructureId.of(2L),
                "ARCH-002",
                ArchUnitTestName.of("레이어 의존성 검증"),
                ArchUnitTestDescription.of("레이어 간 의존성 규칙 검증"),
                "LayerDependencyArchTest",
                "layer_dependency_should_be_respected",
                TestCode.of("@ArchTest\nstatic final ArchRule layer_dependency = ..."),
                ArchUnitTestSeverity.BLOCKER,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 삭제된 ArchUnitTest */
    public static ArchUnitTest deletedArchUnitTest() {
        Instant now = FIXED_CLOCK.instant();
        return ArchUnitTest.of(
                ArchUnitTestId.of(3L),
                PackageStructureId.of(1L),
                "ARCH-003",
                ArchUnitTestName.of("삭제된 테스트"),
                ArchUnitTestDescription.of("삭제된 ArchUnit 테스트"),
                "DeletedArchTest",
                "deleted_test",
                TestCode.of("@ArchTest\nstatic final ArchRule deleted = ..."),
                ArchUnitTestSeverity.INFO,
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /** 테스트 클래스명과 메서드명이 없는 ArchUnitTest */
    public static ArchUnitTest archUnitTestWithoutTestNames() {
        Instant now = FIXED_CLOCK.instant();
        return ArchUnitTest.of(
                ArchUnitTestId.of(4L),
                PackageStructureId.of(1L),
                "ARCH-004",
                ArchUnitTestName.of("테스트 이름 없음"),
                ArchUnitTestDescription.of("테스트 클래스/메서드 이름이 없는 경우"),
                null,
                null,
                TestCode.of("@ArchTest\nstatic final ArchRule test = ..."),
                ArchUnitTestSeverity.INFO,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 심각도가 없는 ArchUnitTest */
    public static ArchUnitTest archUnitTestWithoutSeverity() {
        Instant now = FIXED_CLOCK.instant();
        return ArchUnitTest.of(
                ArchUnitTestId.of(5L),
                PackageStructureId.of(1L),
                "ARCH-005",
                ArchUnitTestName.of("심각도 없음"),
                ArchUnitTestDescription.of("심각도가 없는 테스트"),
                "InfoArchTest",
                "info_test",
                TestCode.of("@ArchTest\nstatic final ArchRule info = ..."),
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /** 커스텀 ArchUnitTest 생성 */
    public static ArchUnitTest customArchUnitTest(
            Long id,
            Long structureId,
            String code,
            String name,
            String description,
            String testClassName,
            String testMethodName,
            String testCode,
            String severity,
            Instant createdAt,
            Instant updatedAt) {
        ArchUnitTestSeverity mappedSeverity =
                severity != null ? ArchUnitTestSeverity.valueOf(severity) : null;
        return ArchUnitTest.of(
                ArchUnitTestId.of(id),
                PackageStructureId.of(structureId),
                code,
                ArchUnitTestName.of(name),
                description != null ? ArchUnitTestDescription.of(description) : null,
                testClassName,
                testMethodName,
                TestCode.of(testCode),
                mappedSeverity,
                DeletionStatus.active(),
                createdAt,
                updatedAt);
    }
}
