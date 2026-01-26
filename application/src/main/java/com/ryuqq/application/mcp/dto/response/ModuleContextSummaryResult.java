package com.ryuqq.application.mcp.dto.response;

/**
 * ModuleContextSummaryResult - Module Context 요약 정보
 *
 * @param packageCount 패키지 개수
 * @param templateCount 템플릿 개수
 * @param ruleCount 규칙 개수
 * @param zeroToleranceCount Zero-Tolerance 규칙 개수
 * @param archTestCount ArchUnit 테스트 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ModuleContextSummaryResult(
        int packageCount,
        int templateCount,
        int ruleCount,
        int zeroToleranceCount,
        int archTestCount) {}
