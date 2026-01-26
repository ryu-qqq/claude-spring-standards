package com.ryuqq.application.resourcetemplate.service;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.factory.command.ResourceTemplateCommandFactory;
import com.ryuqq.application.resourcetemplate.manager.ResourceTemplatePersistenceManager;
import com.ryuqq.application.resourcetemplate.port.in.UpdateResourceTemplateUseCase;
import com.ryuqq.application.resourcetemplate.validator.ResourceTemplateValidator;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplateUpdateData;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.springframework.stereotype.Service;

/**
 * UpdateResourceTemplateService - 리소스 템플릿 수정 서비스
 *
 * <p>리소스 템플릿 수정 유스케이스를 구현합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
 *
 * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateResourceTemplateService implements UpdateResourceTemplateUseCase {

    private final ResourceTemplateCommandFactory resourceTemplateCommandFactory;
    private final ResourceTemplateValidator resourceTemplateValidator;
    private final ResourceTemplatePersistenceManager resourceTemplatePersistenceManager;

    public UpdateResourceTemplateService(
            ResourceTemplateCommandFactory resourceTemplateCommandFactory,
            ResourceTemplateValidator resourceTemplateValidator,
            ResourceTemplatePersistenceManager resourceTemplatePersistenceManager) {
        this.resourceTemplateCommandFactory = resourceTemplateCommandFactory;
        this.resourceTemplateValidator = resourceTemplateValidator;
        this.resourceTemplatePersistenceManager = resourceTemplatePersistenceManager;
    }

    @Override
    public void execute(UpdateResourceTemplateCommand command) {
        UpdateContext<ResourceTemplateId, ResourceTemplateUpdateData> context =
                resourceTemplateCommandFactory.createUpdateContext(command);

        ResourceTemplate resourceTemplate =
                resourceTemplateValidator.findExistingOrThrow(context.id());

        resourceTemplate.update(context.updateData(), context.changedAt());

        resourceTemplatePersistenceManager.persist(resourceTemplate);
    }
}
