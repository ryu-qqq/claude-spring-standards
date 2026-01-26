package com.ryuqq.application.codingrule.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.codingrule.factory.command.CodingRuleCommandFactory;
import com.ryuqq.application.codingrule.manager.CodingRulePersistenceManager;
import com.ryuqq.application.codingrule.validator.CodingRuleValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.aggregate.CodingRuleUpdateData;
import com.ryuqq.domain.codingrule.exception.CodingRuleDuplicateCodeException;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
import java.time.Instant;
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
 * UpdateCodingRuleService 단위 테스트
 *
 * <p>CodingRule 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateCodingRuleService 단위 테스트")
class UpdateCodingRuleServiceTest {

    @Mock private CodingRuleValidator codingRuleValidator;

    @Mock private CodingRuleCommandFactory codingRuleCommandFactory;

    @Mock private CodingRulePersistenceManager codingRulePersistenceManager;

    @Mock private CodingRule codingRule;

    @Mock private CodingRuleUpdateData updateData;

    private UpdateCodingRuleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateCodingRuleService(
                        codingRuleValidator,
                        codingRuleCommandFactory,
                        codingRulePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 CodingRule 수정")
        void execute_WithValidCommand_ShouldUpdateCodingRule() {
            // given
            UpdateCodingRuleCommand command = createDefaultCommand();
            CodingRuleId codingRuleId = CodingRuleId.of(command.codingRuleId());
            ConventionId conventionId = ConventionId.of(1L);
            RuleCode ruleCode = RuleCode.of(command.code());
            Instant changedAt = Instant.now();
            UpdateContext<CodingRuleId, CodingRuleUpdateData> context =
                    new UpdateContext<>(codingRuleId, updateData, changedAt);

            given(codingRuleCommandFactory.createUpdateContext(command)).willReturn(context);
            given(codingRuleValidator.findExistingOrThrow(codingRuleId)).willReturn(codingRule);
            given(codingRule.conventionId()).willReturn(conventionId);
            given(updateData.code()).willReturn(ruleCode);
            willDoNothing()
                    .given(codingRuleValidator)
                    .validateNotDuplicateExcluding(
                            any(ConventionId.class), any(RuleCode.class), any(CodingRuleId.class));
            willDoNothing().given(codingRule).update(updateData, changedAt);
            given(codingRulePersistenceManager.persist(codingRule)).willReturn(codingRuleId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(codingRuleCommandFactory).should().createUpdateContext(command);
            then(codingRuleValidator).should().findExistingOrThrow(codingRuleId);
            then(codingRuleValidator)
                    .should()
                    .validateNotDuplicateExcluding(conventionId, ruleCode, codingRuleId);
            then(codingRule).should().update(updateData, changedAt);
            then(codingRulePersistenceManager).should().persist(codingRule);
        }

        @Test
        @DisplayName("실패 - 코드가 중복되는 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            UpdateCodingRuleCommand command = createDefaultCommand();
            CodingRuleId codingRuleId = CodingRuleId.of(command.codingRuleId());
            ConventionId conventionId = ConventionId.of(1L);
            RuleCode ruleCode = RuleCode.of(command.code());
            Instant changedAt = Instant.now();
            UpdateContext<CodingRuleId, CodingRuleUpdateData> context =
                    new UpdateContext<>(codingRuleId, updateData, changedAt);

            given(codingRuleCommandFactory.createUpdateContext(command)).willReturn(context);
            given(codingRuleValidator.findExistingOrThrow(codingRuleId)).willReturn(codingRule);
            given(codingRule.conventionId()).willReturn(conventionId);
            given(updateData.code()).willReturn(ruleCode);
            willThrow(new CodingRuleDuplicateCodeException(conventionId, ruleCode))
                    .given(codingRuleValidator)
                    .validateNotDuplicateExcluding(
                            any(ConventionId.class), any(RuleCode.class), any(CodingRuleId.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(CodingRuleDuplicateCodeException.class);

            then(codingRulePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateCodingRuleCommand createDefaultCommand() {
        return new UpdateCodingRuleCommand(
                1L,
                1L,
                "DOM-001",
                "Aggregate Root Validation",
                "CRITICAL",
                "DOMAIN",
                "Aggregate Root must validate invariants",
                "DDD pattern requires aggregate root to maintain consistency",
                false,
                List.of("AGGREGATE"),
                null,
                null,
                null);
    }
}
