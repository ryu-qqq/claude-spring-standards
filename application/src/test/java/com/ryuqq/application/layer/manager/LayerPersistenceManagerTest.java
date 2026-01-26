package com.ryuqq.application.layer.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.layer.port.out.LayerCommandPort;
import com.ryuqq.domain.layer.aggregate.Layer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LayerPersistenceManager 단위 테스트
 *
 * <p>Layer 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("LayerPersistenceManager 단위 테스트")
class LayerPersistenceManagerTest {

    @Mock private LayerCommandPort layerCommandPort;

    @Mock private Layer layer;

    private LayerPersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new LayerPersistenceManager(layerCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - Layer 영속화")
        void persist_WithLayer_ShouldReturnId() {
            // given
            Long expectedId = 1L;
            given(layerCommandPort.persist(layer)).willReturn(expectedId);

            // when
            Long result = sut.persist(layer);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(layerCommandPort).should().persist(layer);
        }
    }
}
