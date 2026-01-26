package com.ryuqq.application.layer.service;

import com.ryuqq.application.layer.dto.command.CreateLayerCommand;
import com.ryuqq.application.layer.factory.command.LayerCommandFactory;
import com.ryuqq.application.layer.manager.LayerPersistenceManager;
import com.ryuqq.application.layer.port.in.CreateLayerUseCase;
import com.ryuqq.application.layer.validator.LayerValidator;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.aggregate.Layer;
import com.ryuqq.domain.layer.vo.LayerCode;
import org.springframework.stereotype.Service;

/**
 * CreateLayerService - Layer 생성 서비스
 *
 * <p>CreateLayerUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class CreateLayerService implements CreateLayerUseCase {

    private final LayerValidator layerValidator;
    private final LayerCommandFactory layerCommandFactory;
    private final LayerPersistenceManager layerPersistenceManager;

    public CreateLayerService(
            LayerValidator layerValidator,
            LayerCommandFactory layerCommandFactory,
            LayerPersistenceManager layerPersistenceManager) {
        this.layerValidator = layerValidator;
        this.layerCommandFactory = layerCommandFactory;
        this.layerPersistenceManager = layerPersistenceManager;
    }

    @Override
    public Long execute(CreateLayerCommand command) {
        layerValidator.validateCodeNotDuplicated(
                ArchitectureId.of(command.architectureId()), LayerCode.of(command.code()));

        Layer layer = layerCommandFactory.create(command);
        return layerPersistenceManager.persist(layer);
    }
}
