package com.ryuqq.application.architecture.service;

import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import com.ryuqq.application.architecture.factory.command.ArchitectureCommandFactory;
import com.ryuqq.application.architecture.manager.ArchitecturePersistenceManager;
import com.ryuqq.application.architecture.port.in.CreateArchitectureUseCase;
import com.ryuqq.application.architecture.validator.ArchitectureValidator;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.springframework.stereotype.Service;

/**
 * CreateArchitectureService - Architecture 생성 서비스
 *
 * <p>CreateArchitectureUseCase를 구현합니다.
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
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 */
@Service
public class CreateArchitectureService implements CreateArchitectureUseCase {

    private final ArchitectureValidator architectureValidator;
    private final ArchitectureCommandFactory architectureCommandFactory;
    private final ArchitecturePersistenceManager architecturePersistenceManager;

    public CreateArchitectureService(
            ArchitectureValidator architectureValidator,
            ArchitectureCommandFactory architectureCommandFactory,
            ArchitecturePersistenceManager architecturePersistenceManager) {
        this.architectureValidator = architectureValidator;
        this.architectureCommandFactory = architectureCommandFactory;
        this.architecturePersistenceManager = architecturePersistenceManager;
    }

    @Override
    public Long execute(CreateArchitectureCommand command) {
        architectureValidator.validateTechStackExists(TechStackId.of(command.techStackId()));
        architectureValidator.validateNameNotDuplicate(ArchitectureName.of(command.name()));

        Architecture architecture = architectureCommandFactory.create(command);
        return architecturePersistenceManager.persist(architecture);
    }
}
