package com.ryuqq.domain.packagepurpose.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * PackagePurposeNotFoundException - 패키지 목적 미존재 예외
 *
 * @author ryu-qqq
 */
public class PackagePurposeNotFoundException extends DomainException {

    public PackagePurposeNotFoundException(Long packagePurposeId) {
        super(
                PackagePurposeErrorCode.PACKAGE_PURPOSE_NOT_FOUND,
                String.format("PackagePurpose not found: %d", packagePurposeId),
                Map.of("packagePurposeId", packagePurposeId));
    }
}
