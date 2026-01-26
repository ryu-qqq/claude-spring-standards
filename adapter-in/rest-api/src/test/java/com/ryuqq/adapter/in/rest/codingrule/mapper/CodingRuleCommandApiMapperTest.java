package com.ryuqq.adapter.in.rest.codingrule.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.codingrule.dto.request.CreateCodingRuleApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.request.UpdateCodingRuleApiRequest;
import com.ryuqq.adapter.in.rest.fixture.request.CreateCodingRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateCodingRuleApiRequestFixture;
import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * CodingRuleCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>null 처리 (appliesTo, sdkConstraint)
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("CodingRuleCommandApiMapper 단위 테스트")
class CodingRuleCommandApiMapperTest {

    private CodingRuleCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CodingRuleCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateCodingRuleApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateCodingRuleApiRequest request = CreateCodingRuleApiRequestFixture.valid();

            // When
            CreateCodingRuleCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.conventionId()).isEqualTo(1L);
            assertThat(command.structureId()).isNull();
            assertThat(command.code()).isEqualTo("AGG-001");
            assertThat(command.name()).isEqualTo("Lombok 사용 금지");
            assertThat(command.severity()).isEqualTo("BLOCKER");
            assertThat(command.category()).isEqualTo("ANNOTATION");
            assertThat(command.description()).isEqualTo("Domain Layer에서 Lombok 사용을 금지합니다");
            assertThat(command.rationale()).isEqualTo("Pure Java 원칙");
            assertThat(command.autoFixable()).isFalse();
            assertThat(command.appliesTo()).containsExactly("AGGREGATE", "VALUE_OBJECT");
            assertThat(command.sdkArtifact()).isNull();
            assertThat(command.sdkMinVersion()).isNull();
            assertThat(command.sdkMaxVersion()).isNull();
        }

        @Test
        @DisplayName("null 필드 처리 - appliesTo가 null이면 빈 리스트")
        void nullAppliesTo_ShouldReturnEmptyList() {
            // Given
            CreateCodingRuleApiRequest request =
                    new CreateCodingRuleApiRequest(
                            1L,
                            null,
                            "AGG-001",
                            "Name",
                            "BLOCKER",
                            "ANNOTATION",
                            "Description",
                            null,
                            false,
                            null,
                            null);

            // When
            CreateCodingRuleCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.appliesTo()).isEmpty();
        }

        @Test
        @DisplayName("sdkConstraint가 null이면 sdk 관련 필드도 null")
        void nullSdkConstraint_ShouldSetSdkFieldsToNull() {
            // Given
            CreateCodingRuleApiRequest request = CreateCodingRuleApiRequestFixture.valid();

            // When
            CreateCodingRuleCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.sdkArtifact()).isNull();
            assertThat(command.sdkMinVersion()).isNull();
            assertThat(command.sdkMaxVersion()).isNull();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateCodingRuleApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long codingRuleId = 1L;
            UpdateCodingRuleApiRequest request = UpdateCodingRuleApiRequestFixture.valid();

            // When
            UpdateCodingRuleCommand command = mapper.toCommand(codingRuleId, request);

            // Then
            assertThat(command.codingRuleId()).isEqualTo(codingRuleId);
            assertThat(command.structureId()).isNull();
            assertThat(command.code()).isEqualTo("AGG-001");
            assertThat(command.name()).isEqualTo("Lombok 사용 금지");
            assertThat(command.severity()).isEqualTo("BLOCKER");
            assertThat(command.category()).isEqualTo("ANNOTATION");
            assertThat(command.description()).isEqualTo("Domain Layer에서 Lombok 사용을 금지합니다");
            assertThat(command.rationale()).isEqualTo("Pure Java 원칙");
            assertThat(command.autoFixable()).isFalse();
            assertThat(command.appliesTo()).containsExactly("AGGREGATE", "VALUE_OBJECT");
        }

        @Test
        @DisplayName("null 필드 처리 - appliesTo가 null이면 빈 리스트")
        void nullAppliesTo_ShouldReturnEmptyList() {
            // Given
            Long codingRuleId = 1L;
            UpdateCodingRuleApiRequest request =
                    new UpdateCodingRuleApiRequest(
                            null,
                            "AGG-001",
                            "Name",
                            "BLOCKER",
                            "ANNOTATION",
                            "Description",
                            null,
                            false,
                            null,
                            null);

            // When
            UpdateCodingRuleCommand command = mapper.toCommand(codingRuleId, request);

            // Then
            assertThat(command.appliesTo()).isEmpty();
        }
    }
}
