package com.ryuqq.application.module.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.module.dto.command.UpdateModuleCommand;
import com.ryuqq.application.module.factory.command.ModuleCommandFactory;
import com.ryuqq.application.module.manager.ModulePersistenceManager;
import com.ryuqq.application.module.port.in.UpdateModuleUseCase;
import com.ryuqq.application.module.validator.ModuleValidator;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.aggregate.ModuleUpdateData;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.ModuleName;
import org.springframework.stereotype.Service;

/**
 * UpdateModuleService - Module 수정 서비스
 *
 * <p>UpdateModuleUseCase를 구현합니다.
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
public class UpdateModuleService implements UpdateModuleUseCase {

    private final ModuleValidator moduleValidator;
    private final ModuleCommandFactory moduleCommandFactory;
    private final ModulePersistenceManager modulePersistenceManager;

    public UpdateModuleService(
            ModuleValidator moduleValidator,
            ModuleCommandFactory moduleCommandFactory,
            ModulePersistenceManager modulePersistenceManager) {
        this.moduleValidator = moduleValidator;
        this.moduleCommandFactory = moduleCommandFactory;
        this.modulePersistenceManager = modulePersistenceManager;
    }

    @Override
    public void execute(UpdateModuleCommand command) {
        UpdateContext<ModuleId, ModuleUpdateData> context =
                moduleCommandFactory.createUpdateContext(command);

        Module module = moduleValidator.findExistingOrThrow(context.id());

        moduleValidator.validateNotDuplicateExcluding(
                module.layerId(), ModuleName.of(command.name()), context.id());

        module.update(context.updateData(), context.changedAt());
        modulePersistenceManager.persist(module);
    }
}
