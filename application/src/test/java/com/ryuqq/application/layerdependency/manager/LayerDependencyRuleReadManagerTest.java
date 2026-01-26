package com.ryuqq.application.layerdependency.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.layerdependency.port.out.LayerDependencyRuleQueryPort;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.exception.LayerDependencyRuleNotFoundException;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LayerDependencyRuleReadManager 단위 테스트
 *
 * <p>LayerDependencyRule 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("LayerDependencyRuleReadManager 단위 테스트")
class LayerDependencyRuleReadManagerTest {

    @Mock private LayerDependencyRuleQueryPort layerDependencyRuleQueryPort;

    @Mock private LayerDependencyRule layerDependencyRule;

    private LayerDependencyRuleReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new LayerDependencyRuleReadManager(layerDependencyRuleQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 LayerDependencyRule 조회")
        void getById_WithValidId_ShouldReturnLayerDependencyRule() {
            // given
            LayerDependencyRuleId id = LayerDependencyRuleId.of(1L);
            given(layerDependencyRuleQueryPort.findById(id))
                    .willReturn(Optional.of(layerDependencyRule));

            // when
            LayerDependencyRule result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(layerDependencyRule);
            then(layerDependencyRuleQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            LayerDependencyRuleId id = LayerDependencyRuleId.of(999L);
            given(layerDependencyRuleQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(LayerDependencyRuleNotFoundException.class);
            then(layerDependencyRuleQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findByArchitectureId 메서드")
    class FindByArchitectureId {

        @Test
        @DisplayName("성공 - 아키텍처 ID로 레이어 의존성 규칙 목록 조회")
        void findByArchitectureId_WithArchitectureId_ShouldReturnList() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            List<LayerDependencyRule> rules = List.of(layerDependencyRule);
            given(layerDependencyRuleQueryPort.findByArchitectureId(architectureId))
                    .willReturn(rules);

            // when
            List<LayerDependencyRule> result = sut.findByArchitectureId(architectureId);

            // then
            assertThat(result).hasSize(1).containsExactly(layerDependencyRule);
            then(layerDependencyRuleQueryPort).should().findByArchitectureId(architectureId);
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void findByArchitectureId_WhenEmpty_ShouldReturnEmptyList() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(999L);
            given(layerDependencyRuleQueryPort.findByArchitectureId(architectureId))
                    .willReturn(List.of());

            // when
            List<LayerDependencyRule> result = sut.findByArchitectureId(architectureId);

            // then
            assertThat(result).isEmpty();
            then(layerDependencyRuleQueryPort).should().findByArchitectureId(architectureId);
        }
    }
}
