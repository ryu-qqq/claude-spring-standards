package com.ryuqq.application.layerdependency.service;

import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.factory.command.LayerDependencyRuleCommandFactory;
import com.ryuqq.application.layerdependency.manager.LayerDependencyRulePersistenceManager;
import com.ryuqq.application.layerdependency.port.in.CreateLayerDependencyRuleUseCase;
import com.ryuqq.application.layerdependency.validator.LayerDependencyRuleValidator;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import org.springframework.stereotype.Service;

/**
 * CreateLayerDependencyRuleService - 레이어 의존성 규칙 생성 서비스
 *
 * <p>레이어 의존성 규칙 생성 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class CreateLayerDependencyRuleService implements CreateLayerDependencyRuleUseCase {

    private final LayerDependencyRuleValidator layerDependencyRuleValidator;
    private final LayerDependencyRuleCommandFactory layerDependencyRuleCommandFactory;
    private final LayerDependencyRulePersistenceManager layerDependencyRulePersistenceManager;

    public CreateLayerDependencyRuleService(
            LayerDependencyRuleValidator layerDependencyRuleValidator,
            LayerDependencyRuleCommandFactory layerDependencyRuleCommandFactory,
            LayerDependencyRulePersistenceManager layerDependencyRulePersistenceManager) {
        this.layerDependencyRuleValidator = layerDependencyRuleValidator;
        this.layerDependencyRuleCommandFactory = layerDependencyRuleCommandFactory;
        this.layerDependencyRulePersistenceManager = layerDependencyRulePersistenceManager;
    }

    @Override
    public Long execute(CreateLayerDependencyRuleCommand command) {
        layerDependencyRuleValidator.validateArchitectureExists(
                ArchitectureId.of(command.architectureId()));

        LayerDependencyRule layerDependencyRule = layerDependencyRuleCommandFactory.create(command);
        LayerDependencyRuleId savedId =
                layerDependencyRulePersistenceManager.persist(layerDependencyRule);

        return savedId.value();
    }
}
