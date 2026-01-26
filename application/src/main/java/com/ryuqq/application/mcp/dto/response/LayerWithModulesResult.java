package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * LayerWithModulesResult - 모듈을 포함한 레이어 정보
 *
 * @param code 레이어 코드
 * @param name 레이어 이름
 * @param description 레이어 설명
 * @param modules 모듈 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LayerWithModulesResult(
        String code, String name, String description, List<ModuleWithPackagesResult> modules) {}
