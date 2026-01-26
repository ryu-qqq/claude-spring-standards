package com.ryuqq.application.packagestructure.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;
import com.ryuqq.application.packagestructure.factory.command.PackageStructureCommandFactory;
import com.ryuqq.application.packagestructure.manager.PackageStructurePersistenceManager;
import com.ryuqq.application.packagestructure.port.in.UpdatePackageStructureUseCase;
import com.ryuqq.application.packagestructure.validator.PackageStructureValidator;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructureUpdateData;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import org.springframework.stereotype.Service;

/**
 * UpdatePackageStructureService - 패키지 구조 수정 서비스
 *
 * <p>UpdatePackageStructureUseCase를 구현합니다.
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
public class UpdatePackageStructureService implements UpdatePackageStructureUseCase {

    private final PackageStructureValidator packageStructureValidator;
    private final PackageStructureCommandFactory packageStructureCommandFactory;
    private final PackageStructurePersistenceManager packageStructurePersistenceManager;

    public UpdatePackageStructureService(
            PackageStructureValidator packageStructureValidator,
            PackageStructureCommandFactory packageStructureCommandFactory,
            PackageStructurePersistenceManager packageStructurePersistenceManager) {
        this.packageStructureValidator = packageStructureValidator;
        this.packageStructureCommandFactory = packageStructureCommandFactory;
        this.packageStructurePersistenceManager = packageStructurePersistenceManager;
    }

    @Override
    public void execute(UpdatePackageStructureCommand command) {
        UpdateContext<PackageStructureId, PackageStructureUpdateData> context =
                packageStructureCommandFactory.createUpdateContext(command);

        PackageStructure packageStructure =
                packageStructureValidator.findExistingOrThrow(context.id());

        PathPattern newPathPattern = PathPattern.of(command.pathPattern());
        packageStructureValidator.validateNotDuplicateExcluding(
                packageStructure.moduleId(), newPathPattern, context.id());

        packageStructure.update(context.updateData(), context.changedAt());

        packageStructurePersistenceManager.persist(packageStructure);
    }
}
