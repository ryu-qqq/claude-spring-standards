package com.ryuqq.adapter.in.rest.configfiletemplate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CreateConfigFileTemplateApiRequest - ConfigFileTemplate 생성 API Request
 *
 * <p>ConfigFileTemplate 생성 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param techStackId 기술 스택 ID
 * @param architectureId 아키텍처 ID (nullable)
 * @param toolType 도구 타입 (CLAUDE, CURSOR, COPILOT 등)
 * @param filePath 파일 경로 (예: .claude/CLAUDE.md)
 * @param fileName 파일명 (예: CLAUDE.md)
 * @param content 파일 내용
 * @param category 카테고리 (MAIN_CONFIG, SKILL, RULE, AGENT, HOOK)
 * @param description 템플릿 설명
 * @param variables 치환 가능한 변수 정의 (JSON 문자열)
 * @param displayOrder 정렬 순서
 * @param isRequired 필수 파일 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ConfigFileTemplate 생성 요청 DTO")
public record CreateConfigFileTemplateApiRequest(
        @Schema(description = "기술 스택 ID", example = "1") @NotNull(message = "techStackId는 필수입니다")
                Long techStackId,
        @Schema(description = "아키텍처 ID", example = "1", nullable = true) Long architectureId,
        @Schema(description = "도구 타입", example = "CLAUDE")
                @NotBlank(message = "toolType은 필수입니다")
                @Size(max = 50, message = "toolType은 50자 이내여야 합니다")
                String toolType,
        @Schema(description = "파일 경로", example = ".claude/CLAUDE.md")
                @NotBlank(message = "filePath는 필수입니다")
                @Size(max = 200, message = "filePath는 200자 이내여야 합니다")
                String filePath,
        @Schema(description = "파일명", example = "CLAUDE.md")
                @NotBlank(message = "fileName은 필수입니다")
                @Size(max = 100, message = "fileName은 100자 이내여야 합니다")
                String fileName,
        @Schema(description = "파일 내용", example = "# Project Config...")
                @NotBlank(message = "content는 필수입니다")
                String content,
        @Schema(description = "카테고리", example = "MAIN_CONFIG", nullable = true)
                @Size(max = 50, message = "category는 50자 이내여야 합니다")
                String category,
        @Schema(description = "템플릿 설명", example = "Claude 메인 설정 파일", nullable = true)
                String description,
        @Schema(
                        description = "치환 가능한 변수 정의 (JSON)",
                        example = "{\"project_name\": \"string\"}",
                        nullable = true)
                String variables,
        @Schema(description = "정렬 순서", example = "0", nullable = true) Integer displayOrder,
        @Schema(description = "필수 파일 여부", example = "true") @NotNull(message = "isRequired는 필수입니다")
                Boolean isRequired) {}
