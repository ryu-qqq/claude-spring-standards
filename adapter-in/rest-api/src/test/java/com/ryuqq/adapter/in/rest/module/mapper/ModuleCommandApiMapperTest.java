package com.ryuqq.adapter.in.rest.module.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreateModuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateModuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.module.dto.request.CreateModuleApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.request.UpdateModuleApiRequest;
import com.ryuqq.application.module.dto.command.CreateModuleCommand;
import com.ryuqq.application.module.dto.command.UpdateModuleCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ModuleCommandApiMapper 단위 테스트
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
@DisplayName("ModuleCommandApiMapper 단위 테스트")
class ModuleCommandApiMapperTest {

    private ModuleCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ModuleCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateModuleApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateModuleApiRequest request = CreateModuleApiRequestFixture.valid();

            // When
            CreateModuleCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.layerId()).isEqualTo(1L);
            assertThat(command.parentModuleId()).isNull();
            assertThat(command.name()).isEqualTo("adapter-in-rest-api");
            assertThat(command.description()).isEqualTo("REST API Adapter");
            assertThat(command.modulePath()).isEqualTo("adapter-in/rest-api");
            assertThat(command.buildIdentifier()).isEqualTo(":adapter-in:rest-api");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateModuleApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long moduleId = 1L;
            UpdateModuleApiRequest request = UpdateModuleApiRequestFixture.valid();

            // When
            UpdateModuleCommand command = mapper.toCommand(moduleId, request);

            // Then
            assertThat(command.moduleId()).isEqualTo(moduleId);
            assertThat(command.name()).isNotNull();
        }
    }
}
