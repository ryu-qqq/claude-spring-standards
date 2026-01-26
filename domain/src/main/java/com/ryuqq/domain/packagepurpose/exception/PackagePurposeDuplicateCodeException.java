package com.ryuqq.domain.packagepurpose.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * PackagePurposeDuplicateCodeException - 패키지 목적 코드 중복 예외
 *
 * @author ryu-qqq
 */
public class PackagePurposeDuplicateCodeException extends DomainException {

    public PackagePurposeDuplicateCodeException(Long structureId, String code) {
        super(
                PackagePurposeErrorCode.PACKAGE_PURPOSE_DUPLICATE_CODE,
                String.format(
                        "PackagePurpose with code '%s' already exists in structure '%d'",
                        code, structureId),
                Map.of("structureId", structureId, "code", code));
    }
}
