package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * PackageStructureWithDetailsResult - 상세 정보를 포함한 패키지 구조 결과
 *
 * @param id 패키지 구조 ID
 * @param pathPattern 경로 패턴
 * @param description 설명
 * @param purposes 목적 목록
 * @param templates 템플릿 목록
 * @param archUnitTests ArchUnit 테스트 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackageStructureWithDetailsResult(
        Long id,
        String pathPattern,
        String description,
        List<PackagePurposeDetailResult> purposes,
        List<ClassTemplateDetailResult> templates,
        List<ArchUnitTestDetailResult> archUnitTests) {}
