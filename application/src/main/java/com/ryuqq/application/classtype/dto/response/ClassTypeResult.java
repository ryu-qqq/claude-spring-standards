package com.ryuqq.application.classtype.dto.response;

import java.time.Instant;

/**
 * ClassTypeResult - ClassType 조회 결과
 *
 * <p>ClassType 조회 시 반환되는 결과 객체입니다.
 *
 * @param id 클래스 타입 ID
 * @param categoryId 카테고리 ID
 * @param code 클래스 타입 코드
 * @param name 클래스 타입 이름
 * @param description 클래스 타입 설명
 * @param orderIndex 정렬 순서
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record ClassTypeResult(
        Long id,
        Long categoryId,
        String code,
        String name,
        String description,
        int orderIndex,
        Instant createdAt,
        Instant updatedAt) {}
