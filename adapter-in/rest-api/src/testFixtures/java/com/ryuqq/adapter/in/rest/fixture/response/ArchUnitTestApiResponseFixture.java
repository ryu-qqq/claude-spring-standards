package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.archunittest.dto.response.ArchUnitTestApiResponse;

/**
 * ArchUnitTestApiResponse Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ArchUnitTestApiResponseFixture {

    private ArchUnitTestApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static ArchUnitTestApiResponse valid() {
        return new ArchUnitTestApiResponse(
                1L,
                1L,
                "ARCH-001",
                "Lombok 사용 금지 테스트",
                "Domain Layer에서 Lombok 사용을 검증하는 테스트",
                "DomainLayerArchUnitTest",
                "shouldNotUseLombok",
                "@ArchTest\npublic static void shouldNotUseLombok() { ... }",
                "BLOCKER",
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }
}
