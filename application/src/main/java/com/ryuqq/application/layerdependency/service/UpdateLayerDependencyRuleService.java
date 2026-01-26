package com.ryuqq.application.layerdependency.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.factory.command.LayerDependencyRuleCommandFactory;
import com.ryuqq.application.layerdependency.manager.LayerDependencyRulePersistenceManager;
import com.ryuqq.application.layerdependency.port.in.UpdateLayerDependencyRuleUseCase;
import com.ryuqq.application.layerdependency.validator.LayerDependencyRuleValidator;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRuleUpdateData;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import org.springframework.stereotype.Service;

/**
 * UpdateLayerDependencyRuleService - 레이어 의존성 규칙 수정 서비스
 *
 * <p>레이어 의존성 규칙 수정 유스케이스를 구현합니다.
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
public class UpdateLayerDependencyRuleService implements UpdateLayerDependencyRuleUseCase {

    private final LayerDependencyRuleValidator layerDependencyRuleValidator;
    private final LayerDependencyRuleCommandFactory layerDependencyRuleCommandFactory;
    private final LayerDependencyRulePersistenceManager layerDependencyRulePersistenceManager;

    public UpdateLayerDependencyRuleService(
            LayerDependencyRuleValidator layerDependencyRuleValidator,
            LayerDependencyRuleCommandFactory layerDependencyRuleCommandFactory,
            LayerDependencyRulePersistenceManager layerDependencyRulePersistenceManager) {
        this.layerDependencyRuleValidator = layerDependencyRuleValidator;
        this.layerDependencyRuleCommandFactory = layerDependencyRuleCommandFactory;
        this.layerDependencyRulePersistenceManager = layerDependencyRulePersistenceManager;
    }

    @Override
    public void execute(UpdateLayerDependencyRuleCommand command) {
        layerDependencyRuleValidator.validateArchitectureExists(
                ArchitectureId.of(command.architectureId()));

        UpdateContext<LayerDependencyRuleId, LayerDependencyRuleUpdateData> context =
                layerDependencyRuleCommandFactory.createUpdateContext(command);

        LayerDependencyRule layerDependencyRule =
                layerDependencyRuleValidator.findExistingOrThrow(context.id());

        layerDependencyRule.update(context.updateData(), context.changedAt());

        layerDependencyRulePersistenceManager.persist(layerDependencyRule);
    }
}
