package com.ryuqq.application.codingrule.service;

import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.codingrule.factory.command.CodingRuleCommandFactory;
import com.ryuqq.application.codingrule.manager.CodingRulePersistenceManager;
import com.ryuqq.application.codingrule.port.in.UpdateCodingRuleUseCase;
import com.ryuqq.application.codingrule.validator.CodingRuleValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.aggregate.CodingRuleUpdateData;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.springframework.stereotype.Service;

/**
 * UpdateCodingRuleService - 코딩 규칙 수정 서비스
 *
 * <p>UpdateCodingRuleUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → UpdateContext.changedAt() 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateCodingRuleService implements UpdateCodingRuleUseCase {

    private final CodingRuleValidator codingRuleValidator;
    private final CodingRuleCommandFactory codingRuleCommandFactory;
    private final CodingRulePersistenceManager codingRulePersistenceManager;

    public UpdateCodingRuleService(
            CodingRuleValidator codingRuleValidator,
            CodingRuleCommandFactory codingRuleCommandFactory,
            CodingRulePersistenceManager codingRulePersistenceManager) {
        this.codingRuleValidator = codingRuleValidator;
        this.codingRuleCommandFactory = codingRuleCommandFactory;
        this.codingRulePersistenceManager = codingRulePersistenceManager;
    }

    @Override
    public void execute(UpdateCodingRuleCommand command) {
        UpdateContext<CodingRuleId, CodingRuleUpdateData> context =
                codingRuleCommandFactory.createUpdateContext(command);

        CodingRule codingRule = codingRuleValidator.findExistingOrThrow(context.id());

        codingRuleValidator.validateNotDuplicateExcluding(
                codingRule.conventionId(), context.updateData().code(), context.id());

        codingRule.update(context.updateData(), context.changedAt());

        codingRulePersistenceManager.persist(codingRule);
    }
}
