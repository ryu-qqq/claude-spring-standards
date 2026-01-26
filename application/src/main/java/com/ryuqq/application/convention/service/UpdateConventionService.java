package com.ryuqq.application.convention.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;
import com.ryuqq.application.convention.factory.command.ConventionCommandFactory;
import com.ryuqq.application.convention.manager.ConventionPersistenceManager;
import com.ryuqq.application.convention.port.in.UpdateConventionUseCase;
import com.ryuqq.application.convention.validator.ConventionValidator;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.aggregate.ConventionUpdateData;
import com.ryuqq.domain.convention.id.ConventionId;
import org.springframework.stereotype.Service;

/**
 * UpdateConventionService - Convention 수정 서비스
 *
 * <p>UpdateConventionUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 -> Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 -> 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 -> Manager 사용.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드로 Domain 객체를 조회합니다.
 *
 * <p>APP-FAC-002: UpdateContext는 Factory에서 생성합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateConventionService implements UpdateConventionUseCase {

    private final ConventionValidator conventionValidator;
    private final ConventionCommandFactory conventionCommandFactory;
    private final ConventionPersistenceManager conventionPersistenceManager;

    public UpdateConventionService(
            ConventionValidator conventionValidator,
            ConventionCommandFactory conventionCommandFactory,
            ConventionPersistenceManager conventionPersistenceManager) {
        this.conventionValidator = conventionValidator;
        this.conventionCommandFactory = conventionCommandFactory;
        this.conventionPersistenceManager = conventionPersistenceManager;
    }

    @Override
    public void execute(UpdateConventionCommand command) {
        UpdateContext<ConventionId, ConventionUpdateData> context =
                conventionCommandFactory.createUpdateContext(command);

        conventionValidator.validateNotDuplicateExcluding(
                context.updateData().moduleId(), context.updateData().version(), context.id());

        Convention convention = conventionValidator.findExistingOrThrow(context.id());
        convention.update(context.updateData(), context.changedAt());

        conventionPersistenceManager.persist(convention);
    }
}
