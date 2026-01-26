package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * PlanningContextResult - Planning Context 조회 결과
 *
 * <p>Planning Context 조회 결과를 담습니다.
 *
 * @param techStack 기술 스택 정보
 * @param architecture 아키텍처 정보
 * @param layers 레이어 목록 (모듈 및 패키지 포함)
 * @param summary 요약 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PlanningContextResult(
        TechStackSummaryResult techStack,
        ArchitectureSummaryResult architecture,
        List<LayerWithModulesResult> layers,
        PlanningContextSummaryResult summary) {}
