package com.ryuqq.application.ruleexample.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.factory.command.RuleExampleCommandFactory;
import com.ryuqq.application.ruleexample.manager.RuleExamplePersistenceManager;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
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
 * CreateRuleExampleService 단위 테스트
 *
 * <p>RuleExample 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateRuleExampleService 단위 테스트")
class CreateRuleExampleServiceTest {

    @Mock private RuleExampleCommandFactory ruleExampleCommandFactory;

    @Mock private RuleExamplePersistenceManager ruleExamplePersistenceManager;

    @Mock private RuleExample ruleExample;

    private CreateRuleExampleService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateRuleExampleService(
                        ruleExampleCommandFactory, ruleExamplePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 RuleExample 생성")
        void execute_WithValidCommand_ShouldCreateRuleExample() {
            // given
            CreateRuleExampleCommand command = createDefaultCommand();
            RuleExampleId savedId = RuleExampleId.of(1L);

            given(ruleExampleCommandFactory.create(command)).willReturn(ruleExample);
            given(ruleExamplePersistenceManager.persist(ruleExample)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(ruleExampleCommandFactory).should().create(command);
            then(ruleExamplePersistenceManager).should().persist(ruleExample);
        }
    }

    private CreateRuleExampleCommand createDefaultCommand() {
        return new CreateRuleExampleCommand(
                1L, "GOOD", "public class Example { }", "java", "좋은 예시입니다.", List.of(1, 2, 3));
    }
}
