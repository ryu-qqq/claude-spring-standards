package com.ryuqq.application.zerotolerance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.zerotolerance.dto.command.CreateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.factory.command.ZeroToleranceRuleCommandFactory;
import com.ryuqq.application.zerotolerance.manager.ZeroToleranceRulePersistenceManager;
import com.ryuqq.application.zerotolerance.validator.ZeroToleranceRuleValidator;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.exception.ZeroToleranceRuleDuplicateException;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateZeroToleranceRuleService 단위 테스트
 *
 * <p>ZeroToleranceRule 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateZeroToleranceRuleService 단위 테스트")
class CreateZeroToleranceRuleServiceTest {

    @Mock private ZeroToleranceRuleValidator zeroToleranceRuleValidator;

    @Mock private ZeroToleranceRuleCommandFactory zeroToleranceRuleCommandFactory;

    @Mock private ZeroToleranceRulePersistenceManager zeroToleranceRulePersistenceManager;

    @Mock private ZeroToleranceRule zeroToleranceRule;

    private CreateZeroToleranceRuleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateZeroToleranceRuleService(
                        zeroToleranceRuleValidator,
                        zeroToleranceRuleCommandFactory,
                        zeroToleranceRulePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ZeroToleranceRule 생성")
        void execute_WithValidCommand_ShouldCreateZeroToleranceRule() {
            // given
            CreateZeroToleranceRuleCommand command = createDefaultCommand();
            CodingRuleId ruleId = CodingRuleId.of(command.ruleId());
            ZeroToleranceRuleId savedId = ZeroToleranceRuleId.of(1L);

            willDoNothing().given(zeroToleranceRuleValidator).validateNotDuplicate(ruleId);
            given(zeroToleranceRuleCommandFactory.create(command)).willReturn(zeroToleranceRule);
            given(zeroToleranceRulePersistenceManager.persist(zeroToleranceRule))
                    .willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(zeroToleranceRuleValidator).should().validateNotDuplicate(ruleId);
            then(zeroToleranceRuleCommandFactory).should().create(command);
            then(zeroToleranceRulePersistenceManager).should().persist(zeroToleranceRule);
        }

        @Test
        @DisplayName("실패 - 중복된 규칙인 경우")
        void execute_WhenRuleDuplicate_ShouldThrowException() {
            // given
            CreateZeroToleranceRuleCommand command = createDefaultCommand();
            CodingRuleId ruleId = CodingRuleId.of(command.ruleId());

            willThrow(new ZeroToleranceRuleDuplicateException(ruleId))
                    .given(zeroToleranceRuleValidator)
                    .validateNotDuplicate(ruleId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ZeroToleranceRuleDuplicateException.class);

            then(zeroToleranceRuleCommandFactory).shouldHaveNoInteractions();
            then(zeroToleranceRulePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateZeroToleranceRuleCommand createDefaultCommand() {
        return new CreateZeroToleranceRuleCommand(
                1L,
                "ANNOTATION",
                "@Data",
                DetectionType.REGEX,
                true,
                "Lombok @Data is not allowed");
    }
}
