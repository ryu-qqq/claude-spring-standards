package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.layer.dto.request.CreateLayerApiRequest;

/**
 * CreateLayerApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateLayerApiRequestFixture {

    private static final Long DEFAULT_ARCHITECTURE_ID = 1L;
    private static final String DEFAULT_CODE = "DOMAIN";
    private static final String DEFAULT_NAME = "Domain Layer";
    private static final String DEFAULT_DESCRIPTION = "Domain Layer Description";
    private static final Integer DEFAULT_ORDER_INDEX = 1;

    private CreateLayerApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateLayerApiRequest valid() {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateLayerApiRequest invalidWithNullArchitectureId() {
        return new CreateLayerApiRequest(
                null, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_ORDER_INDEX);
    }

    public static CreateLayerApiRequest invalidWithBlankCode() {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                "",
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateLayerApiRequest invalidWithLongCode() {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                "A".repeat(51),
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateLayerApiRequest invalidWithBlankName() {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                DEFAULT_CODE,
                "",
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateLayerApiRequest invalidWithLongName() {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                DEFAULT_CODE,
                "A".repeat(101),
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateLayerApiRequest invalidWithNullOrderIndex() {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, null);
    }

    public static CreateLayerApiRequest invalidWithNegativeOrderIndex() {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID, DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, -1);
    }

    public static CreateLayerApiRequest withArchitectureId(Long architectureId) {
        return new CreateLayerApiRequest(
                architectureId,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateLayerApiRequest withCode(String code) {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                code,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_ORDER_INDEX);
    }

    public static CreateLayerApiRequest withOrderIndex(Integer orderIndex) {
        return new CreateLayerApiRequest(
                DEFAULT_ARCHITECTURE_ID,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                orderIndex);
    }
}
