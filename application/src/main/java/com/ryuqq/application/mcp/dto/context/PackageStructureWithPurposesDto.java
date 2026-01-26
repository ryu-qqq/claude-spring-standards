package com.ryuqq.application.mcp.dto.context;

import java.util.List;

/**
 * PackageStructureWithPurposesDto - PackageStructure + Purpose 조회 결과
 *
 * <p>MCP Context 조회용 DTO입니다.
 *
 * @param structureId 패키지 구조 ID
 * @param pathPattern 경로 패턴
 * @param description 설명
 * @param purposes 패키지 목적 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackageStructureWithPurposesDto(
        Long structureId,
        String pathPattern,
        String description,
        List<PackagePurposeDto> purposes) {}
