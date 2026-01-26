package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.UpdateClassTypeCategoryApiRequest;

/**
 * UpdateClassTypeCategoryApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateClassTypeCategoryApiRequestFixture {

    private static final String DEFAULT_CODE = "DOMAIN_TYPES";
    private static final String DEFAULT_NAME = "도메인 타입";
    private static final String DEFAULT_DESCRIPTION = "도메인 레이어 클래스 타입";
    private static final Integer DEFAULT_ORDER_INDEX = 1;

    private UpdateClassTypeCategoryApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateClassTypeCategoryApiRequest valid() {
        return new UpdateClassTypeCategoryApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeCategoryApiRequest invalidWithBlankCode() {
        return new UpdateClassTypeCategoryApiRequest(
                "", DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeCategoryApiRequest invalidWithLongCode() {
        return new UpdateClassTypeCategoryApiRequest(
                "A".repeat(51), DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeCategoryApiRequest invalidWithBlankName() {
        return new UpdateClassTypeCategoryApiRequest(
                DEFAULT_CODE, "", DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeCategoryApiRequest invalidWithLongName() {
        return new UpdateClassTypeCategoryApiRequest(
                DEFAULT_CODE, "A".repeat(101), DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeCategoryApiRequest invalidWithNullOrderIndex() {
        return new UpdateClassTypeCategoryApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, null);
    }

    public static UpdateClassTypeCategoryApiRequest invalidWithNegativeOrderIndex() {
        return new UpdateClassTypeCategoryApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, -1);
    }

    public static UpdateClassTypeCategoryApiRequest withCode(String code) {
        return new UpdateClassTypeCategoryApiRequest(
                code, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeCategoryApiRequest withName(String name) {
        return new UpdateClassTypeCategoryApiRequest(
                DEFAULT_CODE, name, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeCategoryApiRequest withOrderIndex(Integer orderIndex) {
        return new UpdateClassTypeCategoryApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, orderIndex);
    }
}
