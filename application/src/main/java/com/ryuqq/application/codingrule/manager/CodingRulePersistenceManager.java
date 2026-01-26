package com.ryuqq.application.codingrule.manager;

import com.ryuqq.application.codingrule.port.out.CodingRuleCommandPort;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * CodingRulePersistenceManager - 코딩 규칙 영속화 관리자
 *
 * <p>코딩 규칙 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class CodingRulePersistenceManager {

    private final CodingRuleCommandPort codingRuleCommandPort;

    public CodingRulePersistenceManager(CodingRuleCommandPort codingRuleCommandPort) {
        this.codingRuleCommandPort = codingRuleCommandPort;
    }

    /**
     * 코딩 규칙 영속화 (생성 또는 수정)
     *
     * @param codingRule 영속화할 코딩 규칙
     * @return 영속화된 코딩 규칙 ID
     */
    @Transactional
    public CodingRuleId persist(CodingRule codingRule) {
        return codingRuleCommandPort.persist(codingRule);
    }
}
