package com.ryuqq.application.layer.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.layer.dto.command.CreateLayerCommand;
import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.aggregate.LayerUpdateData;
import com.ryuqq.domain.layer.id.LayerId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LayerCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("LayerCommandFactory 단위 테스트")
class LayerCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private LayerCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new LayerCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateLayerCommand로 Layer 생성")
        void create_WithValidCommand_ShouldReturnLayer() {
            // given
            CreateLayerCommand command =
                    new CreateLayerCommand(1L, "DOMAIN", "Domain Layer", "도메인 레이어 설명", 1);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Layer result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.code().value()).isEqualTo(command.code());
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.description()).isEqualTo(command.description());
            assertThat(result.orderIndex()).isEqualTo(command.orderIndex());
            assertThat(result.architectureId().value()).isEqualTo(command.architectureId());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateLayerCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateLayerCommand command =
                    new UpdateLayerCommand(
                            1L, "APPLICATION", "Application Layer", "애플리케이션 레이어 설명", 2);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<LayerId, LayerUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.id());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.updateData().code().value()).isEqualTo(command.code());
            assertThat(result.updateData().name().value()).isEqualTo(command.name());
            assertThat(result.updateData().description()).isEqualTo(command.description());
            assertThat(result.updateData().orderIndex()).isEqualTo(command.orderIndex());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
