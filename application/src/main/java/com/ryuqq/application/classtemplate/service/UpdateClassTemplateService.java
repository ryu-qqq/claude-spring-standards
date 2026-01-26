package com.ryuqq.application.classtemplate.service;

import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.classtemplate.factory.command.ClassTemplateCommandFactory;
import com.ryuqq.application.classtemplate.manager.ClassTemplatePersistenceManager;
import com.ryuqq.application.classtemplate.port.in.UpdateClassTemplateUseCase;
import com.ryuqq.application.classtemplate.validator.ClassTemplateValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplateUpdateData;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import org.springframework.stereotype.Service;

/**
 * UpdateClassTemplateService - 클래스 템플릿 수정 서비스
 *
 * <p>UpdateClassTemplateUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → UpdateContext.changedAt() 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateClassTemplateService implements UpdateClassTemplateUseCase {

    private final ClassTemplateValidator classTemplateValidator;
    private final ClassTemplateCommandFactory classTemplateCommandFactory;
    private final ClassTemplatePersistenceManager classTemplatePersistenceManager;

    public UpdateClassTemplateService(
            ClassTemplateValidator classTemplateValidator,
            ClassTemplateCommandFactory classTemplateCommandFactory,
            ClassTemplatePersistenceManager classTemplatePersistenceManager) {
        this.classTemplateValidator = classTemplateValidator;
        this.classTemplateCommandFactory = classTemplateCommandFactory;
        this.classTemplatePersistenceManager = classTemplatePersistenceManager;
    }

    @Override
    public void execute(UpdateClassTemplateCommand command) {
        UpdateContext<ClassTemplateId, ClassTemplateUpdateData> context =
                classTemplateCommandFactory.createUpdateContext(command);

        ClassTemplate classTemplate = classTemplateValidator.findExistingOrThrow(context.id());

        classTemplateValidator.validateNotDuplicateExcluding(
                classTemplate.structureId(), context.updateData().templateCode(), context.id());

        classTemplate.update(context.updateData(), context.changedAt());

        classTemplatePersistenceManager.persist(classTemplate);
    }
}
