package com.ryuqq.application.codingrule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.factory.command.CodingRuleCommandFactory;
import com.ryuqq.application.codingrule.manager.CodingRulePersistenceManager;
import com.ryuqq.application.codingrule.validator.CodingRuleValidator;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.exception.CodingRuleDuplicateCodeException;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateCodingRuleService 단위 테스트
 *
 * <p>CodingRule 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateCodingRuleService 단위 테스트")
class CreateCodingRuleServiceTest {

    @Mock private CodingRuleValidator codingRuleValidator;

    @Mock private CodingRuleCommandFactory codingRuleCommandFactory;

    @Mock private CodingRulePersistenceManager codingRulePersistenceManager;

    @Mock private CodingRule codingRule;

    private CreateCodingRuleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateCodingRuleService(
                        codingRuleValidator,
                        codingRuleCommandFactory,
                        codingRulePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 CodingRule 생성")
        void execute_WithValidCommand_ShouldCreateCodingRule() {
            // given
            CreateCodingRuleCommand command = createDefaultCommand();
            ConventionId conventionId = ConventionId.of(command.conventionId());
            RuleCode ruleCode = RuleCode.of(command.code());
            CodingRuleId savedId = CodingRuleId.of(1L);

            willDoNothing().given(codingRuleValidator).validateNotDuplicate(conventionId, ruleCode);
            given(codingRuleCommandFactory.create(command)).willReturn(codingRule);
            given(codingRulePersistenceManager.persist(codingRule)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(codingRuleValidator).should().validateNotDuplicate(conventionId, ruleCode);
            then(codingRuleCommandFactory).should().create(command);
            then(codingRulePersistenceManager).should().persist(codingRule);
        }

        @Test
        @DisplayName("실패 - 중복된 코드인 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            CreateCodingRuleCommand command = createDefaultCommand();
            ConventionId conventionId = ConventionId.of(command.conventionId());
            RuleCode ruleCode = RuleCode.of(command.code());

            willThrow(new CodingRuleDuplicateCodeException(conventionId, ruleCode))
                    .given(codingRuleValidator)
                    .validateNotDuplicate(conventionId, ruleCode);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(CodingRuleDuplicateCodeException.class);

            then(codingRuleCommandFactory).shouldHaveNoInteractions();
            then(codingRulePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateCodingRuleCommand createDefaultCommand() {
        return new CreateCodingRuleCommand(
                1L,
                null,
                "DOM-001",
                "Lombok 금지",
                "BLOCKER",
                "ANNOTATION",
                "Lombok 사용 금지",
                "명시적인 코드 작성을 위해",
                false,
                List.of("CLASS"),
                null,
                null,
                null);
    }
}
