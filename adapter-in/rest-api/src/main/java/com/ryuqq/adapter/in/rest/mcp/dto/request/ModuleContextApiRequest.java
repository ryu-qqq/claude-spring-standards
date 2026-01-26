package com.ryuqq.adapter.in.rest.mcp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModuleContextApiRequest - Module Context 조회 요청 DTO
 *
 * <p>코드 생성에 필요한 Module 전체 컨텍스트 조회 요청입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param classTypeId 클래스 타입 ID 필터 (선택)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Module Context 조회 요청")
public record ModuleContextApiRequest(
        @Schema(description = "클래스 타입 ID 필터 (선택)", example = "1") Long classTypeId) {}
