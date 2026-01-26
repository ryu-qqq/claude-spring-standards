package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ExecutionContextApiResponse - 실행 컨텍스트 응답
 *
 * @param packageStructures 패키지 구조 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "실행 컨텍스트")
public record ExecutionContextApiResponse(
        @Schema(description = "패키지 구조 목록")
                List<PackageStructureWithDetailsApiResponse> packageStructures) {}
