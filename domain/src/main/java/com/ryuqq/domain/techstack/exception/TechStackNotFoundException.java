package com.ryuqq.domain.techstack.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * TechStackNotFoundException - 기술 스택 미존재 예외
 *
 * @author ryu-qqq
 */
public class TechStackNotFoundException extends DomainException {

    public TechStackNotFoundException(Long techStackId) {
        super(
                TechStackErrorCode.TECH_STACK_NOT_FOUND,
                String.format("TechStack not found: %d", techStackId),
                Map.of("techStackId", techStackId));
    }
}
