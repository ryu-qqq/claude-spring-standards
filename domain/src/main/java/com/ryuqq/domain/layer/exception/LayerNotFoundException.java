package com.ryuqq.domain.layer.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * LayerNotFoundException - 레이어 미존재 예외
 *
 * @author ryu-qqq
 */
public class LayerNotFoundException extends DomainException {

    public LayerNotFoundException(Long layerId) {
        super(
                LayerErrorCode.LAYER_NOT_FOUND,
                String.format("Layer not found: %d", layerId),
                Map.of("layerId", layerId));
    }
}
