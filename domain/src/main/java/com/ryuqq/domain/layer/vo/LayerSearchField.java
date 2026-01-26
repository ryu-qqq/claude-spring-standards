package com.ryuqq.domain.layer.vo;

import com.ryuqq.domain.common.vo.SearchField;

/**
 * LayerSearchField - Layer 검색 필드
 *
 * <p>Layer 목록 조회 시 검색에 사용되는 필드입니다.
 *
 * <p>각 필드에서 부분 일치 검색을 지원합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public enum LayerSearchField implements SearchField {
    CODE("code"),
    NAME("name"),
    DESCRIPTION("description");

    private final String fieldName;

    LayerSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 기본 검색 필드 반환
     *
     * @return 기본 검색 필드 (CODE)
     */
    public static LayerSearchField defaultField() {
        return CODE;
    }
}
