package com.ryuqq.application.convention.service;

import com.ryuqq.application.convention.dto.command.CreateConventionCommand;
import com.ryuqq.application.convention.factory.command.ConventionCommandFactory;
import com.ryuqq.application.convention.manager.ConventionPersistenceManager;
import com.ryuqq.application.convention.port.in.CreateConventionUseCase;
import com.ryuqq.application.convention.validator.ConventionValidator;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import org.springframework.stereotype.Service;

/**
 * CreateConventionService - Convention 생성 서비스
 *
 * <p>CreateConventionUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 -> Factory 사용.
 *
 * <p>SVC-006: @Transactional 금지 -> Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 -> 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 -> Manager 사용.
 *
 * <p>APP-FAC-001: 단순 VO 변환은 Service에서 직접 호출합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 */
@Service
public class CreateConventionService implements CreateConventionUseCase {

    private final ConventionValidator conventionValidator;
    private final ConventionCommandFactory conventionCommandFactory;
    private final ConventionPersistenceManager conventionPersistenceManager;

    public CreateConventionService(
            ConventionValidator conventionValidator,
            ConventionCommandFactory conventionCommandFactory,
            ConventionPersistenceManager conventionPersistenceManager) {
        this.conventionValidator = conventionValidator;
        this.conventionCommandFactory = conventionCommandFactory;
        this.conventionPersistenceManager = conventionPersistenceManager;
    }

    @Override
    public Long execute(CreateConventionCommand command) {
        conventionValidator.validateNotDuplicate(
                ModuleId.of(command.moduleId()), ConventionVersion.of(command.version()));

        Convention convention = conventionCommandFactory.create(command);
        return conventionPersistenceManager.persist(convention);
    }
}
