package com.ryuqq.application.layer.manager;

import com.ryuqq.application.layer.port.out.LayerCommandPort;
import com.ryuqq.domain.layer.aggregate.Layer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * LayerPersistenceManager - Layer 영속성 관리자
 *
 * <p>CommandPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author ryu-qqq
 */
@Component
public class LayerPersistenceManager {

    private final LayerCommandPort layerCommandPort;

    public LayerPersistenceManager(LayerCommandPort layerCommandPort) {
        this.layerCommandPort = layerCommandPort;
    }

    /**
     * Layer 영속화
     *
     * @param layer 영속화할 Layer
     * @return 영속화된 Layer ID
     */
    @Transactional
    public Long persist(Layer layer) {
        return layerCommandPort.persist(layer);
    }
}
