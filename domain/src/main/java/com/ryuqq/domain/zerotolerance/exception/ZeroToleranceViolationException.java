package com.ryuqq.domain.zerotolerance.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ZeroToleranceViolationException - Zero Tolerance 규칙 위반 예외
 *
 * @author ryu-qqq
 */
public class ZeroToleranceViolationException extends DomainException {

    public ZeroToleranceViolationException(String type, String errorMessage, String violatedCode) {
        super(
                ZeroToleranceRuleErrorCode.ZERO_TOLERANCE_VIOLATION_DETECTED,
                String.format("Zero tolerance violation [%s]: %s", type, errorMessage),
                Map.of(
                        "type", type,
                        "errorMessage", errorMessage,
                        "violatedCode", violatedCode != null ? violatedCode : ""));
    }
}
