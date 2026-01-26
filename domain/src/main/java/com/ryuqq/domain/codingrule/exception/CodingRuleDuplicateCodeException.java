package com.ryuqq.domain.codingrule.exception;

import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.Map;

/**
 * CodingRuleDuplicateCodeException - 코딩 규칙 코드 중복 예외
 *
 * <p>동일한 컨벤션 내에서 규칙 코드가 이미 존재할 때 발생합니다.
 *
 * @author ryu-qqq
 */
public class CodingRuleDuplicateCodeException extends DomainException {

    public CodingRuleDuplicateCodeException(ConventionId conventionId, RuleCode code) {
        super(
                CodingRuleErrorCode.CODING_RULE_DUPLICATE_CODE,
                String.format(
                        "CodingRule code '%s' already exists in convention: %d",
                        code.value(), conventionId.value()),
                Map.of(
                        "conventionId", conventionId.value(),
                        "ruleCode", code.value()));
    }
}
