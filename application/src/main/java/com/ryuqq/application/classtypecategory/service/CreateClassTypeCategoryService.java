package com.ryuqq.application.classtypecategory.service;

import com.ryuqq.application.classtypecategory.dto.command.CreateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.factory.command.ClassTypeCategoryCommandFactory;
import com.ryuqq.application.classtypecategory.manager.ClassTypeCategoryPersistenceManager;
import com.ryuqq.application.classtypecategory.port.in.CreateClassTypeCategoryUseCase;
import com.ryuqq.application.classtypecategory.validator.ClassTypeCategoryValidator;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import org.springframework.stereotype.Service;

/**
 * CreateClassTypeCategoryService - ClassTypeCategory 생성 서비스
 *
 * <p>CreateClassTypeCategoryUseCase를 구현합니다.
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
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class CreateClassTypeCategoryService implements CreateClassTypeCategoryUseCase {

    private final ClassTypeCategoryValidator classTypeCategoryValidator;
    private final ClassTypeCategoryCommandFactory classTypeCategoryCommandFactory;
    private final ClassTypeCategoryPersistenceManager classTypeCategoryPersistenceManager;

    public CreateClassTypeCategoryService(
            ClassTypeCategoryValidator classTypeCategoryValidator,
            ClassTypeCategoryCommandFactory classTypeCategoryCommandFactory,
            ClassTypeCategoryPersistenceManager classTypeCategoryPersistenceManager) {
        this.classTypeCategoryValidator = classTypeCategoryValidator;
        this.classTypeCategoryCommandFactory = classTypeCategoryCommandFactory;
        this.classTypeCategoryPersistenceManager = classTypeCategoryPersistenceManager;
    }

    @Override
    public Long execute(CreateClassTypeCategoryCommand command) {
        classTypeCategoryValidator.validateCodeNotDuplicated(
                ArchitectureId.of(command.architectureId()), CategoryCode.of(command.code()));

        ClassTypeCategory classTypeCategory = classTypeCategoryCommandFactory.create(command);
        return classTypeCategoryPersistenceManager.persist(classTypeCategory);
    }
}
