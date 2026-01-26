package com.ryuqq.domain.architecture.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ArchitectureNotFoundException - 아키텍처 미존재 예외
 *
 * @author ryu-qqq
 */
public class ArchitectureNotFoundException extends DomainException {

    public ArchitectureNotFoundException(Long architectureId) {
        super(
                ArchitectureErrorCode.ARCHITECTURE_NOT_FOUND,
                String.format("Architecture not found: %d", architectureId),
                Map.of("architectureId", architectureId));
    }
}
