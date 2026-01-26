package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ConfigFileApiResponse - MCP Config File 개별 응답 DTO
 *
 * <p>설정 파일 템플릿 정보를 담습니다.
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
@Schema(description = "설정 파일 템플릿 정보")
public record ConfigFileApiResponse(
        @Schema(description = "설정 파일 템플릿 ID", example = "1") Long id,
        @Schema(description = "도구 타입", example = "CLAUDE") String toolType,
        @Schema(description = "파일 경로", example = ".claude/") String filePath,
        @Schema(description = "파일명", example = "CLAUDE.md") String fileName,
        @Schema(description = "설명", example = "메인 설정 파일") String description,
        @Schema(description = "템플릿 내용", example = "# Claude Configuration...")
                String templateContent,
        @Schema(description = "우선순위", example = "0") int priority) {}
