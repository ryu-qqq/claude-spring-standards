package com.ryuqq.application.layerdependency.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.application.layerdependency.port.out.LayerDependencyRuleCommandPort;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LayerDependencyRulePersistenceManager 단위 테스트
 *
 * <p>LayerDependencyRule 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("LayerDependencyRulePersistenceManager 단위 테스트")
class LayerDependencyRulePersistenceManagerTest {

    @Mock private LayerDependencyRuleCommandPort layerDependencyRuleCommandPort;

    @Mock private LayerDependencyRule layerDependencyRule;

    private LayerDependencyRulePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new LayerDependencyRulePersistenceManager(layerDependencyRuleCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - LayerDependencyRule 영속화")
        void persist_WithLayerDependencyRule_ShouldReturnId() {
            // given
            LayerDependencyRuleId expectedId = LayerDependencyRuleId.of(1L);
            given(layerDependencyRuleCommandPort.persist(layerDependencyRule))
                    .willReturn(expectedId);

            // when
            LayerDependencyRuleId result = sut.persist(layerDependencyRule);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(layerDependencyRuleCommandPort).should().persist(layerDependencyRule);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("성공 - LayerDependencyRule 삭제")
        void delete_WithId_ShouldCallCommandPort() {
            // given
            LayerDependencyRuleId id = LayerDependencyRuleId.of(1L);
            willDoNothing().given(layerDependencyRuleCommandPort).delete(id);

            // when
            sut.delete(id);

            // then
            then(layerDependencyRuleCommandPort).should().delete(id);
        }
    }
}
