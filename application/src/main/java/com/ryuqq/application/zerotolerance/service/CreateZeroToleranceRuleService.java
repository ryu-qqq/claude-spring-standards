package com.ryuqq.application.zerotolerance.service;

import com.ryuqq.application.zerotolerance.dto.command.CreateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.factory.command.ZeroToleranceRuleCommandFactory;
import com.ryuqq.application.zerotolerance.manager.ZeroToleranceRulePersistenceManager;
import com.ryuqq.application.zerotolerance.port.in.CreateZeroToleranceRuleUseCase;
import com.ryuqq.application.zerotolerance.validator.ZeroToleranceRuleValidator;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import org.springframework.stereotype.Service;

/**
 * CreateZeroToleranceRuleService - Zero-Tolerance 규칙 생성 서비스
 *
 * <p>Zero-Tolerance 규칙 생성 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class CreateZeroToleranceRuleService implements CreateZeroToleranceRuleUseCase {

    private final ZeroToleranceRuleValidator zeroToleranceRuleValidator;
    private final ZeroToleranceRuleCommandFactory zeroToleranceRuleCommandFactory;
    private final ZeroToleranceRulePersistenceManager zeroToleranceRulePersistenceManager;

    public CreateZeroToleranceRuleService(
            ZeroToleranceRuleValidator zeroToleranceRuleValidator,
            ZeroToleranceRuleCommandFactory zeroToleranceRuleCommandFactory,
            ZeroToleranceRulePersistenceManager zeroToleranceRulePersistenceManager) {
        this.zeroToleranceRuleValidator = zeroToleranceRuleValidator;
        this.zeroToleranceRuleCommandFactory = zeroToleranceRuleCommandFactory;
        this.zeroToleranceRulePersistenceManager = zeroToleranceRulePersistenceManager;
    }

    @Override
    public Long execute(CreateZeroToleranceRuleCommand command) {
        zeroToleranceRuleValidator.validateNotDuplicate(CodingRuleId.of(command.ruleId()));

        ZeroToleranceRule zeroToleranceRule = zeroToleranceRuleCommandFactory.create(command);
        ZeroToleranceRuleId savedId =
                zeroToleranceRulePersistenceManager.persist(zeroToleranceRule);

        return savedId.value();
    }
}
