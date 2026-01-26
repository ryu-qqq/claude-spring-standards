package com.ryuqq.domain.convention.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.Map;

/**
 * ConventionDuplicateException - 컨벤션 중복 예외
 *
 * <p>동일한 모듈과 버전의 컨벤션이 이미 존재할 때 발생합니다.
 *
 * @author ryu-qqq
 */
public class ConventionDuplicateException extends DomainException {

    public ConventionDuplicateException(ModuleId moduleId, String version) {
        super(
                ConventionErrorCode.CONVENTION_DUPLICATE,
                String.format(
                        "Convention already exists for moduleId: %d, version: %s",
                        moduleId.value(), version),
                Map.of("moduleId", moduleId.value(), "version", version));
    }
}
