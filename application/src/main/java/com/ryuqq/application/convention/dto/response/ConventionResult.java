package com.ryuqq.application.convention.dto.response;

import java.time.Instant;

/**
 * ConventionResult - Convention 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 불변 결과 객체입니다.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param id 컨벤션 ID
 * @param moduleId 레이어 ID (Layer 테이블 FK)
 * @param version 버전 문자열
 * @param description 설명
 * @param active 활성화 여부
 * @param deleted 삭제 여부
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record ConventionResult(
        Long id,
        Long moduleId,
        String version,
        String description,
        boolean active,
        boolean deleted,
        Instant createdAt,
        Instant updatedAt) {}
