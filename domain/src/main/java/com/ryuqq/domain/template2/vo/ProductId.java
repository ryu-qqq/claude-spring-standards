package com.ryuqq.domain.template2.vo;

/**
 * Product ID Value Object (Auto Increment)
 *
 * <p><strong>DB 전략</strong>: MySQL AUTO_INCREMENT - DB가 ID 할당
 *
 * <p><strong>생성 패턴</strong>:
 *
 * <ul>
 *   <li>{@code forNew()} - 신규 엔티티 생성 시 (ID = null, DB가 할당 예정)
 *   <li>{@code of(Long value)} - 기존 엔티티 조회/참조 시 (ID 필수)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record ProductId(Long value) {

    /**
     * Compact Constructor (검증 로직)
     *
     * <p>주의: forNew()로 생성 시 null 허용 (DB AUTO_INCREMENT 대비)
     */
    public ProductId {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException("ProductId는 양수여야 합니다: " + value);
        }
    }

    /**
     * 신규 생성 - DB AUTO_INCREMENT가 ID 할당 예정
     *
     * @return ProductId (value = null)
     */
    public static ProductId forNew() {
        return new ProductId(null);
    }

    /**
     * 기존 ID 참조 - null 금지
     *
     * @param value ID 값 (null 불가)
     * @return ProductId
     * @throws IllegalArgumentException value가 null이거나 음수인 경우
     */
    public static ProductId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("기존 ProductId는 null일 수 없습니다");
        }
        return new ProductId(value);
    }

    /**
     * 신규 엔티티 여부 확인
     *
     * @return ID가 null이면 true (아직 DB에 저장되지 않음)
     */
    public boolean isNew() {
        return value == null;
    }
}
