package com.ryuqq.application.classtypecategory.service;

import com.ryuqq.application.classtypecategory.dto.command.UpdateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.factory.command.ClassTypeCategoryCommandFactory;
import com.ryuqq.application.classtypecategory.manager.ClassTypeCategoryPersistenceManager;
import com.ryuqq.application.classtypecategory.port.in.UpdateClassTypeCategoryUseCase;
import com.ryuqq.application.classtypecategory.validator.ClassTypeCategoryValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategoryUpdateData;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import org.springframework.stereotype.Service;

/**
 * UpdateClassTypeCategoryService - ClassTypeCategory 수정 서비스
 *
 * <p>UpdateClassTypeCategoryUseCase를 구현합니다.
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
public class UpdateClassTypeCategoryService implements UpdateClassTypeCategoryUseCase {

    private final ClassTypeCategoryValidator classTypeCategoryValidator;
    private final ClassTypeCategoryCommandFactory classTypeCategoryCommandFactory;
    private final ClassTypeCategoryPersistenceManager classTypeCategoryPersistenceManager;

    public UpdateClassTypeCategoryService(
            ClassTypeCategoryValidator classTypeCategoryValidator,
            ClassTypeCategoryCommandFactory classTypeCategoryCommandFactory,
            ClassTypeCategoryPersistenceManager classTypeCategoryPersistenceManager) {
        this.classTypeCategoryValidator = classTypeCategoryValidator;
        this.classTypeCategoryCommandFactory = classTypeCategoryCommandFactory;
        this.classTypeCategoryPersistenceManager = classTypeCategoryPersistenceManager;
    }

    @Override
    public void execute(UpdateClassTypeCategoryCommand command) {
        UpdateContext<ClassTypeCategoryId, ClassTypeCategoryUpdateData> context =
                classTypeCategoryCommandFactory.createUpdateContext(command);

        ClassTypeCategory classTypeCategory =
                classTypeCategoryValidator.findExistingOrThrow(context.id());

        classTypeCategoryValidator.validateCodeNotDuplicatedExcluding(
                classTypeCategory.architectureId(), CategoryCode.of(command.code()), context.id());

        classTypeCategory.update(context.updateData(), context.changedAt());
        classTypeCategoryPersistenceManager.persist(classTypeCategory);
    }
}
