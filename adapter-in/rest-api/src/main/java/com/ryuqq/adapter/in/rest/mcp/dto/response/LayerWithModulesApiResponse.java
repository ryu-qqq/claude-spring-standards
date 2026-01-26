package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * LayerWithModulesApiResponse - 모듈을 포함한 레이어 정보
 *
 * @param code 레이어 코드
 * @param name 레이어 이름
 * @param description 레이어 설명
 * @param modules 모듈 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "모듈을 포함한 레이어 정보")
public record LayerWithModulesApiResponse(
        @Schema(description = "레이어 코드", example = "DOMAIN") String code,
        @Schema(description = "레이어 이름", example = "Domain Layer") String name,
        @Schema(description = "레이어 설명", example = "핵심 비즈니스 로직") String description,
        @Schema(description = "모듈 목록") List<ModuleWithPackagesApiResponse> modules) {}
