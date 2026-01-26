package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.mcp.dto.request.PlanningContextApiRequest;
import java.util.List;

/**
 * PlanningContextApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PlanningContextApiRequestFixture {

    private PlanningContextApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static PlanningContextApiRequest valid() {
        return new PlanningContextApiRequest(
                List.of("DOMAIN", "APPLICATION", "PERSISTENCE", "REST_API"), 1L);
    }

    public static PlanningContextApiRequest validWithoutTechStackId() {
        return new PlanningContextApiRequest(List.of("DOMAIN", "APPLICATION"), null);
    }

    public static PlanningContextApiRequest invalidWithEmptyLayers() {
        return new PlanningContextApiRequest(List.of(), 1L);
    }

    public static PlanningContextApiRequest invalidWithNullLayers() {
        return new PlanningContextApiRequest(null, 1L);
    }

    public static PlanningContextApiRequest withLayers(List<String> layers) {
        return new PlanningContextApiRequest(layers, 1L);
    }

    public static PlanningContextApiRequest withTechStackId(Long techStackId) {
        return new PlanningContextApiRequest(List.of("DOMAIN", "APPLICATION"), techStackId);
    }
}
