package com.ryuqq.application.mcp.dto.response;

/**
 * ConfigFileResult - MCP Config File 결과 DTO
 *
 * <p>설정 파일 템플릿 정보를 담는 결과 DTO입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param id 설정 파일 템플릿 ID
 * @param toolType 도구 타입 (CLAUDE, CURSOR, COPILOT)
 * @param filePath 파일 경로 (예: .claude/)
 * @param fileName 파일명 (예: CLAUDE.md)
 * @param description 설명
 * @param templateContent 템플릿 내용
 * @param priority 우선순위
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConfigFileResult(
        Long id,
        String toolType,
        String filePath,
        String fileName,
        String description,
        String templateContent,
        int priority) {}
