package com.ryuqq.application.architecture.service;

import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import com.ryuqq.application.architecture.factory.command.ArchitectureCommandFactory;
import com.ryuqq.application.architecture.manager.ArchitecturePersistenceManager;
import com.ryuqq.application.architecture.port.in.UpdateArchitectureUseCase;
import com.ryuqq.application.architecture.validator.ArchitectureValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.aggregate.ArchitectureUpdateData;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import org.springframework.stereotype.Service;

/**
 * UpdateArchitectureService - Architecture 수정 서비스
 *
 * <p>UpdateArchitectureUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드로 Domain 객체를 조회합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateArchitectureService implements UpdateArchitectureUseCase {

    private final ArchitectureValidator architectureValidator;
    private final ArchitectureCommandFactory architectureCommandFactory;
    private final ArchitecturePersistenceManager architecturePersistenceManager;

    public UpdateArchitectureService(
            ArchitectureValidator architectureValidator,
            ArchitectureCommandFactory architectureCommandFactory,
            ArchitecturePersistenceManager architecturePersistenceManager) {
        this.architectureValidator = architectureValidator;
        this.architectureCommandFactory = architectureCommandFactory;
        this.architecturePersistenceManager = architecturePersistenceManager;
    }

    @Override
    public void execute(UpdateArchitectureCommand command) {
        UpdateContext<ArchitectureId, ArchitectureUpdateData> context =
                architectureCommandFactory.createUpdateContext(command);

        architectureValidator.validateNameNotDuplicateExcluding(
                ArchitectureName.of(command.name()), context.id());

        Architecture architecture = architectureValidator.findExistingOrThrow(context.id());
        architecture.update(context.updateData(), context.changedAt());
        architecturePersistenceManager.persist(architecture);
    }
}
