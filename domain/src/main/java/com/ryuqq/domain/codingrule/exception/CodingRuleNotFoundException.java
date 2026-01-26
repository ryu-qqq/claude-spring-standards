package com.ryuqq.domain.codingrule.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * CodingRuleNotFoundException - 코딩 규칙 미존재 예외
 *
 * @author ryu-qqq
 */
public class CodingRuleNotFoundException extends DomainException {

    public CodingRuleNotFoundException(Long codingRuleId) {
        super(
                CodingRuleErrorCode.CODING_RULE_NOT_FOUND,
                String.format("CodingRule not found: %d", codingRuleId),
                Map.of("codingRuleId", codingRuleId));
    }
}
