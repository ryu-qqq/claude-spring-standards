package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.mcp.dto.request.ValidationContextApiRequest;
import java.util.List;

/**
 * ValidationContextApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ValidationContextApiRequestFixture {

    private ValidationContextApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ValidationContextApiRequest valid() {
        return new ValidationContextApiRequest(
                1L,
                1L,
                List.of("DOMAIN", "APPLICATION", "PERSISTENCE", "REST_API"),
                List.of("AGGREGATE", "USE_CASE", "ENTITY", "CONTROLLER"));
    }

    public static ValidationContextApiRequest validWithoutClassTypes() {
        return new ValidationContextApiRequest(1L, 1L, List.of("DOMAIN", "APPLICATION"), null);
    }

    public static ValidationContextApiRequest invalidWithEmptyLayers() {
        return new ValidationContextApiRequest(1L, 1L, List.of(), List.of("AGGREGATE"));
    }

    public static ValidationContextApiRequest invalidWithNullLayers() {
        return new ValidationContextApiRequest(1L, 1L, null, List.of("AGGREGATE"));
    }

    public static ValidationContextApiRequest withLayers(List<String> layers) {
        return new ValidationContextApiRequest(1L, 1L, layers, List.of("AGGREGATE"));
    }

    public static ValidationContextApiRequest withClassTypes(List<String> classTypes) {
        return new ValidationContextApiRequest(
                1L, 1L, List.of("DOMAIN", "APPLICATION"), classTypes);
    }
}
