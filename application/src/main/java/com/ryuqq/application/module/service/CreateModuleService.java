package com.ryuqq.application.module.service;

import com.ryuqq.application.module.dto.command.CreateModuleCommand;
import com.ryuqq.application.module.factory.command.ModuleCommandFactory;
import com.ryuqq.application.module.manager.ModulePersistenceManager;
import com.ryuqq.application.module.port.in.CreateModuleUseCase;
import com.ryuqq.application.module.validator.ModuleValidator;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.vo.ModuleName;
import org.springframework.stereotype.Service;

/**
 * CreateModuleService - Module 생성 서비스
 *
 * <p>CreateModuleUseCase를 구현합니다.
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
public class CreateModuleService implements CreateModuleUseCase {

    private final ModuleValidator moduleValidator;
    private final ModuleCommandFactory moduleCommandFactory;
    private final ModulePersistenceManager modulePersistenceManager;

    public CreateModuleService(
            ModuleValidator moduleValidator,
            ModuleCommandFactory moduleCommandFactory,
            ModulePersistenceManager modulePersistenceManager) {
        this.moduleValidator = moduleValidator;
        this.moduleCommandFactory = moduleCommandFactory;
        this.modulePersistenceManager = modulePersistenceManager;
    }

    @Override
    public Long execute(CreateModuleCommand command) {
        moduleValidator.validateNotDuplicate(
                LayerId.of(command.layerId()), ModuleName.of(command.name()));

        Module module = moduleCommandFactory.create(command);
        return modulePersistenceManager.persist(module);
    }
}
