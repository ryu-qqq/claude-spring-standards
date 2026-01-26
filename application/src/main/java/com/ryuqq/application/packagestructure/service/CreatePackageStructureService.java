package com.ryuqq.application.packagestructure.service;

import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;
import com.ryuqq.application.packagestructure.factory.command.PackageStructureCommandFactory;
import com.ryuqq.application.packagestructure.manager.PackageStructurePersistenceManager;
import com.ryuqq.application.packagestructure.port.in.CreatePackageStructureUseCase;
import com.ryuqq.application.packagestructure.validator.PackageStructureValidator;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import org.springframework.stereotype.Service;

/**
 * CreatePackageStructureService - 패키지 구조 생성 서비스
 *
 * <p>CreatePackageStructureUseCase를 구현합니다.
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
public class CreatePackageStructureService implements CreatePackageStructureUseCase {

    private final PackageStructureValidator packageStructureValidator;
    private final PackageStructureCommandFactory packageStructureCommandFactory;
    private final PackageStructurePersistenceManager packageStructurePersistenceManager;

    public CreatePackageStructureService(
            PackageStructureValidator packageStructureValidator,
            PackageStructureCommandFactory packageStructureCommandFactory,
            PackageStructurePersistenceManager packageStructurePersistenceManager) {
        this.packageStructureValidator = packageStructureValidator;
        this.packageStructureCommandFactory = packageStructureCommandFactory;
        this.packageStructurePersistenceManager = packageStructurePersistenceManager;
    }

    @Override
    public Long execute(CreatePackageStructureCommand command) {
        ModuleId moduleId = ModuleId.of(command.moduleId());
        PathPattern pathPattern = PathPattern.of(command.pathPattern());
        packageStructureValidator.validateNotDuplicate(moduleId, pathPattern);

        PackageStructure packageStructure = packageStructureCommandFactory.create(command);
        PackageStructureId savedId = packageStructurePersistenceManager.persist(packageStructure);

        return savedId.value();
    }
}
