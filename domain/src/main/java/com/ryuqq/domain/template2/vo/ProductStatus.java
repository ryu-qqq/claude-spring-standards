package com.ryuqq.domain.template2.vo;

/**
 * Product Status Enum
 *
 * <p><strong>상태 정의</strong>:
 *
 * <ul>
 *   <li>DRAFT: 임시 저장 (판매 전)
 *   <li>ON_SALE: 판매 중
 *   <li>OUT_OF_STOCK: 품절
 *   <li>SUSPENDED: 판매 중지
 *   <li>DISCONTINUED: 판매 종료
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum ProductStatus {
    DRAFT("임시 저장"),
    ON_SALE("판매 중"),
    OUT_OF_STOCK("품절"),
    SUSPENDED("판매 중지"),
    DISCONTINUED("판매 종료");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 화면 표시용 이름 반환
     *
     * @return 화면 표시용 이름
     */
    public String displayName() {
        return description;
    }

    /**
     * 판매 가능한 상태인지 확인
     *
     * @return 판매 가능하면 true
     */
    public boolean isSellable() {
        return this == ON_SALE;
    }

    /**
     * 재고 관리가 필요한 상태인지 확인
     *
     * @return 재고 관리가 필요하면 true
     */
    public boolean requiresStockManagement() {
        return this == ON_SALE || this == OUT_OF_STOCK;
    }
}
