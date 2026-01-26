package com.ryuqq.domain.module.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ModuleNotFoundException - 모듈 미존재 예외
 *
 * @author ryu-qqq
 */
public class ModuleNotFoundException extends DomainException {

    public ModuleNotFoundException(Long moduleId) {
        super(
                ModuleErrorCode.MODULE_NOT_FOUND,
                String.format("Module not found: %d", moduleId),
                Map.of("moduleId", moduleId));
    }
}
