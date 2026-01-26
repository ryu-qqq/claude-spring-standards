package com.ryuqq.application.ruleexample.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.fixture.RuleExampleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RuleExampleAssembler 단위 테스트
 *
 * <p>RuleExample 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("RuleExampleAssembler 단위 테스트")
class RuleExampleAssemblerTest {

    private RuleExampleAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new RuleExampleAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - RuleExample을 RuleExampleResult로 변환")
        void toResult_WithValidRuleExample_ShouldReturnResult() {
            // given
            RuleExample ruleExample = RuleExampleFixture.reconstitute();

            // when
            RuleExampleResult result = sut.toResult(ruleExample);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - GOOD 예시 변환")
        void toResult_WithGoodExample_ShouldReturnResult() {
            // given
            RuleExample ruleExample = RuleExampleFixture.goodExample();

            // when
            RuleExampleResult result = sut.toResult(ruleExample);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - BAD 예시 변환")
        void toResult_WithBadExample_ShouldReturnResult() {
            // given
            RuleExample ruleExample = RuleExampleFixture.badExample();

            // when
            RuleExampleResult result = sut.toResult(ruleExample);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - RuleExample 목록을 RuleExampleResult 목록으로 변환")
        void toResults_WithValidRuleExamples_ShouldReturnResults() {
            // given
            RuleExample ruleExample1 = RuleExampleFixture.goodExample();
            RuleExample ruleExample2 = RuleExampleFixture.badExample();
            List<RuleExample> ruleExamples = List.of(ruleExample1, ruleExample2);

            // when
            List<RuleExampleResult> results = sut.toResults(ruleExamples);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<RuleExample> ruleExamples = List.of();

            // when
            List<RuleExampleResult> results = sut.toResults(ruleExamples);

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
            RuleExample ruleExample1 = RuleExampleFixture.goodExample();
            RuleExample ruleExample2 = RuleExampleFixture.badExample();
            RuleExample ruleExample3 = RuleExampleFixture.fromFeedbackExample();
            List<RuleExample> ruleExamples = List.of(ruleExample1, ruleExample2, ruleExample3);
            int size = 2;

            // when
            RuleExampleSliceResult result = sut.toSliceResult(ruleExamples, size);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.ruleExamples()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            RuleExample ruleExample1 = RuleExampleFixture.goodExample();
            List<RuleExample> ruleExamples = List.of(ruleExample1);
            int size = 10;

            // when
            RuleExampleSliceResult result = sut.toSliceResult(ruleExamples, size);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.ruleExamples()).hasSize(1);
        }
    }
}
