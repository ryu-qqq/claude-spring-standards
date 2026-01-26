package com.ryuqq.application.mcp.dto.query;

import java.util.List;

/**
 * PlanningContextQuery - Planning Context 조회 쿼리
 *
 * <p>Planning Context 조회에 필요한 파라미터를 담습니다.
 *
 * <p>CDTO-001: Record 필수.
 *
 * @param layers 레이어 코드 목록 (필수)
 * @param techStackId 기술 스택 ID (선택, null이면 활성 스택 사용)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PlanningContextQuery(List<String> layers, Long techStackId) {}
