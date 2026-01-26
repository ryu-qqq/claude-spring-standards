package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * ArchUnitTestRow - ArchUnitTest DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param structureId 패키지 구조 ID
 * @param testId 테스트 ID
 * @param name 테스트 이름
 * @param description 설명
 * @param testCode 테스트 코드
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ArchUnitTestRow(
        Long structureId, Long testId, String name, String description, String testCode) {}
