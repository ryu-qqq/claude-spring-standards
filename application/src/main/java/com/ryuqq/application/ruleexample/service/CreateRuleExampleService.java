package com.ryuqq.application.ruleexample.service;

import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.factory.command.RuleExampleCommandFactory;
import com.ryuqq.application.ruleexample.manager.RuleExamplePersistenceManager;
import com.ryuqq.application.ruleexample.port.in.CreateRuleExampleUseCase;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import org.springframework.stereotype.Service;

/**
 * CreateRuleExampleService - 규칙 예시 생성 서비스
 *
 * <p>규칙 예시 생성 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class CreateRuleExampleService implements CreateRuleExampleUseCase {

    private final RuleExampleCommandFactory ruleExampleCommandFactory;
    private final RuleExamplePersistenceManager ruleExamplePersistenceManager;

    public CreateRuleExampleService(
            RuleExampleCommandFactory ruleExampleCommandFactory,
            RuleExamplePersistenceManager ruleExamplePersistenceManager) {
        this.ruleExampleCommandFactory = ruleExampleCommandFactory;
        this.ruleExamplePersistenceManager = ruleExamplePersistenceManager;
    }

    @Override
    public Long execute(CreateRuleExampleCommand command) {
        RuleExample ruleExample = ruleExampleCommandFactory.create(command);
        RuleExampleId savedId = ruleExamplePersistenceManager.persist(ruleExample);

        return savedId.value();
    }
}
