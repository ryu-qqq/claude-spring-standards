package com.ryuqq.application.layer.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.layer.port.out.LayerQueryPort;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.exception.LayerNotFoundException;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import com.ryuqq.domain.layer.vo.LayerCode;
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
 * LayerReadManager 단위 테스트
 *
 * <p>Layer 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("LayerReadManager 단위 테스트")
class LayerReadManagerTest {

    @Mock private LayerQueryPort layerQueryPort;

    @Mock private Layer layer;

    @Mock private LayerSliceCriteria criteria;

    private LayerReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new LayerReadManager(layerQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Layer 조회")
        void findById_WithValidId_ShouldReturnLayer() {
            // given
            LayerId id = LayerId.of(1L);
            given(layerQueryPort.findById(id)).willReturn(Optional.of(layer));

            // when
            Optional<Layer> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(layer);
            then(layerQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            LayerId id = LayerId.of(999L);
            given(layerQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Layer> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(layerQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 Layer 조회")
        void getById_WithValidId_ShouldReturnLayer() {
            // given
            LayerId id = LayerId.of(1L);
            given(layerQueryPort.findById(id)).willReturn(Optional.of(layer));

            // when
            Layer result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(layer);
            then(layerQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID 조회 시 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            LayerId id = LayerId.of(999L);
            given(layerQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(LayerNotFoundException.class);
            then(layerQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID 확인")
        void existsById_WhenExists_ShouldReturnTrue() {
            // given
            LayerId id = LayerId.of(1L);
            given(layerQueryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(layerQueryPort).should().existsById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<Layer> layers = List.of(layer);
            given(layerQueryPort.findBySliceCriteria(criteria)).willReturn(layers);

            // when
            List<Layer> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(layer);
            then(layerQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByArchitectureIdAndCode 메서드")
    class ExistsByArchitectureIdAndCode {

        @Test
        @DisplayName("성공 - 아키텍처 내 코드 중복 확인")
        void existsByArchitectureIdAndCode_WhenExists_ShouldReturnTrue() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("DOMAIN");
            given(layerQueryPort.existsByArchitectureIdAndCode(architectureId, code))
                    .willReturn(true);

            // when
            boolean result = sut.existsByArchitectureIdAndCode(architectureId, code);

            // then
            assertThat(result).isTrue();
            then(layerQueryPort).should().existsByArchitectureIdAndCode(architectureId, code);
        }
    }

    @Nested
    @DisplayName("existsByArchitectureIdAndCodeAndIdNot 메서드")
    class ExistsByArchitectureIdAndCodeAndIdNot {

        @Test
        @DisplayName("성공 - 특정 ID 제외하고 아키텍처 내 코드 중복 확인")
        void existsByArchitectureIdAndCodeAndIdNot_WhenExists_ShouldReturnTrue() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            LayerCode code = LayerCode.of("DOMAIN");
            LayerId excludeId = LayerId.of(1L);
            given(
                            layerQueryPort.existsByArchitectureIdAndCodeAndIdNot(
                                    architectureId, code, excludeId))
                    .willReturn(true);

            // when
            boolean result =
                    sut.existsByArchitectureIdAndCodeAndIdNot(architectureId, code, excludeId);

            // then
            assertThat(result).isTrue();
            then(layerQueryPort)
                    .should()
                    .existsByArchitectureIdAndCodeAndIdNot(architectureId, code, excludeId);
        }
    }

    @Nested
    @DisplayName("existsByArchitectureId 메서드")
    class ExistsByArchitectureId {

        @Test
        @DisplayName("성공 - Architecture에 속한 Layer 존재 확인")
        void existsByArchitectureId_WhenExists_ShouldReturnTrue() {
            // given
            ArchitectureId architectureId = ArchitectureId.of(1L);
            given(layerQueryPort.existsByArchitectureId(architectureId)).willReturn(true);

            // when
            boolean result = sut.existsByArchitectureId(architectureId);

            // then
            assertThat(result).isTrue();
            then(layerQueryPort).should().existsByArchitectureId(architectureId);
        }
    }
}
