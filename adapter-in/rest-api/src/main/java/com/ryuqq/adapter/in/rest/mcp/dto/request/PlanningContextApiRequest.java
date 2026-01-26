package com.ryuqq.adapter.in.rest.mcp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * PlanningContextApiRequest - Planning Context 조회 요청 DTO
 *
 * <p>개발 계획 수립에 필요한 컨텍스트 조회 요청입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * <p>DTO-002: @NotNull 필수 (Nullable 금지).
 *
 * @param layers 레이어 코드 목록 (필수, 예: ["DOMAIN", "APPLICATION"])
 * @param techStackId 기술 스택 ID (선택, null이면 활성 스택 사용)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Planning Context 조회 요청")
public record PlanningContextApiRequest(
        @Schema(
                        description = "레이어 코드 목록",
                        example = "[\"DOMAIN\", \"APPLICATION\", \"PERSISTENCE\", \"REST_API\"]")
                @NotEmpty(message = "layers는 필수입니다")
                @NotNull(message = "layers는 필수입니다")
                List<String> layers,
        @Schema(description = "기술 스택 ID (선택, null이면 활성 스택 사용)", example = "1") Long techStackId) {}
