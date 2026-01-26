package com.ryuqq.application.codingrule.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.codingrule.port.out.CodingRuleCommandPort;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CodingRulePersistenceManager 단위 테스트
 *
 * <p>CodingRule 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("CodingRulePersistenceManager 단위 테스트")
class CodingRulePersistenceManagerTest {

    @Mock private CodingRuleCommandPort codingRuleCommandPort;

    @Mock private CodingRule codingRule;

    private CodingRulePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new CodingRulePersistenceManager(codingRuleCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - CodingRule 영속화")
        void persist_WithCodingRule_ShouldReturnId() {
            // given
            CodingRuleId expectedId = CodingRuleId.of(1L);
            given(codingRuleCommandPort.persist(codingRule)).willReturn(expectedId);

            // when
            CodingRuleId result = sut.persist(codingRule);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(codingRuleCommandPort).should().persist(codingRule);
        }
    }
}
