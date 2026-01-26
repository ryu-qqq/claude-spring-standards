package com.ryuqq.application.layerdependency.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRuleUpdateData;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
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
 * LayerDependencyRuleCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("LayerDependencyRuleCommandFactory 단위 테스트")
class LayerDependencyRuleCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private LayerDependencyRuleCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new LayerDependencyRuleCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateLayerDependencyRuleCommand로 LayerDependencyRule 생성")
        void create_WithValidCommand_ShouldReturnLayerDependencyRule() {
            // given
            CreateLayerDependencyRuleCommand command =
                    new CreateLayerDependencyRuleCommand(
                            1L, "DOMAIN", "APPLICATION", "ALLOWED", "도메인은 애플리케이션에 의존 가능");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            LayerDependencyRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.architectureId().value()).isEqualTo(command.architectureId());
            assertThat(result.fromLayer()).isEqualTo(LayerType.DOMAIN);
            assertThat(result.toLayer()).isEqualTo(LayerType.APPLICATION);
            assertThat(result.dependencyType()).isEqualTo(DependencyType.ALLOWED);
            assertThat(result.conditionDescription().value())
                    .isEqualTo(command.conditionDescription());
        }

        @Test
        @DisplayName("성공 - FORBIDDEN 의존성 타입으로 LayerDependencyRule 생성")
        void create_WithForbiddenType_ShouldReturnLayerDependencyRule() {
            // given
            CreateLayerDependencyRuleCommand command =
                    new CreateLayerDependencyRuleCommand(
                            1L, "DOMAIN", "ADAPTER_IN", "FORBIDDEN", "도메인은 어댑터에 의존 금지");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            LayerDependencyRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.dependencyType()).isEqualTo(DependencyType.FORBIDDEN);
        }

        @Test
        @DisplayName("성공 - CONDITIONAL 의존성 타입으로 LayerDependencyRule 생성")
        void create_WithConditionalType_ShouldReturnLayerDependencyRule() {
            // given
            CreateLayerDependencyRuleCommand command =
                    new CreateLayerDependencyRuleCommand(
                            1L,
                            "APPLICATION",
                            "ADAPTER_OUT",
                            "CONDITIONAL",
                            "Port 인터페이스를 통해서만 의존 가능");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            LayerDependencyRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.dependencyType()).isEqualTo(DependencyType.CONDITIONAL);
            assertThat(result.conditionDescription().value()).isEqualTo("Port 인터페이스를 통해서만 의존 가능");
        }

        @Test
        @DisplayName("성공 - 조건 설명 없이 LayerDependencyRule 생성")
        void
                create_WithoutConditionDescription_ShouldReturnLayerDependencyRuleWithEmptyDescription() {
            // given
            CreateLayerDependencyRuleCommand command =
                    new CreateLayerDependencyRuleCommand(1L, "COMMON", "DOMAIN", "ALLOWED", null);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            LayerDependencyRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.conditionDescription().isEmpty()).isTrue();
        }

        @Test
        @DisplayName("성공 - 빈 조건 설명으로 LayerDependencyRule 생성")
        void
                create_WithBlankConditionDescription_ShouldReturnLayerDependencyRuleWithEmptyDescription() {
            // given
            CreateLayerDependencyRuleCommand command =
                    new CreateLayerDependencyRuleCommand(
                            1L, "INFRASTRUCTURE", "COMMON", "ALLOWED", "   ");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            LayerDependencyRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.conditionDescription().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("createUpdateData 메서드")
    class CreateUpdateData {

        @Test
        @DisplayName("성공 - UpdateLayerDependencyRuleCommand로 LayerDependencyRuleUpdateData 생성")
        void createUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateLayerDependencyRuleCommand command =
                    new UpdateLayerDependencyRuleCommand(
                            1L, 10L, "APPLICATION", "DOMAIN", "FORBIDDEN", "수정된 조건");

            // when
            LayerDependencyRuleUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.fromLayer()).isEqualTo(LayerType.APPLICATION);
            assertThat(result.toLayer()).isEqualTo(LayerType.DOMAIN);
            assertThat(result.dependencyType()).isEqualTo(DependencyType.FORBIDDEN);
            assertThat(result.conditionDescription().value()).isEqualTo("수정된 조건");
        }

        @Test
        @DisplayName("성공 - 조건 설명 없이 LayerDependencyRuleUpdateData 생성")
        void
                createUpdateData_WithoutConditionDescription_ShouldReturnUpdateDataWithEmptyDescription() {
            // given
            UpdateLayerDependencyRuleCommand command =
                    new UpdateLayerDependencyRuleCommand(
                            1L, 10L, "ADAPTER_IN", "APPLICATION", "ALLOWED", null);

            // when
            LayerDependencyRuleUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.conditionDescription().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateLayerDependencyRuleCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateLayerDependencyRuleCommand command =
                    new UpdateLayerDependencyRuleCommand(
                            1L, 10L, "DOMAIN", "APPLICATION", "ALLOWED", "조건");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<LayerDependencyRuleId, LayerDependencyRuleUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.layerDependencyRuleId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
