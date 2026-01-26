package com.ryuqq.application.mcp.dto.response;

/**
 * PackagePurposeDetailResult - 패키지 목적 상세 정보
 *
 * @param classType 클래스 타입
 * @param description 설명
 * @param constraints 제약사항
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PackagePurposeDetailResult(
        String classType, String description, String constraints) {}
