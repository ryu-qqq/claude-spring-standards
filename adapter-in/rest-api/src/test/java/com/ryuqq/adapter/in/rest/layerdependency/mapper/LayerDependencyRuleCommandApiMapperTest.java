package com.ryuqq.adapter.in.rest.layerdependency.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreateLayerDependencyRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateLayerDependencyRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.CreateLayerDependencyRuleApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.UpdateLayerDependencyRuleApiRequest;
import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * LayerDependencyRuleCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>PathVariable ID 포함
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("LayerDependencyRuleCommandApiMapper 단위 테스트")
class LayerDependencyRuleCommandApiMapperTest {

    private LayerDependencyRuleCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LayerDependencyRuleCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, CreateLayerDependencyRuleApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long architectureId = 1L;
            CreateLayerDependencyRuleApiRequest request =
                    CreateLayerDependencyRuleApiRequestFixture.valid();

            // When
            CreateLayerDependencyRuleCommand command = mapper.toCommand(architectureId, request);

            // Then
            assertThat(command.architectureId()).isEqualTo(architectureId);
            assertThat(command.fromLayer()).isEqualTo("DOMAIN");
            assertThat(command.toLayer()).isEqualTo("APPLICATION");
            assertThat(command.dependencyType()).isEqualTo("ALLOWED");
            assertThat(command.conditionDescription()).isNull();
        }

        @Test
        @DisplayName("CONDITIONAL 타입 - conditionDescription 포함")
        void conditionalType_ShouldMapConditionDescription() {
            // Given
            Long architectureId = 1L;
            CreateLayerDependencyRuleApiRequest request =
                    CreateLayerDependencyRuleApiRequestFixture.validConditional();

            // When
            CreateLayerDependencyRuleCommand command = mapper.toCommand(architectureId, request);

            // Then
            assertThat(command.dependencyType()).isEqualTo("CONDITIONAL");
            assertThat(command.conditionDescription()).isEqualTo("특정 조건에서만 허용");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, Long, UpdateLayerDependencyRuleApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long architectureId = 1L;
            Long ldrId = 1L;
            UpdateLayerDependencyRuleApiRequest request =
                    UpdateLayerDependencyRuleApiRequestFixture.valid();

            // When
            UpdateLayerDependencyRuleCommand command =
                    mapper.toCommand(architectureId, ldrId, request);

            // Then
            assertThat(command.architectureId()).isEqualTo(architectureId);
            assertThat(command.layerDependencyRuleId()).isEqualTo(ldrId);
            assertThat(command.fromLayer()).isEqualTo("DOMAIN");
            assertThat(command.toLayer()).isEqualTo("APPLICATION");
            assertThat(command.dependencyType()).isEqualTo("FORBIDDEN");
            assertThat(command.conditionDescription()).isEqualTo("조건 설명");
        }
    }
}
