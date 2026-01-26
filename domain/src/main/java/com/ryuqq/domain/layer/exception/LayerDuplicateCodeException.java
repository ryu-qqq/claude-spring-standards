package com.ryuqq.domain.layer.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * LayerDuplicateCodeException - 레이어 코드 중복 예외
 *
 * @author ryu-qqq
 */
public class LayerDuplicateCodeException extends DomainException {

    public LayerDuplicateCodeException(String code, Long architectureId) {
        super(
                LayerErrorCode.LAYER_DUPLICATE_CODE,
                String.format(
                        "Layer code '%s' already exists in architecture: %d", code, architectureId),
                Map.of("code", code, "architectureId", architectureId));
    }
}
