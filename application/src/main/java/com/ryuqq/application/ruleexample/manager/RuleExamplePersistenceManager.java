package com.ryuqq.application.ruleexample.manager;

import com.ryuqq.application.ruleexample.port.out.RuleExampleCommandPort;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RuleExamplePersistenceManager - 규칙 예시 영속화 관리자
 *
 * <p>규칙 예시 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class RuleExamplePersistenceManager {

    private final RuleExampleCommandPort ruleExampleCommandPort;

    public RuleExamplePersistenceManager(RuleExampleCommandPort ruleExampleCommandPort) {
        this.ruleExampleCommandPort = ruleExampleCommandPort;
    }

    /**
     * 규칙 예시 영속화 (생성 또는 수정)
     *
     * @param ruleExample 영속화할 규칙 예시
     * @return 영속화된 규칙 예시 ID
     */
    @Transactional
    public RuleExampleId persist(RuleExample ruleExample) {
        return ruleExampleCommandPort.persist(ruleExample);
    }
}
