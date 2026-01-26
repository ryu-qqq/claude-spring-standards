package com.ryuqq.domain.zerotolerance.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import java.util.Map;

/**
 * ZeroToleranceRuleNotFoundException - Zero Tolerance 규칙 미존재 예외
 *
 * @author ryu-qqq
 */
public class ZeroToleranceRuleNotFoundException extends DomainException {

    public ZeroToleranceRuleNotFoundException(Long zeroToleranceRuleId) {
        super(
                ZeroToleranceRuleErrorCode.ZERO_TOLERANCE_RULE_NOT_FOUND,
                String.format("ZeroToleranceRule not found: %d", zeroToleranceRuleId),
                Map.of("zeroToleranceRuleId", zeroToleranceRuleId));
    }

    public ZeroToleranceRuleNotFoundException(ZeroToleranceRuleId zeroToleranceRuleId) {
        this(zeroToleranceRuleId.value());
    }
}
