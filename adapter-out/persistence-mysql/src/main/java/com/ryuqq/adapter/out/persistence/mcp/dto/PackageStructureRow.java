package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * PackageStructureRow - PackageStructure 기본 정보 DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param structureId 패키지 구조 ID
 * @param pathPattern 경로 패턴
 * @param description 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackageStructureRow(Long structureId, String pathPattern, String description) {}
