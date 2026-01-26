package com.ryuqq.adapter.in.rest.mcp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * ValidationContextApiRequest - Validation Context 조회 요청 DTO
 *
 * <p>코드 검증에 필요한 Zero-Tolerance + Checklist 조회 요청입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * <p>DTO-002: @NotNull 필수 (Nullable 금지).
 *
 * @param techStackId 기술 스택 ID (필수)
 * @param architectureId 아키텍처 ID (필수)
 * @param layers 레이어 코드 목록 (필수, 예: ["DOMAIN", "APPLICATION"])
 * @param classTypes 클래스 타입 목록 (선택, 예: ["AGGREGATE", "USE_CASE"])
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Validation Context 조회 요청")
public record ValidationContextApiRequest(
        @Schema(description = "기술 스택 ID", example = "1") @NotNull(message = "techStackId는 필수입니다")
                Long techStackId,
        @Schema(description = "아키텍처 ID", example = "1") @NotNull(message = "architectureId는 필수입니다")
                Long architectureId,
        @Schema(
                        description = "레이어 코드 목록",
                        example = "[\"DOMAIN\", \"APPLICATION\", \"PERSISTENCE\", \"REST_API\"]")
                @NotEmpty(message = "layers는 필수입니다")
                @NotNull(message = "layers는 필수입니다")
                List<String> layers,
        @Schema(description = "클래스 타입 목록 (선택)", example = "[\"AGGREGATE\", \"USE_CASE\"]")
                List<String> classTypes) {}
