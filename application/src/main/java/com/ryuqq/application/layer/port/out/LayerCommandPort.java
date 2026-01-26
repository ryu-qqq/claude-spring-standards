package com.ryuqq.application.layer.port.out;

import com.ryuqq.domain.layer.aggregate.Layer;

/**
 * LayerCommandPort - Layer 명령 Port
 *
 * <p>영속성 계층으로의 Layer CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface LayerCommandPort {

    /**
     * Layer 영속화 (생성/수정/삭제)
     *
     * @param layer 영속화할 Layer
     * @return 영속화된 Layer ID
     */
    Long persist(Layer layer);
}
