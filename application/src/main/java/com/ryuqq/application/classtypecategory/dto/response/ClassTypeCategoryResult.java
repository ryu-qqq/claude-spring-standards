package com.ryuqq.application.classtypecategory.dto.response;

import java.time.Instant;

/**
 * ClassTypeCategoryResult - ClassTypeCategory 조회 결과
 *
 * <p>ClassTypeCategory 조회 시 반환되는 결과 객체입니다.
 *
 * @param id 카테고리 ID
 * @param architectureId 아키텍처 ID
 * @param code 카테고리 코드
 * @param name 카테고리 이름
 * @param description 카테고리 설명
 * @param orderIndex 정렬 순서
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record ClassTypeCategoryResult(
        Long id,
        Long architectureId,
        String code,
        String name,
        String description,
        int orderIndex,
        Instant createdAt,
        Instant updatedAt) {}
