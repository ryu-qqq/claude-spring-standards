package com.ryuqq.application.layerdependency.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleResult;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.fixture.LayerDependencyRuleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LayerDependencyRuleAssembler 단위 테스트
 *
 * <p>LayerDependencyRule 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("LayerDependencyRuleAssembler 단위 테스트")
class LayerDependencyRuleAssemblerTest {

    private LayerDependencyRuleAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new LayerDependencyRuleAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - LayerDependencyRule을 LayerDependencyRuleResult로 변환")
        void toResult_WithValidRule_ShouldReturnResult() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.reconstitute();

            // when
            LayerDependencyRuleResult result = sut.toResult(rule);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - 허용된 의존성 규칙 변환")
        void toResult_WithAllowedRule_ShouldReturnResult() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.allowedRule();

            // when
            LayerDependencyRuleResult result = sut.toResult(rule);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - 금지된 의존성 규칙 변환")
        void toResult_WithForbiddenRule_ShouldReturnResult() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.forbiddenRule();

            // when
            LayerDependencyRuleResult result = sut.toResult(rule);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - 조건부 의존성 규칙 변환")
        void toResult_WithConditionalRule_ShouldReturnResult() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.conditionalRule();

            // when
            LayerDependencyRuleResult result = sut.toResult(rule);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - LayerDependencyRule 목록을 LayerDependencyRuleResult 목록으로 변환")
        void toResults_WithValidRules_ShouldReturnResults() {
            // given
            LayerDependencyRule rule1 = LayerDependencyRuleFixture.allowedRule();
            LayerDependencyRule rule2 = LayerDependencyRuleFixture.forbiddenRule();
            List<LayerDependencyRule> rules = List.of(rule1, rule2);

            // when
            List<LayerDependencyRuleResult> results = sut.toResults(rules);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<LayerDependencyRule> rules = List.of();

            // when
            List<LayerDependencyRuleResult> results = sut.toResults(rules);

            // then
            assertThat(results).isEmpty();
        }
    }
}
