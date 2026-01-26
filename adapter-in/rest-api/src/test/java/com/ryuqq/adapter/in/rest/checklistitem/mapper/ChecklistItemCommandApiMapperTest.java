package com.ryuqq.adapter.in.rest.checklistitem.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.checklistitem.dto.request.CreateChecklistItemApiRequest;
import com.ryuqq.adapter.in.rest.checklistitem.dto.request.UpdateChecklistItemApiRequest;
import com.ryuqq.adapter.in.rest.fixture.request.CreateChecklistItemApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateChecklistItemApiRequestFixture;
import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ChecklistItemCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>null 처리 (automationTool, automationRuleId, isCritical)
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ChecklistItemCommandApiMapper 단위 테스트")
class ChecklistItemCommandApiMapperTest {

    private ChecklistItemCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ChecklistItemCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateChecklistItemApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateChecklistItemApiRequest request = CreateChecklistItemApiRequestFixture.valid();

            // When
            CreateChecklistItemCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.ruleId()).isEqualTo(1L);
            assertThat(command.sequenceOrder()).isEqualTo(1);
            assertThat(command.checkDescription()).isEqualTo("Lombok 어노테이션 사용 여부 확인");
            assertThat(command.checkType()).isEqualTo("AUTOMATED");
            assertThat(command.automationTool()).isEqualTo("ARCHUNIT");
            assertThat(command.automationRuleId()).isEqualTo("AGG-001-CHECK-1");
            assertThat(command.critical()).isFalse();
        }

        @Test
        @DisplayName("null 필드 처리 - automationTool이 null이면 빈 문자열")
        void nullAutomationTool_ShouldReturnEmptyString() {
            // Given
            CreateChecklistItemApiRequest request =
                    CreateChecklistItemApiRequestFixture.validMinimal();

            // When
            CreateChecklistItemCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.automationTool()).isEmpty();
        }

        @Test
        @DisplayName("null 필드 처리 - automationRuleId가 null이면 빈 문자열")
        void nullAutomationRuleId_ShouldReturnEmptyString() {
            // Given
            CreateChecklistItemApiRequest request =
                    CreateChecklistItemApiRequestFixture.validMinimal();

            // When
            CreateChecklistItemCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.automationRuleId()).isEmpty();
        }

        @Test
        @DisplayName("null 필드 처리 - isCritical이 null이면 false")
        void nullIsCritical_ShouldReturnFalse() {
            // Given
            CreateChecklistItemApiRequest request =
                    CreateChecklistItemApiRequestFixture.validMinimal();

            // When
            CreateChecklistItemCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.critical()).isFalse();
        }

        @Test
        @DisplayName("isCritical이 true이면 true 반환")
        void isCriticalTrue_ShouldReturnTrue() {
            // Given
            CreateChecklistItemApiRequest request =
                    CreateChecklistItemApiRequestFixture.validWithCritical();

            // When
            CreateChecklistItemCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.critical()).isTrue();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateChecklistItemApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long checklistItemId = 1L;
            UpdateChecklistItemApiRequest request = UpdateChecklistItemApiRequestFixture.valid();

            // When
            UpdateChecklistItemCommand command = mapper.toCommand(checklistItemId, request);

            // Then
            assertThat(command.checklistItemId()).isEqualTo(checklistItemId);
            assertThat(command.sequenceOrder()).isNotNull();
            assertThat(command.checkDescription()).isNotNull();
            assertThat(command.checkType()).isNotNull();
        }
    }
}
