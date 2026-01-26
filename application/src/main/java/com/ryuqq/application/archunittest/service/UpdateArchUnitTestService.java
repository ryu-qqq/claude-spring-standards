package com.ryuqq.application.archunittest.service;

import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.archunittest.factory.command.ArchUnitTestCommandFactory;
import com.ryuqq.application.archunittest.manager.ArchUnitTestPersistenceManager;
import com.ryuqq.application.archunittest.port.in.UpdateArchUnitTestUseCase;
import com.ryuqq.application.archunittest.validator.ArchUnitTestValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTestUpdateData;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import org.springframework.stereotype.Service;

/**
 * UpdateArchUnitTestService - ArchUnit 테스트 수정 서비스
 *
 * <p>ArchUnit 테스트 수정 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateArchUnitTestService implements UpdateArchUnitTestUseCase {

    private final ArchUnitTestValidator archUnitTestValidator;
    private final ArchUnitTestCommandFactory archUnitTestCommandFactory;
    private final ArchUnitTestPersistenceManager archUnitTestPersistenceManager;

    public UpdateArchUnitTestService(
            ArchUnitTestValidator archUnitTestValidator,
            ArchUnitTestCommandFactory archUnitTestCommandFactory,
            ArchUnitTestPersistenceManager archUnitTestPersistenceManager) {
        this.archUnitTestValidator = archUnitTestValidator;
        this.archUnitTestCommandFactory = archUnitTestCommandFactory;
        this.archUnitTestPersistenceManager = archUnitTestPersistenceManager;
    }

    @Override
    public void execute(UpdateArchUnitTestCommand command) {
        UpdateContext<ArchUnitTestId, ArchUnitTestUpdateData> context =
                archUnitTestCommandFactory.createUpdateContext(command);

        ArchUnitTest archUnitTest = archUnitTestValidator.findExistingOrThrow(context.id());

        String newCode = archUnitTestCommandFactory.toCode(command);
        if (newCode != null) {
            archUnitTestValidator.validateNotDuplicateExcluding(
                    archUnitTest.structureId(), newCode, context.id());
        }

        archUnitTest.update(context.updateData(), context.changedAt());

        archUnitTestPersistenceManager.persist(archUnitTest);
    }
}
