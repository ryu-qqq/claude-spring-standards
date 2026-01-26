package com.ryuqq.domain.architecture.fixture;

import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.architecture.vo.PatternDescription;
import com.ryuqq.domain.architecture.vo.PatternPrinciples;
import com.ryuqq.domain.architecture.vo.PatternType;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Architecture Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 Architecture 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ArchitectureFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private ArchitectureFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 Architecture Fixture (신규 생성) */
    public static Architecture defaultNewArchitecture() {
        return Architecture.forNew(
                TechStackId.of(1L),
                ArchitectureName.of("hexagonal-architecture"),
                PatternType.HEXAGONAL,
                PatternDescription.empty(),
                PatternPrinciples.empty(),
                ReferenceLinks.empty(),
                FIXED_CLOCK.instant());
    }

    /** 기존 Architecture Fixture (저장된 상태) */
    public static Architecture defaultExistingArchitecture() {
        Instant now = FIXED_CLOCK.instant();
        return Architecture.of(
                ArchitectureId.of(1L),
                TechStackId.of(1L),
                ArchitectureName.of("hexagonal-architecture"),
                PatternType.HEXAGONAL,
                PatternDescription.empty(),
                PatternPrinciples.empty(),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 설명이 포함된 Architecture */
    public static Architecture architectureWithDescription() {
        Instant now = FIXED_CLOCK.instant();
        return Architecture.of(
                ArchitectureId.of(2L),
                TechStackId.of(1L),
                ArchitectureName.of("hexagonal-architecture"),
                PatternType.HEXAGONAL,
                PatternDescription.of("Ports and Adapters 패턴을 사용한 헥사고날 아키텍처"),
                PatternPrinciples.empty(),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 원칙이 포함된 Architecture */
    public static Architecture architectureWithPrinciples() {
        Instant now = FIXED_CLOCK.instant();
        return Architecture.of(
                ArchitectureId.of(3L),
                TechStackId.of(1L),
                ArchitectureName.of("clean-architecture"),
                PatternType.CLEAN,
                PatternDescription.empty(),
                PatternPrinciples.of(java.util.List.of("DIP", "SRP", "OCP", "ISP")),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 설명과 원칙이 모두 포함된 Architecture */
    public static Architecture architectureWithDescriptionAndPrinciples() {
        Instant now = FIXED_CLOCK.instant();
        return Architecture.of(
                ArchitectureId.of(4L),
                TechStackId.of(1L),
                ArchitectureName.of("layered-architecture"),
                PatternType.LAYERED,
                PatternDescription.of("계층형 아키텍처 패턴"),
                PatternPrinciples.of(java.util.List.of("SRP", "SOC")),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 삭제된 Architecture */
    public static Architecture deletedArchitecture() {
        Instant now = FIXED_CLOCK.instant();
        return Architecture.of(
                ArchitectureId.of(5L),
                TechStackId.of(1L),
                ArchitectureName.of("deleted-architecture"),
                PatternType.HEXAGONAL,
                PatternDescription.empty(),
                PatternPrinciples.empty(),
                ReferenceLinks.empty(),
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /** 참조 링크가 포함된 Architecture */
    public static Architecture architectureWithReferenceLinks() {
        Instant now = FIXED_CLOCK.instant();
        return Architecture.of(
                ArchitectureId.of(8L),
                TechStackId.of(1L),
                ArchitectureName.of("hexagonal-with-references"),
                PatternType.HEXAGONAL,
                PatternDescription.of("Ports and Adapters 패턴"),
                PatternPrinciples.of(java.util.List.of("DIP", "OCP")),
                ReferenceLinks.of(
                        java.util.List.of(
                                "https://alistair.cockburn.us/hexagonal-architecture/",
                                "https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html")),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 특정 패턴 타입의 Architecture 생성 */
    public static Architecture architectureWithPatternType(PatternType patternType) {
        Instant now = FIXED_CLOCK.instant();
        return Architecture.of(
                ArchitectureId.of(6L),
                TechStackId.of(1L),
                ArchitectureName.of(patternType.name().toLowerCase() + "-architecture"),
                patternType,
                PatternDescription.empty(),
                PatternPrinciples.empty(),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 커스텀 Architecture 생성 */
    public static Architecture customArchitecture(
            Long id,
            Long techStackId,
            String name,
            PatternType patternType,
            String description,
            java.util.List<String> principles,
            Instant createdAt,
            Instant updatedAt) {
        return Architecture.of(
                ArchitectureId.of(id),
                TechStackId.of(techStackId),
                ArchitectureName.of(name),
                patternType,
                description != null
                        ? PatternDescription.of(description)
                        : PatternDescription.empty(),
                principles != null ? PatternPrinciples.of(principles) : PatternPrinciples.empty(),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                createdAt,
                updatedAt);
    }

    /** 특정 TechStack에 연결된 Architecture 생성 */
    public static Architecture architectureForTechStack(Long techStackId) {
        Instant now = FIXED_CLOCK.instant();
        return Architecture.of(
                ArchitectureId.of(7L),
                TechStackId.of(techStackId),
                ArchitectureName.of("techstack-specific-architecture"),
                PatternType.HEXAGONAL,
                PatternDescription.empty(),
                PatternPrinciples.empty(),
                ReferenceLinks.empty(),
                DeletionStatus.active(),
                now,
                now);
    }
}
