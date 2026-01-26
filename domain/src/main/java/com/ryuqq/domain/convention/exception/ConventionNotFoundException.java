package com.ryuqq.domain.convention.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.Map;

/**
 * ConventionNotFoundException - 컨벤션 미존재 예외
 *
 * @author ryu-qqq
 */
public class ConventionNotFoundException extends DomainException {

    public ConventionNotFoundException(Long conventionId) {
        super(
                ConventionErrorCode.CONVENTION_NOT_FOUND,
                String.format("Convention not found: %d", conventionId),
                Map.of("conventionId", conventionId));
    }

    public ConventionNotFoundException(ModuleId moduleId) {
        super(
                ConventionErrorCode.CONVENTION_NOT_FOUND,
                String.format("Convention not found for moduleId: %d", moduleId.value()),
                Map.of("moduleId", moduleId.value()));
    }
}
