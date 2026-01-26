package com.ryuqq.application.mcp.dto.response;

/**
 * PlanningContextSummaryResult - Planning Context 요약 정보
 *
 * @param totalModules 전체 모듈 개수
 * @param totalPackages 전체 패키지 개수
 * @param totalTemplates 전체 템플릿 개수
 * @param totalRules 전체 규칙 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PlanningContextSummaryResult(
        int totalModules, int totalPackages, int totalTemplates, int totalRules) {}
