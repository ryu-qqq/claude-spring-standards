package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PackagePurposeDetailApiResponse - 패키지 목적 상세 정보
 *
 * @param classType 클래스 타입
 * @param description 설명
 * @param constraints 제약사항
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "패키지 목적 상세 정보")
public record PackagePurposeDetailApiResponse(
        @Schema(description = "클래스 타입", example = "AGGREGATE") String classType,
        @Schema(description = "설명", example = "도메인의 핵심 비즈니스 로직을 담는 Aggregate Root")
                String description,
        @Schema(description = "제약사항", example = "반드시 하나의 Root Entity, 모든 상태 변경은 Root를 통해")
                String constraints) {}
