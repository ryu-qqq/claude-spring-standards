package com.ryuqq.application.layer.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;
import com.ryuqq.application.layer.factory.command.LayerCommandFactory;
import com.ryuqq.application.layer.manager.LayerPersistenceManager;
import com.ryuqq.application.layer.port.in.UpdateLayerUseCase;
import com.ryuqq.application.layer.validator.LayerValidator;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.aggregate.LayerUpdateData;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import org.springframework.stereotype.Service;

/**
 * UpdateLayerService - Layer 수정 서비스
 *
 * <p>UpdateLayerUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드로 Domain 객체를 조회합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class UpdateLayerService implements UpdateLayerUseCase {

    private final LayerValidator layerValidator;
    private final LayerCommandFactory layerCommandFactory;
    private final LayerPersistenceManager layerPersistenceManager;

    public UpdateLayerService(
            LayerValidator layerValidator,
            LayerCommandFactory layerCommandFactory,
            LayerPersistenceManager layerPersistenceManager) {
        this.layerValidator = layerValidator;
        this.layerCommandFactory = layerCommandFactory;
        this.layerPersistenceManager = layerPersistenceManager;
    }

    @Override
    public void execute(UpdateLayerCommand command) {
        UpdateContext<LayerId, LayerUpdateData> context =
                layerCommandFactory.createUpdateContext(command);

        Layer layer = layerValidator.findExistingOrThrow(context.id());

        layerValidator.validateCodeNotDuplicatedExcluding(
                ArchitectureId.of(layer.architectureIdValue()),
                LayerCode.of(command.code()),
                context.id());

        layer.update(context.updateData(), context.changedAt());
        layerPersistenceManager.persist(layer);
    }
}
