package com.ryuqq.domain.layerdependency.vo;

/**
 * LayerType - 레이어 유형 Value Object
 *
 * <p>헥사고날 아키텍처의 레이어를 정의합니다.
 *
 * @author ryu-qqq
 */
public enum LayerType {
    DOMAIN("도메인 레이어"),
    APPLICATION("애플리케이션 레이어"),
    ADAPTER_IN("인바운드 어댑터 레이어"),
    ADAPTER_OUT("아웃바운드 어댑터 레이어"),
    COMMON("공통 레이어"),
    INFRASTRUCTURE("인프라스트럭처 레이어");

    private final String description;

    LayerType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
