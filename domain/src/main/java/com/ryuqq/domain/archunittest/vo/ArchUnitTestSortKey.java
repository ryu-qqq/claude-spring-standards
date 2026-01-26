package com.ryuqq.domain.archunittest.vo;

import com.ryuqq.domain.common.vo.SortKey;

/**
 * ArchUnitTestSortKey - ArchUnit 테스트 정렬 키 Value Object
 *
 * @author ryu-qqq
 * @since 2026-01-06
 */
public enum ArchUnitTestSortKey implements SortKey {
    NAME("name", "이름순"),
    TYPE("testType", "유형순"),
    CREATED_AT("createdAt", "생성일순"),
    UPDATED_AT("updatedAt", "수정일순");

    private final String fieldName;
    private final String displayName;

    ArchUnitTestSortKey(String fieldName, String displayName) {
        this.fieldName = fieldName;
        this.displayName = displayName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 표시용 이름 반환
     *
     * @return 한글 표시명
     */
    public String displayName() {
        return displayName;
    }

    /**
     * 기본 정렬 키 반환
     *
     * @return CREATED_AT
     */
    public static ArchUnitTestSortKey defaultKey() {
        return CREATED_AT;
    }

    /**
     * 문자열로부터 SortKey 파싱 (대소문자 무관)
     *
     * @param value 문자열
     * @return ArchUnitTestSortKey (유효하지 않으면 CREATED_AT 반환)
     */
    public static ArchUnitTestSortKey fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultKey();
        }
        try {
            return ArchUnitTestSortKey.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return defaultKey();
        }
    }
}
