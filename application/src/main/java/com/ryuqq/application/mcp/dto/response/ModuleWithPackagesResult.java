package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * ModuleWithPackagesResult - 패키지를 포함한 모듈 정보
 *
 * @param id 모듈 ID
 * @param name 모듈 이름
 * @param description 모듈 설명
 * @param packages 패키지 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ModuleWithPackagesResult(
        Long id, String name, String description, List<PackageSummaryResult> packages) {}
