package com.ryuqq.application.mcp.dto.response;

/**
 * ClassTemplateDetailResult - 클래스 템플릿 상세 정보
 *
 * @param id 템플릿 ID
 * @param classTypeId 클래스 타입 ID
 * @param name 템플릿 이름
 * @param description 설명
 * @param body 템플릿 본문 (templateCode)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ClassTemplateDetailResult(
        Long id, Long classTypeId, String name, String description, String body) {}
