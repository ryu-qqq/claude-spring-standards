package com.ryuqq.application.layer.dto.response;

import java.time.Instant;

/**
 * LayerResult - Layer 조회 결과 DTO
 *
 * <p>Layer 조회 시 반환되는 불변 객체입니다.
 *
 * @param id 레이어 ID
 * @param architectureId 아키텍처 ID
 * @param code 레이어 코드
 * @param name 레이어 이름
 * @param description 레이어 설명
 * @param orderIndex 정렬 순서
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record LayerResult(
        Long id,
        Long architectureId,
        String code,
        String name,
        String description,
        int orderIndex,
        Instant createdAt,
        Instant updatedAt) {}
