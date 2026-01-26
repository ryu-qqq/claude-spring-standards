package com.ryuqq.domain.packagestructure.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * PackageStructureDuplicateException - 패키지 구조 중복 예외
 *
 * <p>동일한 모듈 내에서 동일한 경로 패턴의 패키지 구조가 이미 존재할 때 발생합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class PackageStructureDuplicateException extends DomainException {

    public PackageStructureDuplicateException(Long moduleId, String pathPattern) {
        super(
                PackageStructureErrorCode.DUPLICATE_PATH_PATTERN,
                String.format(
                        "Duplicate path pattern in module: moduleId=%d, pathPattern=%s",
                        moduleId, pathPattern),
                Map.of("moduleId", moduleId, "pathPattern", pathPattern));
    }
}
