package com.ryuqq.application.zerotolerance.manager;

import com.ryuqq.application.zerotolerance.port.out.ZeroToleranceRuleCommandPort;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ZeroToleranceRulePersistenceManager - Zero-Tolerance 규칙 영속성 매니저
 *
 * <p>Zero-Tolerance 규칙 저장 로직을 담당합니다.
 *
 * <p>MGR-001: Manager는 ReadManager/PersistenceManager로 분리.
 *
 * <p>MGR-003: PersistenceManager는 CommandPort만 의존.
 *
 * <p>MGR-004: @Transactional은 Manager에서만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class ZeroToleranceRulePersistenceManager {

    private final ZeroToleranceRuleCommandPort zeroToleranceRuleCommandPort;

    public ZeroToleranceRulePersistenceManager(
            ZeroToleranceRuleCommandPort zeroToleranceRuleCommandPort) {
        this.zeroToleranceRuleCommandPort = zeroToleranceRuleCommandPort;
    }

    /**
     * Zero-Tolerance 규칙 저장 (생성 및 수정)
     *
     * @param zeroToleranceRule 저장할 Zero-Tolerance 규칙
     * @return 저장된 Zero-Tolerance 규칙 ID
     */
    @Transactional
    public ZeroToleranceRuleId persist(ZeroToleranceRule zeroToleranceRule) {
        return zeroToleranceRuleCommandPort.persist(zeroToleranceRule);
    }
}
