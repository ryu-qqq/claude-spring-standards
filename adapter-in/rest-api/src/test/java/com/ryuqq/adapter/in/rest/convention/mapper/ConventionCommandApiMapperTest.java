package com.ryuqq.adapter.in.rest.convention.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.convention.dto.request.CreateConventionApiRequest;
import com.ryuqq.adapter.in.rest.convention.dto.request.UpdateConventionApiRequest;
import com.ryuqq.adapter.in.rest.fixture.request.CreateConventionApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateConventionApiRequestFixture;
import com.ryuqq.application.convention.dto.command.CreateConventionCommand;
import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ConventionCommandApiMapper 단위 테스트
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
@DisplayName("ConventionCommandApiMapper 단위 테스트")
class ConventionCommandApiMapperTest {

    private static final Long DEFAULT_MODULE_ID = 1L;

    private ConventionCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ConventionCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateConventionApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateConventionApiRequest request = CreateConventionApiRequestFixture.valid();

            // When
            CreateConventionCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.moduleId()).isEqualTo(DEFAULT_MODULE_ID);
            assertThat(command.version()).isEqualTo("1.0.0");
            assertThat(command.description()).isEqualTo("Module Convention");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateConventionApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long conventionId = 1L;
            UpdateConventionApiRequest request = UpdateConventionApiRequestFixture.valid();

            // When
            UpdateConventionCommand command = mapper.toCommand(conventionId, request);

            // Then
            assertThat(command.id()).isEqualTo(conventionId);
            assertThat(command.moduleId()).isEqualTo(DEFAULT_MODULE_ID);
            assertThat(command.version()).isEqualTo("1.0.0");
            assertThat(command.description()).isEqualTo("Module Convention");
            assertThat(command.active()).isTrue();
        }
    }
}
