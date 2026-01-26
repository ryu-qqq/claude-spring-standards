package com.ryuqq.adapter.in.rest.architecture.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.architecture.dto.request.CreateArchitectureApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.request.UpdateArchitectureApiRequest;
import com.ryuqq.adapter.in.rest.fixture.request.CreateArchitectureApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateArchitectureApiRequestFixture;
import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ArchitectureCommandApiMapper 단위 테스트
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
@DisplayName("ArchitectureCommandApiMapper 단위 테스트")
class ArchitectureCommandApiMapperTest {

    private ArchitectureCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArchitectureCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateArchitectureApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateArchitectureApiRequest request = CreateArchitectureApiRequestFixture.valid();

            // When
            CreateArchitectureCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.techStackId()).isEqualTo(1L);
            assertThat(command.name()).isEqualTo("Hexagonal Architecture");
            assertThat(command.patternType()).isEqualTo("HEXAGONAL");
            assertThat(command.patternDescription()).isEqualTo("Ports and Adapters 패턴");
            assertThat(command.patternPrinciples()).containsExactly("의존성 역전", "계층 분리");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateArchitectureApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long architectureId = 1L;
            UpdateArchitectureApiRequest request = UpdateArchitectureApiRequestFixture.valid();

            // When
            UpdateArchitectureCommand command = mapper.toCommand(architectureId, request);

            // Then
            assertThat(command.id()).isEqualTo(architectureId);
            assertThat(command.name()).isEqualTo("Hexagonal Architecture");
            assertThat(command.patternType()).isEqualTo("HEXAGONAL");
        }
    }
}
