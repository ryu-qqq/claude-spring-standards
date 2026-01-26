package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.layer.dto.request.UpdateLayerApiRequest;

/**
 * UpdateLayerApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateLayerApiRequestFixture {

    private static final String DEFAULT_CODE = "APPLICATION";
    private static final String DEFAULT_NAME = "Application Layer";
    private static final String DEFAULT_DESCRIPTION = "Application Layer Description";
    private static final Integer DEFAULT_ORDER_INDEX = 2;

    private UpdateLayerApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateLayerApiRequest valid() {
        return new UpdateLayerApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateLayerApiRequest invalidWithBlankCode() {
        return new UpdateLayerApiRequest(
                "", DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateLayerApiRequest invalidWithLongCode() {
        return new UpdateLayerApiRequest(
                "A".repeat(51), DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateLayerApiRequest invalidWithBlankName() {
        return new UpdateLayerApiRequest(
                DEFAULT_CODE, "", DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateLayerApiRequest invalidWithLongName() {
        return new UpdateLayerApiRequest(
                DEFAULT_CODE, "A".repeat(101), DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateLayerApiRequest invalidWithNullOrderIndex() {
        return new UpdateLayerApiRequest(DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, null);
    }

    public static UpdateLayerApiRequest invalidWithNegativeOrderIndex() {
        return new UpdateLayerApiRequest(DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, -1);
    }

    public static UpdateLayerApiRequest withCode(String code) {
        return new UpdateLayerApiRequest(
                code, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateLayerApiRequest withName(String name) {
        return new UpdateLayerApiRequest(
                DEFAULT_CODE, name, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static UpdateLayerApiRequest withOrderIndex(Integer orderIndex) {
        return new UpdateLayerApiRequest(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, orderIndex);
    }
}
