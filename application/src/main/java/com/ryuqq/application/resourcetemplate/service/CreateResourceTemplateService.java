package com.ryuqq.application.resourcetemplate.service;

import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.factory.command.ResourceTemplateCommandFactory;
import com.ryuqq.application.resourcetemplate.manager.ResourceTemplatePersistenceManager;
import com.ryuqq.application.resourcetemplate.port.in.CreateResourceTemplateUseCase;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.springframework.stereotype.Service;

/**
 * CreateResourceTemplateService - 리소스 템플릿 생성 서비스
 *
 * <p>리소스 템플릿 생성 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class CreateResourceTemplateService implements CreateResourceTemplateUseCase {

    private final ResourceTemplateCommandFactory resourceTemplateCommandFactory;
    private final ResourceTemplatePersistenceManager resourceTemplatePersistenceManager;

    public CreateResourceTemplateService(
            ResourceTemplateCommandFactory resourceTemplateCommandFactory,
            ResourceTemplatePersistenceManager resourceTemplatePersistenceManager) {
        this.resourceTemplateCommandFactory = resourceTemplateCommandFactory;
        this.resourceTemplatePersistenceManager = resourceTemplatePersistenceManager;
    }

    @Override
    public Long execute(CreateResourceTemplateCommand command) {
        ResourceTemplate resourceTemplate = resourceTemplateCommandFactory.create(command);
        ResourceTemplateId savedId = resourceTemplatePersistenceManager.persist(resourceTemplate);

        return savedId.value();
    }
}
