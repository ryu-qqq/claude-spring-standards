package com.ryuqq.application.mcp.dto.context;

import java.util.List;

/**
 * TemplateAndTestDto - ClassTemplate + ArchUnitTest 통합 조회 결과
 *
 * <p>MCP Context 조회용 DTO입니다.
 *
 * @param structureId 패키지 구조 ID
 * @param templates 클래스 템플릿 목록
 * @param archUnitTests ArchUnit 테스트 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
public record TemplateAndTestDto(
        Long structureId, List<ClassTemplateDto> templates, List<ArchUnitTestDto> archUnitTests) {}
