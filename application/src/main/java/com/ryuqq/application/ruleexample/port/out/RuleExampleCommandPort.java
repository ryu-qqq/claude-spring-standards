package com.ryuqq.application.ruleexample.port.out;

import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;

/**
 * RuleExampleCommandPort - 규칙 예시 명령 Port
 *
 * <p>영속성 계층으로의 RuleExample CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface RuleExampleCommandPort {

    /**
     * RuleExample 영속화 (생성/수정/삭제)
     *
     * @param ruleExample 영속화할 RuleExample
     * @return 영속화된 RuleExample ID
     */
    RuleExampleId persist(RuleExample ruleExample);
}
