package com.ryuqq.application.packagepurpose.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.factory.command.PackagePurposeCommandFactory;
import com.ryuqq.application.packagepurpose.manager.PackagePurposePersistenceManager;
import com.ryuqq.application.packagepurpose.port.in.UpdatePackagePurposeUseCase;
import com.ryuqq.application.packagepurpose.validator.PackagePurposeValidator;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurposeUpdateData;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import org.springframework.stereotype.Service;

/**
 * UpdatePackagePurposeService - 패키지 목적 수정 서비스
 *
 * <p>UpdatePackagePurposeUseCase를 구현합니다.
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
 * <p>APP-VAL-001: Validator.validateAndGetForUpdate()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Service
public class UpdatePackagePurposeService implements UpdatePackagePurposeUseCase {

    private final PackagePurposeValidator packagePurposeValidator;
    private final PackagePurposeCommandFactory packagePurposeCommandFactory;
    private final PackagePurposePersistenceManager packagePurposePersistenceManager;

    public UpdatePackagePurposeService(
            PackagePurposeValidator packagePurposeValidator,
            PackagePurposeCommandFactory packagePurposeCommandFactory,
            PackagePurposePersistenceManager packagePurposePersistenceManager) {
        this.packagePurposeValidator = packagePurposeValidator;
        this.packagePurposeCommandFactory = packagePurposeCommandFactory;
        this.packagePurposePersistenceManager = packagePurposePersistenceManager;
    }

    @Override
    public void execute(UpdatePackagePurposeCommand command) {
        UpdateContext<PackagePurposeId, PackagePurposeUpdateData> context =
                packagePurposeCommandFactory.createUpdateContext(command);

        PackagePurpose packagePurpose =
                packagePurposeValidator.validateAndGetForUpdate(context.id(), context.updateData());

        packagePurpose.update(context.updateData(), context.changedAt());

        packagePurposePersistenceManager.persist(packagePurpose);
    }
}
