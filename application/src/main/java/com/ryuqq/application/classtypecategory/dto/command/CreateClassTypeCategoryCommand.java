package com.ryuqq.application.classtypecategory.dto.command;

/**
 * CreateClassTypeCategoryCommand - ClassTypeCategory 생성 Command
 *
 * <p>ClassTypeCategory 생성 시 필요한 데이터를 담는 불변 객체입니다.
 *
 * <p>CDTO-006: Command 내부에서 Validation 금지 → REST API Layer에서 검증합니다.
 *
 * @param architectureId 아키텍처 ID
 * @param code 카테고리 코드
 * @param name 카테고리 이름
 * @param description 카테고리 설명 (nullable)
 * @param orderIndex 정렬 순서
 * @author ryu-qqq
 */
public record CreateClassTypeCategoryCommand(
        Long architectureId, String code, String name, String description, int orderIndex) {}
