package com.ryuqq.application.ruleexample.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.ruleexample.port.out.RuleExampleCommandPort;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
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
 * RuleExamplePersistenceManager 단위 테스트
 *
 * <p>RuleExample 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("RuleExamplePersistenceManager 단위 테스트")
class RuleExamplePersistenceManagerTest {

    @Mock private RuleExampleCommandPort ruleExampleCommandPort;

    @Mock private RuleExample ruleExample;

    private RuleExamplePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new RuleExamplePersistenceManager(ruleExampleCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - RuleExample 영속화")
        void persist_WithRuleExample_ShouldReturnId() {
            // given
            RuleExampleId expectedId = RuleExampleId.of(1L);
            given(ruleExampleCommandPort.persist(ruleExample)).willReturn(expectedId);

            // when
            RuleExampleId result = sut.persist(ruleExample);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(ruleExampleCommandPort).should().persist(ruleExample);
        }
    }
}
