package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ClassTemplateDetailApiResponse - 클래스 템플릿 상세 정보
 *
 * @param id 템플릿 ID
 * @param classTypeId 클래스 타입 ID
 * @param name 템플릿 이름
 * @param description 설명
 * @param body 템플릿 본문
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "클래스 템플릿 상세 정보")
public record ClassTemplateDetailApiResponse(
        @Schema(description = "템플릿 ID", example = "5") Long id,
        @Schema(description = "클래스 타입 ID", example = "1") Long classTypeId,
        @Schema(description = "템플릿 이름", example = "Aggregate Root 기본 템플릿") String name,
        @Schema(description = "설명", example = "표준 Aggregate Root 구조") String description,
        @Schema(
                        description = "템플릿 본문",
                        example =
                                "/**\n"
                                        + " * {@link [AggregateRoot]} 구현체.\n"
                                        + " */\n"
                                        + "public class {Name} {\n"
                                        + "    // ...\n"
                                        + "}")
                String body) {}
