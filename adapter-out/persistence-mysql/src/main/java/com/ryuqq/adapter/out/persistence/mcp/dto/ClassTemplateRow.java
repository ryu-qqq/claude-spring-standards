package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * ClassTemplateRow - ClassTemplate DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param structureId 패키지 구조 ID
 * @param templateId 템플릿 ID
 * @param classTypeId 클래스 타입 ID
 * @param templateCode 템플릿 코드
 * @param description 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ClassTemplateRow(
        Long structureId,
        Long templateId,
        Long classTypeId,
        String templateCode,
        String description) {}
