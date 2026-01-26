package com.ryuqq.adapter.in.rest.mcp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * GetConfigFilesApiRequest - MCP Config Files 조회 요청 DTO
 *
 * <p>init_project Tool에서 설정 파일 템플릿을 조회할 때 사용합니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param techStackId 기술 스택 ID (필수)
 * @param architectureId 아키텍처 ID (선택)
 * @param toolTypes 도구 타입 목록 (선택, 예: CLAUDE, CURSOR)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "MCP Config Files 조회 요청")
public record GetConfigFilesApiRequest(
        @Schema(description = "기술 스택 ID", example = "1") @NotNull(message = "techStackId는 필수입니다")
                Long techStackId,
        @Schema(description = "아키텍처 ID (선택)", example = "1") Long architectureId,
        @Schema(description = "도구 타입 목록", example = "[\"CLAUDE\", \"CURSOR\"]")
                List<String> toolTypes) {}
