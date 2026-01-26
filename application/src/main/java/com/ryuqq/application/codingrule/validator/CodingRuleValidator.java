package com.ryuqq.application.codingrule.validator;

import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.exception.CodingRuleDuplicateCodeException;
import com.ryuqq.domain.codingrule.exception.CodingRuleNotFoundException;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
import org.springframework.stereotype.Component;

/**
 * CodingRuleValidator - 코딩 규칙 검증기
 *
 * <p>코딩 규칙 비즈니스 규칙을 검증합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드는 검증 성공 시 조회한 Domain 객체를 반환합니다.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 findExistingOrThrow() 사용.
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleValidator {

    private final CodingRuleReadManager codingRuleReadManager;

    public CodingRuleValidator(CodingRuleReadManager codingRuleReadManager) {
        this.codingRuleReadManager = codingRuleReadManager;
    }

    /**
     * CodingRule 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id CodingRule ID (VO)
     * @return CodingRule 조회된 도메인 객체
     * @throws CodingRuleNotFoundException 존재하지 않는 경우
     */
    public CodingRule findExistingOrThrow(CodingRuleId id) {
        return codingRuleReadManager
                .findById(id)
                .orElseThrow(() -> new CodingRuleNotFoundException(id.value()));
    }

    /**
     * 코딩 규칙 존재 여부 검증
     *
     * @param id CodingRule ID (VO)
     * @throws CodingRuleNotFoundException 존재하지 않는 경우
     */
    public void validateExists(CodingRuleId id) {
        if (!codingRuleReadManager.existsById(id)) {
            throw new CodingRuleNotFoundException(id.value());
        }
    }

    /**
     * 규칙 코드 중복 검증 (생성 시)
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @throws CodingRuleDuplicateCodeException 동일 코드의 규칙이 존재하면
     */
    public void validateNotDuplicate(ConventionId conventionId, RuleCode code) {
        if (codingRuleReadManager.existsByConventionIdAndCode(conventionId, code)) {
            throw new CodingRuleDuplicateCodeException(conventionId, code);
        }
    }

    /**
     * 규칙 코드 중복 검증 (수정 시, 자신 제외)
     *
     * @param conventionId 컨벤션 ID
     * @param code 규칙 코드
     * @param excludeCodingRuleId 제외할 코딩 규칙 ID
     * @throws CodingRuleDuplicateCodeException 동일 코드의 다른 규칙이 존재하면
     */
    public void validateNotDuplicateExcluding(
            ConventionId conventionId, RuleCode code, CodingRuleId excludeCodingRuleId) {
        if (codingRuleReadManager.existsByConventionIdAndCodeExcluding(
                conventionId, code, excludeCodingRuleId)) {
            throw new CodingRuleDuplicateCodeException(conventionId, code);
        }
    }
}
