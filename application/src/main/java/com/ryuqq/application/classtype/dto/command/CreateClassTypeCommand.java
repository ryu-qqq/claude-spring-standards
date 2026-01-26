package com.ryuqq.application.classtype.dto.command;

/**
 * CreateClassTypeCommand - ClassType 생성 Command
 *
 * <p>ClassType 생성 시 필요한 데이터를 담는 불변 객체입니다.
 *
 * <p>CDTO-006: Command 내부에서 Validation 금지 → REST API Layer에서 검증합니다.
 *
 * @param categoryId 카테고리 ID
 * @param code 클래스 타입 코드
 * @param name 클래스 타입 이름
 * @param description 클래스 타입 설명 (nullable)
 * @param orderIndex 정렬 순서
 * @author ryu-qqq
 */
public record CreateClassTypeCommand(
        Long categoryId, String code, String name, String description, int orderIndex) {}
