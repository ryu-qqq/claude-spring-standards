package com.ryuqq.application.classtemplate.service;

import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.factory.command.ClassTemplateCommandFactory;
import com.ryuqq.application.classtemplate.manager.ClassTemplatePersistenceManager;
import com.ryuqq.application.classtemplate.port.in.CreateClassTemplateUseCase;
import com.ryuqq.application.classtemplate.validator.ClassTemplateValidator;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Service;

/**
 * CreateClassTemplateService - 클래스 템플릿 생성 서비스
 *
 * <p>CreateClassTemplateUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * @author ryu-qqq
 */
@Service
public class CreateClassTemplateService implements CreateClassTemplateUseCase {

    private final ClassTemplateValidator classTemplateValidator;
    private final ClassTemplateCommandFactory classTemplateCommandFactory;
    private final ClassTemplatePersistenceManager classTemplatePersistenceManager;

    public CreateClassTemplateService(
            ClassTemplateValidator classTemplateValidator,
            ClassTemplateCommandFactory classTemplateCommandFactory,
            ClassTemplatePersistenceManager classTemplatePersistenceManager) {
        this.classTemplateValidator = classTemplateValidator;
        this.classTemplateCommandFactory = classTemplateCommandFactory;
        this.classTemplatePersistenceManager = classTemplatePersistenceManager;
    }

    @Override
    public Long execute(CreateClassTemplateCommand command) {
        PackageStructureId structureId = PackageStructureId.of(command.structureId());
        TemplateCode templateCode = TemplateCode.of(command.templateCode());

        classTemplateValidator.validateNotDuplicate(structureId, templateCode);

        ClassTemplate classTemplate = classTemplateCommandFactory.create(command);
        ClassTemplateId savedId = classTemplatePersistenceManager.persist(classTemplate);

        return savedId.value();
    }
}
