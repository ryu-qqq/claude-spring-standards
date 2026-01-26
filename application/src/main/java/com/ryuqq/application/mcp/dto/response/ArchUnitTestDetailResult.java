package com.ryuqq.application.mcp.dto.response;

/**
 * ArchUnitTestDetailResult - ArchUnit 테스트 상세 정보
 *
 * @param id 테스트 ID
 * @param name 테스트 이름
 * @param description 설명
 * @param testCode 테스트 코드
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ArchUnitTestDetailResult(Long id, String name, String description, String testCode) {}
