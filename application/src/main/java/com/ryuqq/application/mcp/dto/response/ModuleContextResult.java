package com.ryuqq.application.mcp.dto.response;

/**
 * ModuleContextResult - Module Context 조회 결과
 *
 * <p>코드 생성에 필요한 Module 전체 컨텍스트 정보를 담습니다.
 *
 * @param module 모듈 정보
 * @param executionContext 실행 컨텍스트 (패키지 구조, 템플릿, ArchUnit 테스트)
 * @param ruleContext 규칙 컨텍스트 (컨벤션, 코딩 규칙)
 * @param summary 요약 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ModuleContextResult(
        ModuleSummaryResult module,
        ExecutionContextResult executionContext,
        RuleContextResult ruleContext,
        ModuleContextSummaryResult summary) {}
