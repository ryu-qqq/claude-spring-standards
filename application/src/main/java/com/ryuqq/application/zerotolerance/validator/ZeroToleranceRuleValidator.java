package com.ryuqq.application.zerotolerance.validator;

import com.ryuqq.application.zerotolerance.manager.ZeroToleranceRuleReadManager;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.exception.ZeroToleranceRuleDuplicateException;
import com.ryuqq.domain.zerotolerance.exception.ZeroToleranceRuleNotFoundException;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleValidator - Zero-Tolerance 규칙 검증기
 *
 * <p>Zero-Tolerance 규칙 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * <p>APP-VAL-001: findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Component
public class ZeroToleranceRuleValidator {

    private final ZeroToleranceRuleReadManager zeroToleranceRuleReadManager;

    public ZeroToleranceRuleValidator(ZeroToleranceRuleReadManager zeroToleranceRuleReadManager) {
        this.zeroToleranceRuleReadManager = zeroToleranceRuleReadManager;
    }

    /**
     * Zero-Tolerance 규칙 조회 + 존재 검증 통합
     *
     * <p>APP-VAL-001: findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID
     * @return ZeroToleranceRule 도메인 객체
     * @throws ZeroToleranceRuleNotFoundException 규칙이 존재하지 않으면
     */
    public ZeroToleranceRule findExistingOrThrow(ZeroToleranceRuleId zeroToleranceRuleId) {
        return zeroToleranceRuleReadManager.getById(zeroToleranceRuleId);
    }

    /**
     * Zero-Tolerance 규칙 존재 여부 검증
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID
     * @throws ZeroToleranceRuleNotFoundException 규칙이 존재하지 않으면
     */
    public void validateExists(ZeroToleranceRuleId zeroToleranceRuleId) {
        zeroToleranceRuleReadManager.getById(zeroToleranceRuleId);
    }

    /**
     * CodingRule ID 중복 검증 (생성 시)
     *
     * @param ruleId 코딩 규칙 ID
     * @throws ZeroToleranceRuleDuplicateException 동일 ruleId의 규칙이 존재하면
     */
    public void validateNotDuplicate(CodingRuleId ruleId) {
        if (zeroToleranceRuleReadManager.existsByRuleId(ruleId.value())) {
            throw new ZeroToleranceRuleDuplicateException(ruleId);
        }
    }
}
