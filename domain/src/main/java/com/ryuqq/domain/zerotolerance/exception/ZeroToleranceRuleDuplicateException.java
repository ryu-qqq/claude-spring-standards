package com.ryuqq.domain.zerotolerance.exception;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ZeroToleranceRuleDuplicateException - Zero-Tolerance 규칙 중복 예외
 *
 * <p>동일한 CodingRule에 대한 Zero-Tolerance 규칙이 이미 존재할 때 발생합니다.
 *
 * @author ryu-qqq
 */
public class ZeroToleranceRuleDuplicateException extends DomainException {

    public ZeroToleranceRuleDuplicateException(CodingRuleId ruleId) {
        super(
                ZeroToleranceRuleErrorCode.ZERO_TOLERANCE_RULE_DUPLICATE,
                String.format("ZeroToleranceRule already exists for ruleId: %d", ruleId.value()),
                Map.of("ruleId", ruleId.value()));
    }
}
