package com.ryuqq.application.zerotolerance.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.zerotolerance.dto.command.UpdateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.factory.command.ZeroToleranceRuleCommandFactory;
import com.ryuqq.application.zerotolerance.manager.ZeroToleranceRulePersistenceManager;
import com.ryuqq.application.zerotolerance.validator.ZeroToleranceRuleValidator;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRuleUpdateData;
import com.ryuqq.domain.zerotolerance.exception.ZeroToleranceRuleNotFoundException;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateZeroToleranceRuleService 단위 테스트
 *
 * <p>ZeroToleranceRule 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateZeroToleranceRuleService 단위 테스트")
class UpdateZeroToleranceRuleServiceTest {

    @Mock private ZeroToleranceRuleValidator zeroToleranceRuleValidator;

    @Mock private ZeroToleranceRuleCommandFactory zeroToleranceRuleCommandFactory;

    @Mock private ZeroToleranceRulePersistenceManager zeroToleranceRulePersistenceManager;

    @Mock private ZeroToleranceRule zeroToleranceRule;

    @Mock private ZeroToleranceRuleUpdateData updateData;

    private UpdateZeroToleranceRuleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateZeroToleranceRuleService(
                        zeroToleranceRuleValidator,
                        zeroToleranceRuleCommandFactory,
                        zeroToleranceRulePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ZeroToleranceRule 수정")
        void execute_WithValidCommand_ShouldUpdateZeroToleranceRule() {
            // given
            UpdateZeroToleranceRuleCommand command = createDefaultCommand();
            ZeroToleranceRuleId zeroToleranceRuleId =
                    ZeroToleranceRuleId.of(command.zeroToleranceRuleId());
            Instant changedAt = Instant.now();
            UpdateContext<ZeroToleranceRuleId, ZeroToleranceRuleUpdateData> context =
                    new UpdateContext<>(zeroToleranceRuleId, updateData, changedAt);

            given(zeroToleranceRuleCommandFactory.createUpdateContext(command)).willReturn(context);
            given(zeroToleranceRuleValidator.findExistingOrThrow(zeroToleranceRuleId))
                    .willReturn(zeroToleranceRule);
            willDoNothing().given(zeroToleranceRule).update(updateData, changedAt);
            given(zeroToleranceRulePersistenceManager.persist(zeroToleranceRule))
                    .willReturn(zeroToleranceRuleId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(zeroToleranceRuleCommandFactory).should().createUpdateContext(command);
            then(zeroToleranceRuleValidator).should().findExistingOrThrow(zeroToleranceRuleId);
            then(zeroToleranceRule).should().update(updateData, changedAt);
            then(zeroToleranceRulePersistenceManager).should().persist(zeroToleranceRule);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ZeroToleranceRule인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateZeroToleranceRuleCommand command = createDefaultCommand();
            ZeroToleranceRuleId zeroToleranceRuleId =
                    ZeroToleranceRuleId.of(command.zeroToleranceRuleId());
            Instant changedAt = Instant.now();
            UpdateContext<ZeroToleranceRuleId, ZeroToleranceRuleUpdateData> context =
                    new UpdateContext<>(zeroToleranceRuleId, updateData, changedAt);

            given(zeroToleranceRuleCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new ZeroToleranceRuleNotFoundException(zeroToleranceRuleId.value()))
                    .given(zeroToleranceRuleValidator)
                    .findExistingOrThrow(zeroToleranceRuleId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ZeroToleranceRuleNotFoundException.class);

            then(zeroToleranceRulePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateZeroToleranceRuleCommand createDefaultCommand() {
        return new UpdateZeroToleranceRuleCommand(
                1L,
                "ANNOTATION",
                "@Data|@Builder|@Value",
                DetectionType.REGEX,
                true,
                "Lombok 어노테이션 사용이 금지되어 있습니다.");
    }
}
