package com.ryuqq.adapter.in.rest.ruleexample.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreateRuleExampleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateRuleExampleApiRequestFixture;
import com.ryuqq.adapter.in.rest.ruleexample.dto.request.CreateRuleExampleApiRequest;
import com.ryuqq.adapter.in.rest.ruleexample.dto.request.UpdateRuleExampleApiRequest;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * RuleExampleCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>null 처리 (highlightLines)
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("RuleExampleCommandApiMapper 단위 테스트")
class RuleExampleCommandApiMapperTest {

    private RuleExampleCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RuleExampleCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateRuleExampleApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateRuleExampleApiRequest request = CreateRuleExampleApiRequestFixture.valid();

            // When
            CreateRuleExampleCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.ruleId()).isEqualTo(1L);
            assertThat(command.exampleType()).isEqualTo("GOOD");
            assertThat(command.code())
                    .isEqualTo("public class Order {\n    private final OrderId id;\n}");
            assertThat(command.language()).isEqualTo("JAVA");
            assertThat(command.explanation()).isEqualTo("Aggregate 클래스 예시");
            assertThat(command.highlightLines()).containsExactly(1, 2);
        }

        @Test
        @DisplayName("null 필드 처리 - highlightLines가 null이면 빈 리스트")
        void nullHighlightLines_ShouldReturnEmptyList() {
            // Given
            CreateRuleExampleApiRequest request = CreateRuleExampleApiRequestFixture.validMinimal();

            // When
            CreateRuleExampleCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.highlightLines()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateRuleExampleApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long ruleExampleId = 1L;
            UpdateRuleExampleApiRequest request = UpdateRuleExampleApiRequestFixture.valid();

            // When
            UpdateRuleExampleCommand command = mapper.toCommand(ruleExampleId, request);

            // Then
            assertThat(command.ruleExampleId()).isEqualTo(ruleExampleId);
            assertThat(command.exampleType()).isEqualTo("GOOD");
            assertThat(command.code()).isNotNull();
        }
    }
}
