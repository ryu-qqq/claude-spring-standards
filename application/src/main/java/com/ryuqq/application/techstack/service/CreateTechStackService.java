package com.ryuqq.application.techstack.service;

import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import com.ryuqq.application.techstack.factory.command.TechStackCommandFactory;
import com.ryuqq.application.techstack.manager.TechStackPersistenceManager;
import com.ryuqq.application.techstack.port.in.CreateTechStackUseCase;
import com.ryuqq.application.techstack.validator.TechStackValidator;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.vo.TechStackName;
import org.springframework.stereotype.Service;

/**
 * CreateTechStackService - TechStack 생성 서비스
 *
 * <p>CreateTechStackUseCase를 구현합니다.
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
public class CreateTechStackService implements CreateTechStackUseCase {

    private final TechStackValidator techStackValidator;
    private final TechStackCommandFactory techStackCommandFactory;
    private final TechStackPersistenceManager techStackPersistenceManager;

    public CreateTechStackService(
            TechStackValidator techStackValidator,
            TechStackCommandFactory techStackCommandFactory,
            TechStackPersistenceManager techStackPersistenceManager) {
        this.techStackValidator = techStackValidator;
        this.techStackCommandFactory = techStackCommandFactory;
        this.techStackPersistenceManager = techStackPersistenceManager;
    }

    @Override
    public Long execute(CreateTechStackCommand command) {
        techStackValidator.validateNameNotDuplicate(TechStackName.of(command.name()));

        TechStack techStack = techStackCommandFactory.create(command);
        return techStackPersistenceManager.persist(techStack);
    }
}
