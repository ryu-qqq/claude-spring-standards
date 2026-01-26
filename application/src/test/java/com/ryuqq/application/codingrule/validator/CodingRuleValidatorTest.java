package com.ryuqq.application.codingrule.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.exception.CodingRuleDuplicateCodeException;
import com.ryuqq.domain.codingrule.exception.CodingRuleNotFoundException;
import com.ryuqq.domain.codingrule.fixture.CodingRuleFixture;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CodingRuleValidator 단위 테스트
 *
 * <p>CodingRule 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("CodingRuleValidator 단위 테스트")
class CodingRuleValidatorTest {

    @Mock private CodingRuleReadManager codingRuleReadManager;

    private CodingRuleValidator sut;

    @BeforeEach
    void setUp() {
        sut = new CodingRuleValidator(codingRuleReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 CodingRule 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnCodingRule() {
            // given
            CodingRuleId id = CodingRuleId.of(1L);
            CodingRule expected = CodingRuleFixture.reconstitute();

            given(codingRuleReadManager.findById(id)).willReturn(Optional.of(expected));

            // when
            CodingRule result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void findExistingOrThrow_WhenNotExists_ShouldThrowException() {
            // given
            CodingRuleId id = CodingRuleId.of(999L);

            given(codingRuleReadManager.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(CodingRuleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공 - 존재하는 CodingRule")
        void validateExists_WhenExists_ShouldNotThrow() {
            // given
            CodingRuleId id = CodingRuleId.of(1L);

            given(codingRuleReadManager.existsById(id)).willReturn(true);

            // when & then - no exception
            assertThatCode(() -> sut.validateExists(id)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void validateExists_WhenNotExists_ShouldThrowException() {
            // given
            CodingRuleId id = CodingRuleId.of(999L);

            given(codingRuleReadManager.existsById(id)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateExists(id))
                    .isInstanceOf(CodingRuleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicate 메서드")
    class ValidateNotDuplicate {

        @Test
        @DisplayName("성공 - 중복되지 않는 코드")
        void validateNotDuplicate_WhenNotDuplicate_ShouldNotThrow() {
            // given
            ConventionId conventionId = ConventionId.of(1L);
            RuleCode code = RuleCode.of("NEW-001");

            given(codingRuleReadManager.existsByConventionIdAndCode(conventionId, code))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicate(conventionId, code))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우 예외")
        void validateNotDuplicate_WhenDuplicate_ShouldThrowException() {
            // given
            ConventionId conventionId = ConventionId.of(1L);
            RuleCode code = RuleCode.of("DOM-001");

            given(codingRuleReadManager.existsByConventionIdAndCode(conventionId, code))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicate(conventionId, code))
                    .isInstanceOf(CodingRuleDuplicateCodeException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicateExcluding 메서드")
    class ValidateNotDuplicateExcluding {

        @Test
        @DisplayName("성공 - 자신을 제외하고 중복되지 않는 코드")
        void validateNotDuplicateExcluding_WhenNotDuplicate_ShouldNotThrow() {
            // given
            ConventionId conventionId = ConventionId.of(1L);
            RuleCode code = RuleCode.of("DOM-001");
            CodingRuleId excludeId = CodingRuleId.of(1L);

            given(
                            codingRuleReadManager.existsByConventionIdAndCodeExcluding(
                                    conventionId, code, excludeId))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicateExcluding(conventionId, code, excludeId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 다른 CodingRule에서 이미 사용 중인 경우 예외")
        void validateNotDuplicateExcluding_WhenDuplicate_ShouldThrowException() {
            // given
            ConventionId conventionId = ConventionId.of(1L);
            RuleCode code = RuleCode.of("DOM-001");
            CodingRuleId excludeId = CodingRuleId.of(1L);

            given(
                            codingRuleReadManager.existsByConventionIdAndCodeExcluding(
                                    conventionId, code, excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () -> sut.validateNotDuplicateExcluding(conventionId, code, excludeId))
                    .isInstanceOf(CodingRuleDuplicateCodeException.class);
        }
    }
}
