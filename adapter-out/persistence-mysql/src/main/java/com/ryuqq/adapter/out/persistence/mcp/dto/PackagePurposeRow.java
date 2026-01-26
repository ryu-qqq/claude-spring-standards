package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * PackagePurposeRow - PackagePurpose DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param structureId 패키지 구조 ID
 * @param code 목적 코드
 * @param description 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackagePurposeRow(Long structureId, String code, String description) {}
