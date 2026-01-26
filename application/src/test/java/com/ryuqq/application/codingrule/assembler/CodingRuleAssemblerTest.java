package com.ryuqq.application.codingrule.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.fixture.CodingRuleFixture;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CodingRuleAssembler 단위 테스트
 *
 * <p>CodingRule 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("CodingRuleAssembler 단위 테스트")
class CodingRuleAssemblerTest {

    private CodingRuleAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new CodingRuleAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - CodingRule을 CodingRuleResult로 변환")
        void toResult_WithValidCodingRule_ShouldReturnResult() {
            // given
            CodingRule codingRule = CodingRuleFixture.reconstitute();

            // when
            CodingRuleResult result = sut.toResult(codingRule);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(codingRule.idValue());
        }

        @Test
        @DisplayName("성공 - Zero-Tolerance 규칙 변환")
        void toResult_WithZeroToleranceRule_ShouldReturnResult() {
            // given
            CodingRule codingRule = CodingRuleFixture.zeroToleranceRule();

            // when
            CodingRuleResult result = sut.toResult(codingRule);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - AutoFixable 규칙 변환")
        void toResult_WithAutoFixableRule_ShouldReturnResult() {
            // given
            CodingRule codingRule = CodingRuleFixture.autoFixableRule();

            // when
            CodingRuleResult result = sut.toResult(codingRule);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - CodingRule 목록을 CodingRuleResult 목록으로 변환")
        void toResults_WithValidCodingRules_ShouldReturnResults() {
            // given
            CodingRule codingRule1 = CodingRuleFixture.reconstitute(CodingRuleId.of(1L));
            CodingRule codingRule2 = CodingRuleFixture.reconstitute(CodingRuleId.of(2L));
            List<CodingRule> codingRules = List.of(codingRule1, codingRule2);

            // when
            List<CodingRuleResult> results = sut.toResults(codingRules);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<CodingRule> codingRules = List.of();

            // when
            List<CodingRuleResult> results = sut.toResults(codingRules);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenHasNext_ShouldReturnSliceWithHasNextTrue() {
            // given
            CodingRule codingRule1 = CodingRuleFixture.reconstitute(CodingRuleId.of(1L));
            CodingRule codingRule2 = CodingRuleFixture.reconstitute(CodingRuleId.of(2L));
            CodingRule codingRule3 = CodingRuleFixture.reconstitute(CodingRuleId.of(3L));
            List<CodingRule> codingRules = List.of(codingRule1, codingRule2, codingRule3);
            int size = 2;

            // when
            CodingRuleSliceResult result = sut.toSliceResult(codingRules, size);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.codingRules()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            CodingRule codingRule1 = CodingRuleFixture.reconstitute(CodingRuleId.of(1L));
            List<CodingRule> codingRules = List.of(codingRule1);
            int size = 10;

            // when
            CodingRuleSliceResult result = sut.toSliceResult(codingRules, size);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.codingRules()).hasSize(1);
        }
    }
}
