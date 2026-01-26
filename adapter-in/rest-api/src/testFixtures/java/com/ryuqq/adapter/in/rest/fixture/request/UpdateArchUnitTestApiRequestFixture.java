package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.archunittest.dto.request.UpdateArchUnitTestApiRequest;

/**
 * UpdateArchUnitTestApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateArchUnitTestApiRequestFixture {

    private UpdateArchUnitTestApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static UpdateArchUnitTestApiRequest valid() {
        return new UpdateArchUnitTestApiRequest(
                "ARCH-001",
                "Lombok 사용 금지 테스트",
                "Domain Layer에서 Lombok 사용을 검증하는 테스트",
                "DomainLayerArchUnitTest",
                "shouldNotUseLombok",
                "@ArchTest\npublic static void shouldNotUseLombok() { ... }",
                "BLOCKER");
    }

    public static UpdateArchUnitTestApiRequest invalidWithBlankCode() {
        return new UpdateArchUnitTestApiRequest(
                "", "Name", null, null, null, "testCode", "BLOCKER");
    }
}
