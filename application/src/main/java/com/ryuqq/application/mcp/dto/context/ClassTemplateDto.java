package com.ryuqq.application.mcp.dto.context;

/**
 * ClassTemplateDto - 클래스 템플릿 조회 결과
 *
 * <p>MCP Context 조회용 DTO입니다.
 *
 * @param templateId 템플릿 ID
 * @param classTypeId 클래스 타입 ID
 * @param templateCode 템플릿 코드
 * @param description 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ClassTemplateDto(
        Long templateId, Long classTypeId, String templateCode, String description) {}
