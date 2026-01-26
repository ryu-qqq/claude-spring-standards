package com.ryuqq.application.mcp.dto.query;

import java.util.List;

/**
 * ValidationContextQuery - Validation Context 조회 쿼리
 *
 * <p>Validation Context 조회에 필요한 파라미터를 담습니다.
 *
 * <p>CDTO-001: Record 필수.
 *
 * @param techStackId 기술 스택 ID (필수)
 * @param architectureId 아키텍처 ID (필수)
 * @param layers 레이어 코드 목록 (필수)
 * @param classTypes 클래스 타입 목록 (선택)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ValidationContextQuery(
        Long techStackId, Long architectureId, List<String> layers, List<String> classTypes) {}
