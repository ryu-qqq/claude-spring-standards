package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.architecture.dto.response.ArchitectureApiResponse;
import java.util.List;

/**
 * ArchitectureApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ArchitectureApiResponseFixture {

    private ArchitectureApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ArchitectureApiResponse valid() {
        return new ArchitectureApiResponse(
                1L,
                1L,
                "Hexagonal Architecture",
                "HEXAGONAL",
                "Ports and Adapters 패턴",
                List.of("의존성 역전", "계층 분리"),
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }
}
