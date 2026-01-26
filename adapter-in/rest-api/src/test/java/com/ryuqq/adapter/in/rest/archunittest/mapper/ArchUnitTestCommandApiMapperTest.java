package com.ryuqq.adapter.in.rest.archunittest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.archunittest.dto.request.CreateArchUnitTestApiRequest;
import com.ryuqq.adapter.in.rest.archunittest.dto.request.UpdateArchUnitTestApiRequest;
import com.ryuqq.adapter.in.rest.fixture.request.CreateArchUnitTestApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateArchUnitTestApiRequestFixture;
import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ArchUnitTestCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ArchUnitTestCommandApiMapper 단위 테스트")
class ArchUnitTestCommandApiMapperTest {

    private ArchUnitTestCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArchUnitTestCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateArchUnitTestApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateArchUnitTestApiRequest request = CreateArchUnitTestApiRequestFixture.valid();

            // When
            CreateArchUnitTestCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.structureId()).isEqualTo(1L);
            assertThat(command.code()).isEqualTo("ARCH-001");
            assertThat(command.name()).isEqualTo("Lombok 사용 금지 테스트");
            assertThat(command.description()).isEqualTo("Domain Layer에서 Lombok 사용을 검증하는 테스트");
            assertThat(command.testClassName()).isEqualTo("DomainLayerArchUnitTest");
            assertThat(command.testMethodName()).isEqualTo("shouldNotUseLombok");
            assertThat(command.testCode())
                    .isEqualTo("@ArchTest\npublic static void shouldNotUseLombok() { ... }");
            assertThat(command.severity()).isEqualTo("BLOCKER");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateArchUnitTestApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long archUnitTestId = 1L;
            UpdateArchUnitTestApiRequest request = UpdateArchUnitTestApiRequestFixture.valid();

            // When
            UpdateArchUnitTestCommand command = mapper.toCommand(archUnitTestId, request);

            // Then
            assertThat(command.archUnitTestId()).isEqualTo(archUnitTestId);
            assertThat(command.code()).isEqualTo("ARCH-001");
            assertThat(command.name()).isEqualTo("Lombok 사용 금지 테스트");
        }
    }
}
