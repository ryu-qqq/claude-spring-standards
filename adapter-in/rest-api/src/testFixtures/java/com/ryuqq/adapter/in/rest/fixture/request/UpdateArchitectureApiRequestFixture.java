package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.architecture.dto.request.UpdateArchitectureApiRequest;
import java.util.List;

/**
 * UpdateArchitectureApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateArchitectureApiRequestFixture {

    private UpdateArchitectureApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateArchitectureApiRequest valid() {
        return new UpdateArchitectureApiRequest(
                "Hexagonal Architecture",
                "HEXAGONAL",
                "Ports and Adapters 패턴",
                List.of("의존성 역전", "계층 분리"),
                List.of());
    }

    public static UpdateArchitectureApiRequest invalidWithBlankName() {
        return new UpdateArchitectureApiRequest("", "HEXAGONAL", null, null, null);
    }
}
