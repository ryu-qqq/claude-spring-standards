package com.ryuqq.application.classtypecategory.dto.command;

/**
 * UpdateClassTypeCategoryCommand - ClassTypeCategory 수정 Command
 *
 * <p>ClassTypeCategory 수정 시 필요한 데이터를 담는 불변 객체입니다.
 *
 * <p>CDTO-006: Command 내부에서 Validation 금지 → REST API Layer에서 검증합니다.
 *
 * @param id 수정 대상 ID
 * @param code 카테고리 코드
 * @param name 카테고리 이름
 * @param description 카테고리 설명 (nullable)
 * @param orderIndex 정렬 순서
 * @author ryu-qqq
 */
public record UpdateClassTypeCategoryCommand(
        Long id, String code, String name, String description, int orderIndex) {}
