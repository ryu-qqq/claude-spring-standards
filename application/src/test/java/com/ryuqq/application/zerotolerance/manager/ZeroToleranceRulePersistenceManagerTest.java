package com.ryuqq.application.zerotolerance.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.zerotolerance.port.out.ZeroToleranceRuleCommandPort;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ZeroToleranceRulePersistenceManager 단위 테스트
 *
 * <p>ZeroToleranceRule 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ZeroToleranceRulePersistenceManager 단위 테스트")
class ZeroToleranceRulePersistenceManagerTest {

    @Mock private ZeroToleranceRuleCommandPort zeroToleranceRuleCommandPort;

    @Mock private ZeroToleranceRule zeroToleranceRule;

    private ZeroToleranceRulePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ZeroToleranceRulePersistenceManager(zeroToleranceRuleCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - ZeroToleranceRule 영속화")
        void persist_WithZeroToleranceRule_ShouldReturnId() {
            // given
            ZeroToleranceRuleId expectedId = ZeroToleranceRuleId.of(1L);
            given(zeroToleranceRuleCommandPort.persist(zeroToleranceRule)).willReturn(expectedId);

            // when
            ZeroToleranceRuleId result = sut.persist(zeroToleranceRule);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(zeroToleranceRuleCommandPort).should().persist(zeroToleranceRule);
        }
    }
}
