package com.ryuqq.application.zerotolerance.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleDetailResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.fixture.ChecklistItemFixture;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.fixture.CodingRuleFixture;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.fixture.RuleExampleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ZeroToleranceRuleAssembler 단위 테스트
 *
 * <p>ZeroToleranceRule 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ZeroToleranceRuleAssembler 단위 테스트")
class ZeroToleranceRuleAssemblerTest {

    private ZeroToleranceRuleAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ZeroToleranceRuleAssembler();
    }

    @Nested
    @DisplayName("toDetailResult 메서드")
    class ToDetailResult {

        @Test
        @DisplayName("성공 - CodingRule과 관련 데이터를 ZeroToleranceRuleDetailResult로 변환")
        void toDetailResult_WithValidData_ShouldReturnResult() {
            // given
            CodingRule codingRule = CodingRuleFixture.reconstitute();
            List<RuleExample> ruleExamples =
                    List.of(RuleExampleFixture.goodExample(), RuleExampleFixture.badExample());
            List<ChecklistItem> checklistItems = List.of(ChecklistItemFixture.reconstitute());

            // when
            ZeroToleranceRuleDetailResult result =
                    sut.toDetailResult(codingRule, ruleExamples, checklistItems);

            // then
            assertThat(result).isNotNull();
            assertThat(result.codingRule()).isNotNull();
            assertThat(result.examples()).hasSize(2);
            assertThat(result.checklistItems()).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 예시와 체크리스트가 빈 경우")
        void toDetailResult_WithEmptyRelatedData_ShouldReturnResult() {
            // given
            CodingRule codingRule = CodingRuleFixture.reconstitute();
            List<RuleExample> ruleExamples = List.of();
            List<ChecklistItem> checklistItems = List.of();

            // when
            ZeroToleranceRuleDetailResult result =
                    sut.toDetailResult(codingRule, ruleExamples, checklistItems);

            // then
            assertThat(result).isNotNull();
            assertThat(result.codingRule()).isNotNull();
            assertThat(result.examples()).isEmpty();
            assertThat(result.checklistItems()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toDetailResultWithRuleOnly 메서드")
    class ToDetailResultWithRuleOnly {

        @Test
        @DisplayName("성공 - CodingRule만 있는 결과 변환")
        void toDetailResultWithRuleOnly_WithValidCodingRule_ShouldReturnResult() {
            // given
            CodingRule codingRule = CodingRuleFixture.zeroToleranceRule();

            // when
            ZeroToleranceRuleDetailResult result = sut.toDetailResultWithRuleOnly(codingRule);

            // then
            assertThat(result).isNotNull();
            assertThat(result.codingRule()).isNotNull();
            assertThat(result.examples()).isEmpty();
            assertThat(result.checklistItems()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenHasNext_ShouldReturnSliceWithHasNextTrue() {
            // given
            CodingRule codingRule1 = CodingRuleFixture.reconstitute();
            CodingRule codingRule2 = CodingRuleFixture.zeroToleranceRule();
            CodingRule codingRule3 = CodingRuleFixture.autoFixableRule();

            List<ZeroToleranceRuleDetailResult> detailResults =
                    List.of(
                            sut.toDetailResultWithRuleOnly(codingRule1),
                            sut.toDetailResultWithRuleOnly(codingRule2),
                            sut.toDetailResultWithRuleOnly(codingRule3));
            int size = 2;

            // when
            ZeroToleranceRuleSliceResult result = sut.toSliceResult(detailResults, size);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.rules()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            CodingRule codingRule1 = CodingRuleFixture.reconstitute();

            List<ZeroToleranceRuleDetailResult> detailResults =
                    List.of(sut.toDetailResultWithRuleOnly(codingRule1));
            int size = 10;

            // when
            ZeroToleranceRuleSliceResult result = sut.toSliceResult(detailResults, size);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.rules()).hasSize(1);
        }
    }
}
