package com.ryuqq.application.mcp.dto.response;

/**
 * ModuleSummaryResult - 모듈 요약 정보
 *
 * @param id 모듈 ID
 * @param name 모듈 이름
 * @param description 모듈 설명
 * @param layer 레이어 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ModuleSummaryResult(
        Long id, String name, String description, LayerSummaryResult layer) {}
