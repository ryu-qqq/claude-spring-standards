package com.ryuqq.application.ruleexample.port.out;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
import java.util.List;
import java.util.Optional;

/**
 * RuleExampleQueryPort - 규칙 예시 조회 아웃바운드 포트
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 */
public interface RuleExampleQueryPort {

    /**
     * ID로 규칙 예시 조회
     *
     * @param id 규칙 예시 ID
     * @return 규칙 예시 Optional
     */
    Optional<RuleExample> findById(Long id);

    /**
     * RuleExampleId로 규칙 예시 조회
     *
     * @param ruleExampleId 규칙 예시 ID
     * @return 규칙 예시 Optional
     */
    Optional<RuleExample> findById(RuleExampleId ruleExampleId);

    /**
     * 코딩 규칙 ID로 규칙 예시 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 규칙 예시 목록
     */
    List<RuleExample> findByRuleId(Long ruleId);

    /**
     * CodingRuleId 값 객체로 규칙 예시 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 규칙 예시 목록
     */
    List<RuleExample> findByRuleId(CodingRuleId ruleId);

    /**
     * 슬라이스 조건으로 규칙 예시 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 규칙 예시 목록
     */
    List<RuleExample> findBySliceCriteria(RuleExampleSliceCriteria criteria);

    /**
     * 전체 규칙 예시 목록 조회
     *
     * @return 규칙 예시 목록
     */
    List<RuleExample> findAll();
}
