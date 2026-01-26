package com.ryuqq.application.ruleexample.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.application.ruleexample.factory.command.RuleExampleCommandFactory;
import com.ryuqq.application.ruleexample.manager.RuleExamplePersistenceManager;
import com.ryuqq.application.ruleexample.validator.RuleExampleValidator;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.aggregate.RuleExampleUpdateData;
import com.ryuqq.domain.ruleexample.exception.RuleExampleNotFoundException;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
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
 * UpdateRuleExampleService 단위 테스트
 *
 * <p>RuleExample 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateRuleExampleService 단위 테스트")
class UpdateRuleExampleServiceTest {

    @Mock private RuleExampleCommandFactory ruleExampleCommandFactory;

    @Mock private RuleExampleValidator ruleExampleValidator;

    @Mock private RuleExamplePersistenceManager ruleExamplePersistenceManager;

    @Mock private RuleExample ruleExample;

    @Mock private RuleExampleUpdateData updateData;

    private UpdateRuleExampleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateRuleExampleService(
                        ruleExampleCommandFactory,
                        ruleExampleValidator,
                        ruleExamplePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 RuleExample 수정")
        void execute_WithValidCommand_ShouldUpdateRuleExample() {
            // given
            UpdateRuleExampleCommand command = createDefaultCommand();
            RuleExampleId ruleExampleId = RuleExampleId.of(command.ruleExampleId());
            Instant changedAt = Instant.now();
            UpdateContext<RuleExampleId, RuleExampleUpdateData> context =
                    new UpdateContext<>(ruleExampleId, updateData, changedAt);

            given(ruleExampleCommandFactory.createUpdateContext(command)).willReturn(context);
            given(ruleExampleValidator.findExistingOrThrow(ruleExampleId)).willReturn(ruleExample);
            willDoNothing().given(ruleExample).update(updateData, changedAt);
            given(ruleExamplePersistenceManager.persist(ruleExample)).willReturn(ruleExampleId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(ruleExampleCommandFactory).should().createUpdateContext(command);
            then(ruleExampleValidator).should().findExistingOrThrow(ruleExampleId);
            then(ruleExample).should().update(updateData, changedAt);
            then(ruleExamplePersistenceManager).should().persist(ruleExample);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 RuleExample인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateRuleExampleCommand command = createDefaultCommand();
            RuleExampleId ruleExampleId = RuleExampleId.of(command.ruleExampleId());
            Instant changedAt = Instant.now();
            UpdateContext<RuleExampleId, RuleExampleUpdateData> context =
                    new UpdateContext<>(ruleExampleId, updateData, changedAt);

            given(ruleExampleCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new RuleExampleNotFoundException(ruleExampleId.value()))
                    .given(ruleExampleValidator)
                    .findExistingOrThrow(ruleExampleId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(RuleExampleNotFoundException.class);

            then(ruleExamplePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateRuleExampleCommand createDefaultCommand() {
        return new UpdateRuleExampleCommand(
                1L, "GOOD", "public class Example { }", "java", "좋은 예시입니다.", List.of(1, 2, 3));
    }
}
