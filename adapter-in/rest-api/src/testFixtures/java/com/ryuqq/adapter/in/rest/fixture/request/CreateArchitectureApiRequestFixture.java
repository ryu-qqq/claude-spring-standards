package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.architecture.dto.request.CreateArchitectureApiRequest;
import java.util.List;

/**
 * CreateArchitectureApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateArchitectureApiRequestFixture {

    private CreateArchitectureApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateArchitectureApiRequest valid() {
        return new CreateArchitectureApiRequest(
                1L,
                "Hexagonal Architecture",
                "HEXAGONAL",
                "Ports and Adapters 패턴",
                List.of("의존성 역전", "계층 분리"),
                List.of());
    }

    public static CreateArchitectureApiRequest invalidWithNullTechStackId() {
        return new CreateArchitectureApiRequest(
                null, "Architecture", "HEXAGONAL", null, null, null);
    }

    public static CreateArchitectureApiRequest invalidWithBlankName() {
        return new CreateArchitectureApiRequest(1L, "", "HEXAGONAL", null, null, null);
    }

    public static CreateArchitectureApiRequest invalidWithLongName() {
        return new CreateArchitectureApiRequest(1L, "A".repeat(101), "HEXAGONAL", null, null, null);
    }
}
