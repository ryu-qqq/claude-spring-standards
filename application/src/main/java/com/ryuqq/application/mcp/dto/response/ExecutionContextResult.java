package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * ExecutionContextResult - 실행 컨텍스트 결과
 *
 * @param packageStructures 패키지 구조 목록 (템플릿, ArchUnit 테스트 포함)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ExecutionContextResult(List<PackageStructureWithDetailsResult> packageStructures) {}
