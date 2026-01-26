package com.ryuqq.domain.archunittest.vo;

/**
 * ArchUnitTestSearchField - ArchUnitTest 검색 필드 Value Object
 *
 * @author ryu-qqq
 */
public enum ArchUnitTestSearchField {
    CODE("code"),
    NAME("name"),
    DESCRIPTION("description"),
    TEST_CLASS_NAME("testClassName"),
    TEST_METHOD_NAME("testMethodName");

    private final String fieldName;

    ArchUnitTestSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
