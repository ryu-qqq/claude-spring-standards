package com.ryuqq.application.mcp.dto.context;

/**
 * PackagePurposeDto - 패키지 목적 조회 결과
 *
 * <p>MCP Context 조회용 DTO입니다.
 *
 * @param code 목적 코드
 * @param description 설명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackagePurposeDto(String code, String description) {}
