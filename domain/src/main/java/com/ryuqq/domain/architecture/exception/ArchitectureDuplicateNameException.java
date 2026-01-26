package com.ryuqq.domain.architecture.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ArchitectureDuplicateNameException - 아키텍처 이름 중복 예외
 *
 * @author ryu-qqq
 */
public class ArchitectureDuplicateNameException extends DomainException {

    public ArchitectureDuplicateNameException(String name) {
        super(
                ArchitectureErrorCode.ARCHITECTURE_DUPLICATE_NAME,
                String.format("Architecture name already exists: %s", name),
                Map.of("name", name));
    }
}
