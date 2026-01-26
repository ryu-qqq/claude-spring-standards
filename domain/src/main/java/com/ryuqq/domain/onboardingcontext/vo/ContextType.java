package com.ryuqq.domain.onboardingcontext.vo;

/**
 * ContextType - 온보딩 컨텍스트 타입 열거형
 *
 * <p>Serena 온보딩 시 제공할 컨텍스트 유형을 정의합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public enum ContextType {
    /** 프로젝트 개요 */
    SUMMARY,
    /** Zero-Tolerance 규칙 목록 */
    ZERO_TOLERANCE,
    /** 레이어별 규칙 인덱스 (메타 정보) */
    RULES_INDEX,
    /** MCP 사용법 가이드 */
    MCP_USAGE;
}
