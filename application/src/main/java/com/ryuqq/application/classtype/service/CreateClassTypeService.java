package com.ryuqq.application.classtype.service;

import com.ryuqq.application.classtype.dto.command.CreateClassTypeCommand;
import com.ryuqq.application.classtype.factory.command.ClassTypeCommandFactory;
import com.ryuqq.application.classtype.manager.ClassTypePersistenceManager;
import com.ryuqq.application.classtype.port.in.CreateClassTypeUseCase;
import com.ryuqq.application.classtype.validator.ClassTypeValidator;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import org.springframework.stereotype.Service;

/**
 * CreateClassTypeService - ClassType 생성 서비스
 *
 * <p>CreateClassTypeUseCase를 구현합니다.
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
public class CreateClassTypeService implements CreateClassTypeUseCase {

    private final ClassTypeValidator classTypeValidator;
    private final ClassTypeCommandFactory classTypeCommandFactory;
    private final ClassTypePersistenceManager classTypePersistenceManager;

    public CreateClassTypeService(
            ClassTypeValidator classTypeValidator,
            ClassTypeCommandFactory classTypeCommandFactory,
            ClassTypePersistenceManager classTypePersistenceManager) {
        this.classTypeValidator = classTypeValidator;
        this.classTypeCommandFactory = classTypeCommandFactory;
        this.classTypePersistenceManager = classTypePersistenceManager;
    }

    @Override
    public Long execute(CreateClassTypeCommand command) {
        classTypeValidator.validateCodeNotDuplicated(
                ClassTypeCategoryId.of(command.categoryId()), ClassTypeCode.of(command.code()));

        ClassType classType = classTypeCommandFactory.create(command);
        return classTypePersistenceManager.persist(classType);
    }
}
