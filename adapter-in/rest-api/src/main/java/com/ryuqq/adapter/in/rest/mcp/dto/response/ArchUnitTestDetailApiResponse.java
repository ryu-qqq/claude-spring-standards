package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ArchUnitTestDetailApiResponse - ArchUnit 테스트 상세 정보
 *
 * @param id 테스트 ID
 * @param name 테스트 이름
 * @param description 설명
 * @param testCode 테스트 코드
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ArchUnit 테스트 상세 정보")
public record ArchUnitTestDetailApiResponse(
        @Schema(description = "테스트 ID", example = "3") Long id,
        @Schema(description = "테스트 이름", example = "Aggregate Lombok 금지") String name,
        @Schema(description = "설명", example = "Aggregate에서 Lombok 어노테이션 사용 금지") String description,
        @Schema(
                        description = "테스트 코드",
                        example =
                                "@ArchTest\n"
                                        + "static final ArchRule aggregates_should_not_use_lombok ="
                                        + " ...")
                String testCode) {}
