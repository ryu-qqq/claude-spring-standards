package com.ryuqq.domain.ruleexample.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * RuleExampleNotFoundException - 규칙 예시 미존재 예외
 *
 * @author ryu-qqq
 */
public class RuleExampleNotFoundException extends DomainException {

    public RuleExampleNotFoundException(Long ruleExampleId) {
        super(
                RuleExampleErrorCode.RULE_EXAMPLE_NOT_FOUND,
                String.format("RuleExample not found: %d", ruleExampleId),
                Map.of("ruleExampleId", ruleExampleId));
    }
}
