package com.ryuqq.application.zerotolerance.port.out;

import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleDetailResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
import java.util.Optional;

/**
 * ZeroToleranceRuleQueryPort - Zero-Tolerance 규칙 조회 아웃바운드 포트
 *
 * <p>Zero-Tolerance 규칙 상세 조회를 위한 포트입니다. CodingRule과 관련 RuleExample, ChecklistItem을 함께 조회합니다.
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ZeroToleranceRuleQueryPort {

    /**
     * ID로 Zero-Tolerance 규칙 조회
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID
     * @return ZeroToleranceRule Optional
     */
    Optional<ZeroToleranceRule> findById(ZeroToleranceRuleId zeroToleranceRuleId);

    /**
     * CodingRuleId로 Zero-Tolerance 규칙 존재 여부 확인
     *
     * @param ruleId 코딩 규칙 ID
     * @return 존재하면 true
     */
    boolean existsByRuleId(Long ruleId);

    /**
     * ID로 Zero-Tolerance 규칙 상세 조회
     *
     * <p>CodingRule과 관련된 RuleExample, ChecklistItem을 함께 조회합니다.
     *
     * @param ruleId 코딩 규칙 ID
     * @return Zero-Tolerance 규칙 상세 결과 Optional
     */
    Optional<ZeroToleranceRuleDetailResult> findDetailById(Long ruleId);

    /**
     * 슬라이스 조건으로 Zero-Tolerance 규칙 상세 목록 조회
     *
     * <p>커서 기반 페이징으로 조회하며, 각 규칙에 대해 관련 RuleExample, ChecklistItem을 함께 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return Zero-Tolerance 규칙 슬라이스 결과
     */
    ZeroToleranceRuleSliceResult findAllDetails(ZeroToleranceRuleSliceCriteria criteria);
}
