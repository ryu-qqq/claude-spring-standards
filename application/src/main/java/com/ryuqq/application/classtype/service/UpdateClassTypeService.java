package com.ryuqq.application.classtype.service;

import com.ryuqq.application.classtype.dto.command.UpdateClassTypeCommand;
import com.ryuqq.application.classtype.factory.command.ClassTypeCommandFactory;
import com.ryuqq.application.classtype.manager.ClassTypePersistenceManager;
import com.ryuqq.application.classtype.port.in.UpdateClassTypeUseCase;
import com.ryuqq.application.classtype.validator.ClassTypeValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.aggregate.ClassTypeUpdateData;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import org.springframework.stereotype.Service;

/**
 * UpdateClassTypeService - ClassType 수정 서비스
 *
 * <p>UpdateClassTypeUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class UpdateClassTypeService implements UpdateClassTypeUseCase {

    private final ClassTypeValidator classTypeValidator;
    private final ClassTypeCommandFactory classTypeCommandFactory;
    private final ClassTypePersistenceManager classTypePersistenceManager;

    public UpdateClassTypeService(
            ClassTypeValidator classTypeValidator,
            ClassTypeCommandFactory classTypeCommandFactory,
            ClassTypePersistenceManager classTypePersistenceManager) {
        this.classTypeValidator = classTypeValidator;
        this.classTypeCommandFactory = classTypeCommandFactory;
        this.classTypePersistenceManager = classTypePersistenceManager;
    }

    @Override
    public void execute(UpdateClassTypeCommand command) {
        UpdateContext<ClassTypeId, ClassTypeUpdateData> context =
                classTypeCommandFactory.createUpdateContext(command);

        ClassType classType = classTypeValidator.findExistingOrThrow(context.id());

        classTypeValidator.validateCodeNotDuplicatedExcluding(
                classType.categoryId(), ClassTypeCode.of(command.code()), context.id());

        classType.update(context.updateData(), context.changedAt());
        classTypePersistenceManager.persist(classType);
    }
}
