package com.ryuqq.adapter.in.rest.layer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreateLayerApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateLayerApiRequestFixture;
import com.ryuqq.adapter.in.rest.layer.dto.request.CreateLayerApiRequest;
import com.ryuqq.adapter.in.rest.layer.dto.request.UpdateLayerApiRequest;
import com.ryuqq.application.layer.dto.command.CreateLayerCommand;
import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * LayerCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("LayerCommandApiMapper 단위 테스트")
class LayerCommandApiMapperTest {

    private LayerCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LayerCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateLayerApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("성공 - CreateLayerApiRequest를 CreateLayerCommand로 변환")
        void shouldConvertCreateRequestToCommand() {
            // Given
            CreateLayerApiRequest request = CreateLayerApiRequestFixture.valid();

            // When
            CreateLayerCommand command = mapper.toCommand(request);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.architectureId()).isEqualTo(request.architectureId());
            assertThat(command.code()).isEqualTo(request.code());
            assertThat(command.name()).isEqualTo(request.name());
            assertThat(command.description()).isEqualTo(request.description());
            assertThat(command.orderIndex()).isEqualTo(request.orderIndex());
        }

        @Test
        @DisplayName("성공 - null description 처리")
        void shouldHandleNullDescription() {
            // Given
            CreateLayerApiRequest request =
                    new CreateLayerApiRequest(1L, "DOMAIN", "Domain Layer", null, 1);

            // When
            CreateLayerCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.description()).isNull();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateLayerApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("성공 - UpdateLayerApiRequest를 UpdateLayerCommand로 변환")
        void shouldConvertUpdateRequestToCommand() {
            // Given
            Long layerId = 1L;
            UpdateLayerApiRequest request = UpdateLayerApiRequestFixture.valid();

            // When
            UpdateLayerCommand command = mapper.toCommand(layerId, request);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.id()).isEqualTo(layerId);
            assertThat(command.code()).isEqualTo(request.code());
            assertThat(command.name()).isEqualTo(request.name());
            assertThat(command.description()).isEqualTo(request.description());
            assertThat(command.orderIndex()).isEqualTo(request.orderIndex());
        }

        @Test
        @DisplayName("성공 - null description 처리")
        void shouldHandleNullDescription() {
            // Given
            Long layerId = 2L;
            UpdateLayerApiRequest request =
                    new UpdateLayerApiRequest("APPLICATION", "Application Layer", null, 2);

            // When
            UpdateLayerCommand command = mapper.toCommand(layerId, request);

            // Then
            assertThat(command.id()).isEqualTo(layerId);
            assertThat(command.description()).isNull();
        }
    }
}
