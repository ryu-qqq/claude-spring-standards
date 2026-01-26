package com.ryuqq.domain.checklistitem.id;

/**
 * ChecklistItemId - 체크리스트 항목 식별자 Value Object
 *
 * <p>Long 타입 ID로 forNew()/isNew() 패턴 적용.
 *
 * @author ryu-qqq
 */
public record ChecklistItemId(Long value) {

    /** 신규 엔티티용 ID 생성 (null 값) */
    public static ChecklistItemId forNew() {
        return new ChecklistItemId(null);
    }

    /** 기존 엔티티용 ID 생성 */
    public static ChecklistItemId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "ChecklistItemId value must not be null for existing entity");
        }
        return new ChecklistItemId(value);
    }

    /** 신규 엔티티 여부 확인 */
    public boolean isNew() {
        return value == null;
    }
}
