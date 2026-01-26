package com.ryuqq.application.codingrule.port.out;

import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;

/**
 * CodingRuleCommandPort - 코딩 규칙 명령 Port
 *
 * <p>영속성 계층으로의 CodingRule CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface CodingRuleCommandPort {

    /**
     * CodingRule 영속화 (생성/수정/삭제)
     *
     * @param codingRule 영속화할 CodingRule
     * @return 영속화된 CodingRule ID
     */
    CodingRuleId persist(CodingRule codingRule);
}
