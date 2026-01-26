package com.ryuqq.domain.techstack.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * TechStackHasChildrenException - 하위 리소스 존재 예외
 *
 * <p>TechStack 삭제 시 하위 Architecture가 존재하는 경우 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class TechStackHasChildrenException extends DomainException {

    public TechStackHasChildrenException(Long techStackId) {
        super(
                TechStackErrorCode.TECH_STACK_HAS_CHILDREN,
                String.format("TechStack has children: %d", techStackId),
                Map.of("techStackId", techStackId));
    }

    public TechStackHasChildrenException(Long techStackId, String reason) {
        super(
                TechStackErrorCode.TECH_STACK_HAS_CHILDREN,
                reason,
                Map.of("techStackId", techStackId));
    }
}
