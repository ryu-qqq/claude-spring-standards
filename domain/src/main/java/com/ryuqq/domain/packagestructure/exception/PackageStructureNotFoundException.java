package com.ryuqq.domain.packagestructure.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * PackageStructureNotFoundException - 패키지 구조 미존재 예외
 *
 * @author ryu-qqq
 */
public class PackageStructureNotFoundException extends DomainException {

    public PackageStructureNotFoundException(Long packageStructureId) {
        super(
                PackageStructureErrorCode.PACKAGE_STRUCTURE_NOT_FOUND,
                String.format("PackageStructure not found: %d", packageStructureId),
                Map.of("packageStructureId", packageStructureId));
    }
}
