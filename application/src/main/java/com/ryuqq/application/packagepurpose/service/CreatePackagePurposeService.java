package com.ryuqq.application.packagepurpose.service;

import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.factory.command.PackagePurposeCommandFactory;
import com.ryuqq.application.packagepurpose.manager.PackagePurposePersistenceManager;
import com.ryuqq.application.packagepurpose.port.in.CreatePackagePurposeUseCase;
import com.ryuqq.application.packagepurpose.validator.PackagePurposeValidator;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Service;

/**
 * CreatePackagePurposeService - 패키지 목적 생성 서비스
 *
 * <p>CreatePackagePurposeUseCase를 구현합니다.
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
public class CreatePackagePurposeService implements CreatePackagePurposeUseCase {

    private final PackagePurposeValidator packagePurposeValidator;
    private final PackagePurposeCommandFactory packagePurposeCommandFactory;
    private final PackagePurposePersistenceManager packagePurposePersistenceManager;

    public CreatePackagePurposeService(
            PackagePurposeValidator packagePurposeValidator,
            PackagePurposeCommandFactory packagePurposeCommandFactory,
            PackagePurposePersistenceManager packagePurposePersistenceManager) {
        this.packagePurposeValidator = packagePurposeValidator;
        this.packagePurposeCommandFactory = packagePurposeCommandFactory;
        this.packagePurposePersistenceManager = packagePurposePersistenceManager;
    }

    @Override
    public Long execute(CreatePackagePurposeCommand command) {
        PackageStructureId structureId = PackageStructureId.of(command.structureId());
        PurposeCode purposeCode = PurposeCode.of(command.code());
        packagePurposeValidator.validateNotDuplicate(structureId, purposeCode);

        PackagePurpose packagePurpose = packagePurposeCommandFactory.create(command);
        PackagePurposeId savedId = packagePurposePersistenceManager.persist(packagePurpose);

        return savedId.value();
    }
}
