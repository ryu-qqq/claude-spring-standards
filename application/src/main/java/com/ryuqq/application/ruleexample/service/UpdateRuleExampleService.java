package com.ryuqq.application.ruleexample.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.application.ruleexample.factory.command.RuleExampleCommandFactory;
import com.ryuqq.application.ruleexample.manager.RuleExamplePersistenceManager;
import com.ryuqq.application.ruleexample.port.in.UpdateRuleExampleUseCase;
import com.ryuqq.application.ruleexample.validator.RuleExampleValidator;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.aggregate.RuleExampleUpdateData;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.springframework.stereotype.Service;

/**
 * UpdateRuleExampleService - 규칙 예시 수정 서비스
 *
 * <p>규칙 예시 수정 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
 *
 * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateRuleExampleService implements UpdateRuleExampleUseCase {

    private final RuleExampleCommandFactory ruleExampleCommandFactory;
    private final RuleExampleValidator ruleExampleValidator;
    private final RuleExamplePersistenceManager ruleExamplePersistenceManager;

    public UpdateRuleExampleService(
            RuleExampleCommandFactory ruleExampleCommandFactory,
            RuleExampleValidator ruleExampleValidator,
            RuleExamplePersistenceManager ruleExamplePersistenceManager) {
        this.ruleExampleCommandFactory = ruleExampleCommandFactory;
        this.ruleExampleValidator = ruleExampleValidator;
        this.ruleExamplePersistenceManager = ruleExamplePersistenceManager;
    }

    @Override
    public void execute(UpdateRuleExampleCommand command) {
        UpdateContext<RuleExampleId, RuleExampleUpdateData> context =
                ruleExampleCommandFactory.createUpdateContext(command);

        RuleExample ruleExample = ruleExampleValidator.findExistingOrThrow(context.id());

        ruleExample.update(context.updateData(), context.changedAt());

        ruleExamplePersistenceManager.persist(ruleExample);
    }
}
