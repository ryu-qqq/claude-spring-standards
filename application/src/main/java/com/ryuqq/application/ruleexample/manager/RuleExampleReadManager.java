package com.ryuqq.application.ruleexample.manager;

import com.ryuqq.application.ruleexample.port.out.RuleExampleQueryPort;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.exception.RuleExampleNotFoundException;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RuleExampleReadManager - 규칙 예시 조회 관리자
 *
 * <p>규칙 예시 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleReadManager {

    private final RuleExampleQueryPort ruleExampleQueryPort;

    public RuleExampleReadManager(RuleExampleQueryPort ruleExampleQueryPort) {
        this.ruleExampleQueryPort = ruleExampleQueryPort;
    }

    /**
     * ID로 규칙 예시 조회 (존재하지 않으면 예외)
     *
     * @param ruleExampleId 규칙 예시 ID
     * @return 규칙 예시
     * @throws RuleExampleNotFoundException 규칙 예시가 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public RuleExample getById(RuleExampleId ruleExampleId) {
        return ruleExampleQueryPort
                .findById(ruleExampleId)
                .orElseThrow(() -> new RuleExampleNotFoundException(ruleExampleId.value()));
    }

    /**
     * ID로 규칙 예시 존재 여부 확인 후 반환
     *
     * @param ruleExampleId 규칙 예시 ID
     * @return 규칙 예시 (nullable)
     */
    @Transactional(readOnly = true)
    public RuleExample findById(RuleExampleId ruleExampleId) {
        return ruleExampleQueryPort.findById(ruleExampleId).orElse(null);
    }

    /**
     * 슬라이스 조건으로 규칙 예시 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 규칙 예시 목록
     */
    @Transactional(readOnly = true)
    public List<RuleExample> findBySliceCriteria(RuleExampleSliceCriteria criteria) {
        return ruleExampleQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 코딩 규칙 ID로 규칙 예시 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 규칙 예시 목록
     */
    @Transactional(readOnly = true)
    public List<RuleExample> findByRuleId(CodingRuleId ruleId) {
        return ruleExampleQueryPort.findByRuleId(ruleId);
    }
}
