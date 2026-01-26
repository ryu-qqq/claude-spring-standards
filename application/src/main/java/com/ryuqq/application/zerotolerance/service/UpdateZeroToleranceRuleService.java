package com.ryuqq.application.zerotolerance.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.zerotolerance.dto.command.UpdateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.factory.command.ZeroToleranceRuleCommandFactory;
import com.ryuqq.application.zerotolerance.manager.ZeroToleranceRulePersistenceManager;
import com.ryuqq.application.zerotolerance.port.in.UpdateZeroToleranceRuleUseCase;
import com.ryuqq.application.zerotolerance.validator.ZeroToleranceRuleValidator;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRuleUpdateData;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import org.springframework.stereotype.Service;

/**
 * UpdateZeroToleranceRuleService - Zero-Tolerance 규칙 수정 서비스
 *
 * <p>Zero-Tolerance 규칙 수정 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateZeroToleranceRuleService implements UpdateZeroToleranceRuleUseCase {

    private final ZeroToleranceRuleValidator zeroToleranceRuleValidator;
    private final ZeroToleranceRuleCommandFactory zeroToleranceRuleCommandFactory;
    private final ZeroToleranceRulePersistenceManager zeroToleranceRulePersistenceManager;

    public UpdateZeroToleranceRuleService(
            ZeroToleranceRuleValidator zeroToleranceRuleValidator,
            ZeroToleranceRuleCommandFactory zeroToleranceRuleCommandFactory,
            ZeroToleranceRulePersistenceManager zeroToleranceRulePersistenceManager) {
        this.zeroToleranceRuleValidator = zeroToleranceRuleValidator;
        this.zeroToleranceRuleCommandFactory = zeroToleranceRuleCommandFactory;
        this.zeroToleranceRulePersistenceManager = zeroToleranceRulePersistenceManager;
    }

    @Override
    public void execute(UpdateZeroToleranceRuleCommand command) {
        UpdateContext<ZeroToleranceRuleId, ZeroToleranceRuleUpdateData> context =
                zeroToleranceRuleCommandFactory.createUpdateContext(command);

        ZeroToleranceRule zeroToleranceRule =
                zeroToleranceRuleValidator.findExistingOrThrow(context.id());

        zeroToleranceRule.update(context.updateData(), context.changedAt());

        zeroToleranceRulePersistenceManager.persist(zeroToleranceRule);
    }
}
