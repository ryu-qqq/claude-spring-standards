package com.ryuqq.domain.onboardingcontext.fixture;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.vo.ContextContent;
import com.ryuqq.domain.onboardingcontext.vo.ContextTitle;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.onboardingcontext.vo.Priority;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * OnboardingContext Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 OnboardingContext 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OnboardingContextFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private OnboardingContextFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 OnboardingContext Fixture (저장된 상태) - SUMMARY 타입 */
    public static OnboardingContext defaultExistingOnboardingContext() {
        Instant now = FIXED_CLOCK.instant();
        return OnboardingContext.reconstitute(
                OnboardingContextId.of(1L),
                TechStackId.of(1L),
                ArchitectureId.of(1L),
                ContextType.SUMMARY,
                ContextTitle.of("프로젝트 개요"),
                ContextContent.of("# Project Summary\n\nSpring Boot 기반 헥사고날 아키텍처 프로젝트입니다."),
                Priority.of(1),
                DeletionStatus.active(),
                now,
                now);
    }

    /** ZERO_TOLERANCE 타입 OnboardingContext */
    public static OnboardingContext zeroToleranceOnboardingContext() {
        Instant now = FIXED_CLOCK.instant();
        return OnboardingContext.reconstitute(
                OnboardingContextId.of(2L),
                TechStackId.of(1L),
                ArchitectureId.of(1L),
                ContextType.ZERO_TOLERANCE,
                ContextTitle.of("Zero-Tolerance 규칙"),
                ContextContent.of("# Zero-Tolerance Rules\n\n1. Lombok 사용 금지\n2. JPA 관계 어노테이션 금지"),
                Priority.of(2),
                DeletionStatus.active(),
                now,
                now);
    }

    /** RULES_INDEX 타입 OnboardingContext */
    public static OnboardingContext rulesIndexOnboardingContext() {
        Instant now = FIXED_CLOCK.instant();
        return OnboardingContext.reconstitute(
                OnboardingContextId.of(3L),
                TechStackId.of(1L),
                ArchitectureId.of(1L),
                ContextType.RULES_INDEX,
                ContextTitle.of("규칙 인덱스"),
                ContextContent.of(
                        "# Rules Index\n\n"
                                + "- DOM-001: Domain Layer Rules\n"
                                + "- APP-001: Application Layer Rules"),
                Priority.of(3),
                DeletionStatus.active(),
                now,
                now);
    }

    /** MCP_USAGE 타입 OnboardingContext */
    public static OnboardingContext mcpUsageOnboardingContext() {
        Instant now = FIXED_CLOCK.instant();
        return OnboardingContext.reconstitute(
                OnboardingContextId.of(4L),
                TechStackId.of(1L),
                null,
                ContextType.MCP_USAGE,
                ContextTitle.of("MCP 사용법"),
                ContextContent.of("# MCP Tool Usage\n\n## planning_context\n개발 계획 수립에 필요한 컨텍스트 조회"),
                Priority.of(4),
                DeletionStatus.active(),
                now,
                now);
    }

    /** 삭제된 OnboardingContext */
    public static OnboardingContext deletedOnboardingContext() {
        Instant now = FIXED_CLOCK.instant();
        return OnboardingContext.reconstitute(
                OnboardingContextId.of(5L),
                TechStackId.of(1L),
                ArchitectureId.of(1L),
                ContextType.SUMMARY,
                ContextTitle.of("삭제된 컨텍스트"),
                ContextContent.of("# Deleted"),
                Priority.of(99),
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /** 커스텀 OnboardingContext 생성 */
    public static OnboardingContext customOnboardingContext(
            Long id,
            Long techStackId,
            Long architectureId,
            ContextType contextType,
            String title,
            String content,
            int priority) {
        Instant now = FIXED_CLOCK.instant();
        return OnboardingContext.reconstitute(
                OnboardingContextId.of(id),
                TechStackId.of(techStackId),
                architectureId != null ? ArchitectureId.of(architectureId) : null,
                contextType,
                ContextTitle.of(title),
                ContextContent.of(content),
                Priority.of(priority),
                DeletionStatus.active(),
                now,
                now);
    }
}
