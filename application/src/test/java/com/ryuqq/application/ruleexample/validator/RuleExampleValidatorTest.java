package com.ryuqq.application.ruleexample.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.ruleexample.manager.RuleExampleReadManager;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.fixture.RuleExampleFixture;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RuleExampleValidator 단위 테스트
 *
 * <p>RuleExample 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("RuleExampleValidator 단위 테스트")
class RuleExampleValidatorTest {

    @Mock private RuleExampleReadManager ruleExampleReadManager;

    private RuleExampleValidator sut;

    @BeforeEach
    void setUp() {
        sut = new RuleExampleValidator(ruleExampleReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 RuleExample 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnRuleExample() {
            // given
            RuleExampleId id = RuleExampleId.of(1L);
            RuleExample expected = RuleExampleFixture.reconstitute();

            given(ruleExampleReadManager.getById(id)).willReturn(expected);

            // when
            RuleExample result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공 - 존재하는 RuleExample")
        void validateExists_WhenExists_ShouldNotThrow() {
            // given
            RuleExampleId id = RuleExampleId.of(1L);
            RuleExample ruleExample = RuleExampleFixture.reconstitute();

            given(ruleExampleReadManager.getById(id)).willReturn(ruleExample);

            // when & then - no exception
            assertThatCode(() -> sut.validateExists(id)).doesNotThrowAnyException();
        }
    }
}
