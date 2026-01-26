package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * PackageSummaryResult - 패키지 요약 정보
 *
 * @param id 패키지 구조 ID
 * @param pathPattern 경로 패턴
 * @param purposeSummary 목적 요약
 * @param allowedClassTypes 허용된 클래스 타입 목록
 * @param templateCount 템플릿 개수
 * @param ruleCount 규칙 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackageSummaryResult(
        Long id,
        String pathPattern,
        String purposeSummary,
        List<String> allowedClassTypes,
        int templateCount,
        int ruleCount) {}
