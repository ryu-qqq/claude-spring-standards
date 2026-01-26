package com.ryuqq.application.codingrule.service;

import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.factory.command.CodingRuleCommandFactory;
import com.ryuqq.application.codingrule.manager.CodingRulePersistenceManager;
import com.ryuqq.application.codingrule.port.in.CreateCodingRuleUseCase;
import com.ryuqq.application.codingrule.validator.CodingRuleValidator;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
import org.springframework.stereotype.Service;

/**
 * CreateCodingRuleService - 코딩 규칙 생성 서비스
 *
 * <p>CreateCodingRuleUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * @author ryu-qqq
 */
@Service
public class CreateCodingRuleService implements CreateCodingRuleUseCase {

    private final CodingRuleValidator codingRuleValidator;
    private final CodingRuleCommandFactory codingRuleCommandFactory;
    private final CodingRulePersistenceManager codingRulePersistenceManager;

    public CreateCodingRuleService(
            CodingRuleValidator codingRuleValidator,
            CodingRuleCommandFactory codingRuleCommandFactory,
            CodingRulePersistenceManager codingRulePersistenceManager) {
        this.codingRuleValidator = codingRuleValidator;
        this.codingRuleCommandFactory = codingRuleCommandFactory;
        this.codingRulePersistenceManager = codingRulePersistenceManager;
    }

    @Override
    public Long execute(CreateCodingRuleCommand command) {
        ConventionId conventionId = ConventionId.of(command.conventionId());
        RuleCode ruleCode = RuleCode.of(command.code());

        codingRuleValidator.validateNotDuplicate(conventionId, ruleCode);

        CodingRule codingRule = codingRuleCommandFactory.create(command);
        CodingRuleId savedId = codingRulePersistenceManager.persist(codingRule);

        return savedId.value();
    }
}
