package com.ryuqq.domain.architecture.vo;

import com.ryuqq.domain.common.vo.SortKey;

/**
 * ArchitectureSortKey - Architecture 정렬 키
 *
 * <p>Architecture 목록 조회 시 정렬에 사용되는 키입니다.
 *
 * @author ryu-qqq
 */
public enum ArchitectureSortKey implements SortKey {
    ID("id"),
    NAME("name"),
    PATTERN_TYPE("patternType"),
    TECH_STACK_ID("techStackId"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String fieldName;

    ArchitectureSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 기본 정렬 키 반환
     *
     * @return 기본 정렬 키 (CREATED_AT)
     */
    public static ArchitectureSortKey defaultKey() {
        return CREATED_AT;
    }
}
