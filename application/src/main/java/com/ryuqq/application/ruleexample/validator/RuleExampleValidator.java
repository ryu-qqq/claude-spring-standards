package com.ryuqq.application.ruleexample.validator;

import com.ryuqq.application.ruleexample.manager.RuleExampleReadManager;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.springframework.stereotype.Component;

/**
 * RuleExampleValidator - 규칙 예시 검증기
 *
 * <p>규칙 예시 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleValidator {

    private final RuleExampleReadManager ruleExampleReadManager;

    public RuleExampleValidator(RuleExampleReadManager ruleExampleReadManager) {
        this.ruleExampleReadManager = ruleExampleReadManager;
    }

    /**
     * 규칙 예시 존재 여부 검증 후 반환 (조회 + 검증 통합)
     *
     * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param ruleExampleId 규칙 예시 ID
     * @return 존재하는 RuleExample
     * @throws com.ryuqq.domain.ruleexample.exception.RuleExampleNotFoundException 규칙 예시가 존재하지 않으면
     */
    public RuleExample findExistingOrThrow(RuleExampleId ruleExampleId) {
        return ruleExampleReadManager.getById(ruleExampleId);
    }

    /**
     * 규칙 예시 존재 여부 검증
     *
     * @param ruleExampleId 규칙 예시 ID
     * @throws com.ryuqq.domain.ruleexample.exception.RuleExampleNotFoundException 규칙 예시가 존재하지 않으면
     */
    public void validateExists(RuleExampleId ruleExampleId) {
        ruleExampleReadManager.getById(ruleExampleId);
    }
}
