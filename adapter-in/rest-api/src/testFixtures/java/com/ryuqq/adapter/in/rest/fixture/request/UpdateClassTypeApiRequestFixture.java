package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtype.dto.request.UpdateClassTypeApiRequest;

/**
 * UpdateClassTypeApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateClassTypeApiRequestFixture {

    private static final String DEFAULT_CODE = "AGGREGATE";
    private static final String DEFAULT_NAME = "Aggregate";
    private static final String DEFAULT_DESCRIPTION = "도메인 Aggregate Root 클래스";
    private static final Integer DEFAULT_ORDER_INDEX = 1;

    private UpdateClassTypeApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateClassTypeApiRequest valid() {
        return new UpdateClassTypeApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeApiRequest invalidWithBlankCode() {
        return new UpdateClassTypeApiRequest(
                "", DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeApiRequest invalidWithLongCode() {
        return new UpdateClassTypeApiRequest(
                "A".repeat(51), DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeApiRequest invalidWithBlankName() {
        return new UpdateClassTypeApiRequest(
                DEFAULT_CODE, "", DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeApiRequest invalidWithLongName() {
        return new UpdateClassTypeApiRequest(
                DEFAULT_CODE, "A".repeat(101), DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeApiRequest invalidWithNullOrderIndex() {
        return new UpdateClassTypeApiRequest(DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, null);
    }

    public static UpdateClassTypeApiRequest invalidWithNegativeOrderIndex() {
        return new UpdateClassTypeApiRequest(DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, -1);
    }

    public static UpdateClassTypeApiRequest withCode(String code) {
        return new UpdateClassTypeApiRequest(
                code, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeApiRequest withName(String name) {
        return new UpdateClassTypeApiRequest(
                DEFAULT_CODE, name, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateClassTypeApiRequest withOrderIndex(Integer orderIndex) {
        return new UpdateClassTypeApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, orderIndex);
    }
}
