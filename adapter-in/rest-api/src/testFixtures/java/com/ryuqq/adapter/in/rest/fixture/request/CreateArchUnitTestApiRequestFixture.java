package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.archunittest.dto.request.CreateArchUnitTestApiRequest;

/**
 * CreateArchUnitTestApiRequest Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateArchUnitTestApiRequestFixture {

    private CreateArchUnitTestApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static CreateArchUnitTestApiRequest valid() {
        return new CreateArchUnitTestApiRequest(
                1L,
                "ARCH-001",
                "Lombok 사용 금지 테스트",
                "Domain Layer에서 Lombok 사용을 검증하는 테스트",
                "DomainLayerArchUnitTest",
                "shouldNotUseLombok",
                "@ArchTest\npublic static void shouldNotUseLombok() { ... }",
                "BLOCKER");
    }

    public static CreateArchUnitTestApiRequest invalidWithNullStructureId() {
        return new CreateArchUnitTestApiRequest(
                null, "ARCH-001", "Name", null, null, null, "testCode", "BLOCKER");
    }

    public static CreateArchUnitTestApiRequest invalidWithBlankCode() {
        return new CreateArchUnitTestApiRequest(
                1L, "", "Name", null, null, null, "testCode", "BLOCKER");
    }

    public static CreateArchUnitTestApiRequest invalidWithBlankTestCode() {
        return new CreateArchUnitTestApiRequest(
                1L, "ARCH-001", "Name", null, null, null, "", "BLOCKER");
    }
}
