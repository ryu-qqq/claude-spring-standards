package com.ryuqq.application.archunittest.service;

import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.factory.command.ArchUnitTestCommandFactory;
import com.ryuqq.application.archunittest.manager.ArchUnitTestPersistenceManager;
import com.ryuqq.application.archunittest.port.in.CreateArchUnitTestUseCase;
import com.ryuqq.application.archunittest.validator.ArchUnitTestValidator;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import org.springframework.stereotype.Service;

/**
 * CreateArchUnitTestService - ArchUnit 테스트 생성 서비스
 *
 * <p>ArchUnit 테스트 생성 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class CreateArchUnitTestService implements CreateArchUnitTestUseCase {

    private final ArchUnitTestValidator archUnitTestValidator;
    private final ArchUnitTestCommandFactory archUnitTestCommandFactory;
    private final ArchUnitTestPersistenceManager archUnitTestPersistenceManager;

    public CreateArchUnitTestService(
            ArchUnitTestValidator archUnitTestValidator,
            ArchUnitTestCommandFactory archUnitTestCommandFactory,
            ArchUnitTestPersistenceManager archUnitTestPersistenceManager) {
        this.archUnitTestValidator = archUnitTestValidator;
        this.archUnitTestCommandFactory = archUnitTestCommandFactory;
        this.archUnitTestPersistenceManager = archUnitTestPersistenceManager;
    }

    @Override
    public Long execute(CreateArchUnitTestCommand command) {
        archUnitTestValidator.validateNotDuplicate(
                archUnitTestCommandFactory.toStructureId(command),
                archUnitTestCommandFactory.toCode(command));

        ArchUnitTest archUnitTest = archUnitTestCommandFactory.create(command);
        ArchUnitTestId savedId = archUnitTestPersistenceManager.persist(archUnitTest);

        return savedId.value();
    }
}
