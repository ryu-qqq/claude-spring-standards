package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtype.dto.request.CreateClassTypeApiRequest;

/**
 * CreateClassTypeApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateClassTypeApiRequestFixture {

    private static final Long DEFAULT_CATEGORY_ID = 1L;
    private static final String DEFAULT_CODE = "AGGREGATE";
    private static final String DEFAULT_NAME = "Aggregate";
    private static final String DEFAULT_DESCRIPTION = "도메인 Aggregate Root 클래스";
    private static final Integer DEFAULT_ORDER_INDEX = 1;

    private CreateClassTypeApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateClassTypeApiRequest valid() {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeApiRequest invalidWithNullCategoryId() {
        return new CreateClassTypeApiRequest(
                null, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeApiRequest invalidWithBlankCode() {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID, "", DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeApiRequest invalidWithLongCode() {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID,
                "A".repeat(51),
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeApiRequest invalidWithBlankName() {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID, DEFAULT_CODE, "", DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeApiRequest invalidWithLongName() {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID,
                DEFAULT_CODE,
                "A".repeat(101),
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeApiRequest invalidWithNullOrderIndex() {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, null);
    }

    public static CreateClassTypeApiRequest invalidWithNegativeOrderIndex() {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, -1);
    }

    public static CreateClassTypeApiRequest withCategoryId(Long categoryId) {
        return new CreateClassTypeApiRequest(
                categoryId, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeApiRequest withCode(String code) {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID, code, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeApiRequest withOrderIndex(Integer orderIndex) {
        return new CreateClassTypeApiRequest(
                DEFAULT_CATEGORY_ID, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, orderIndex);
    }
}
