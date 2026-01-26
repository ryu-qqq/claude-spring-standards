package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.CreateClassTypeCategoryApiRequest;

/**
 * CreateClassTypeCategoryApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateClassTypeCategoryApiRequestFixture {

    private static final Long DEFAULT_ARCHITECTURE_ID = 1L;
    private static final String DEFAULT_CODE = "DOMAIN_TYPES";
    private static final String DEFAULT_NAME = "도메인 타입";
    private static final String DEFAULT_DESCRIPTION = "도메인 레이어 클래스 타입";
    private static final Integer DEFAULT_ORDER_INDEX = 1;

    private CreateClassTypeCategoryApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateClassTypeCategoryApiRequest valid() {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeCategoryApiRequest invalidWithNullArchitectureId() {
        return new CreateClassTypeCategoryApiRequest(
                null, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeCategoryApiRequest invalidWithBlankCode() {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                "",
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeCategoryApiRequest invalidWithLongCode() {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                "A".repeat(51),
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeCategoryApiRequest invalidWithBlankName() {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                DEFAULT_CODE,
                "",
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeCategoryApiRequest invalidWithLongName() {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                DEFAULT_CODE,
                "A".repeat(101),
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeCategoryApiRequest invalidWithNullOrderIndex() {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, null);
    }

    public static CreateClassTypeCategoryApiRequest invalidWithNegativeOrderIndex() {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, -1);
    }

    public static CreateClassTypeCategoryApiRequest withArchitectureId(Long architectureId) {
        return new CreateClassTypeCategoryApiRequest(
                architectureId,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeCategoryApiRequest withCode(String code) {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                code,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateClassTypeCategoryApiRequest withOrderIndex(Integer orderIndex) {
        return new CreateClassTypeCategoryApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                orderIndex);
    }
}
