package com.ryuqq.application.mcp.dto.query;

/**
 * ModuleContextQuery - Module Context 조회 쿼리
 *
 * <p>Module Context 조회에 필요한 파라미터를 담습니다.
 *
 * <p>CDTO-001: Record 필수.
 *
 * @param moduleId 모듈 ID (필수)
 * @param classTypeId 클래스 타입 ID 필터 (선택)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ModuleContextQuery(Long moduleId, Long classTypeId) {}
