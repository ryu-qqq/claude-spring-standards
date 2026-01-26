package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ConfigFilesApiResponse - MCP Config Files 조회 응답 DTO
 *
 * <p>init_project Tool 응답용 설정 파일 목록입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param configFiles 설정 파일 템플릿 목록
 * @param totalCount 전체 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "설정 파일 템플릿 목록 응답")
public record ConfigFilesApiResponse(
        @Schema(description = "설정 파일 템플릿 목록") List<ConfigFileApiResponse> configFiles,
        @Schema(description = "전체 개수", example = "5") int totalCount) {}
