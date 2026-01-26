package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ModuleWithPackagesApiResponse - 패키지를 포함한 모듈 정보
 *
 * @param id 모듈 ID
 * @param name 모듈 이름
 * @param description 모듈 설명
 * @param packages 패키지 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "패키지를 포함한 모듈 정보")
public record ModuleWithPackagesApiResponse(
        @Schema(description = "모듈 ID", example = "1") Long id,
        @Schema(description = "모듈 이름", example = "domain-core") String name,
        @Schema(description = "모듈 설명", example = "도메인 핵심 모듈") String description,
        @Schema(description = "패키지 목록") List<PackageSummaryApiResponse> packages) {}
