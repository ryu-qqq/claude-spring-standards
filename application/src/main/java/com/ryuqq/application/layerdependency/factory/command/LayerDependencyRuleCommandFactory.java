package com.ryuqq.application.layerdependency.factory.command;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRuleUpdateData;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.vo.ConditionDescription;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleCommandFactory - 레이어 의존성 규칙 커맨드 팩토리
 *
 * <p>레이어 의존성 규칙 생성 및 수정에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class LayerDependencyRuleCommandFactory {

    private final TimeProvider timeProvider;

    public LayerDependencyRuleCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateLayerDependencyRuleCommand로부터 LayerDependencyRule 도메인 객체 생성
     *
     * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
     *
     * @param command 생성 커맨드
     * @return 새로운 LayerDependencyRule 인스턴스
     */
    public LayerDependencyRule create(CreateLayerDependencyRuleCommand command) {
        return LayerDependencyRule.forNew(
                ArchitectureId.of(command.architectureId()),
                LayerType.valueOf(command.fromLayer()),
                LayerType.valueOf(command.toLayer()),
                DependencyType.valueOf(command.dependencyType()),
                toConditionDescription(command.conditionDescription()),
                timeProvider.now());
    }

    private ConditionDescription toConditionDescription(String description) {
        if (description == null || description.isBlank()) {
            return ConditionDescription.empty();
        }
        return ConditionDescription.of(description);
    }

    /**
     * UpdateLayerDependencyRuleCommand로부터 LayerDependencyRuleUpdateData 생성
     *
     * @param command 수정 커맨드
     * @return LayerDependencyRuleUpdateData
     */
    public LayerDependencyRuleUpdateData createUpdateData(
            UpdateLayerDependencyRuleCommand command) {
        return new LayerDependencyRuleUpdateData(
                LayerType.valueOf(command.fromLayer()),
                LayerType.valueOf(command.toLayer()),
                DependencyType.valueOf(command.dependencyType()),
                toConditionDescription(command.conditionDescription()));
    }

    /**
     * UpdateLayerDependencyRuleCommand로부터 LayerDependencyRuleId와 LayerDependencyRuleUpdateData 생성
     *
     * <p>업데이트에 필요한 ID와 UpdateData를 한 번에 생성합니다.
     *
     * @param command 수정 커맨드
     * @return UpdateContext (id, updateData)
     */
    public UpdateContext<LayerDependencyRuleId, LayerDependencyRuleUpdateData> createUpdateContext(
            UpdateLayerDependencyRuleCommand command) {
        LayerDependencyRuleId id = LayerDependencyRuleId.of(command.layerDependencyRuleId());
        LayerDependencyRuleUpdateData updateData = createUpdateData(command);
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}
