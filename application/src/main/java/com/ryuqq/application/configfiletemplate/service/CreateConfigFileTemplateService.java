package com.ryuqq.application.configfiletemplate.service;

import com.ryuqq.application.configfiletemplate.dto.command.CreateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.factory.command.ConfigFileTemplateCommandFactory;
import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplatePersistenceManager;
import com.ryuqq.application.configfiletemplate.port.in.CreateConfigFileTemplateUseCase;
import com.ryuqq.application.configfiletemplate.validator.ConfigFileTemplateValidator;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.techstack.id.TechStackId;
import org.springframework.stereotype.Service;

/**
 * CreateConfigFileTemplateService - ConfigFileTemplate 생성 서비스
 *
 * <p>CreateConfigFileTemplateUseCase를 구현합니다.
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
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class CreateConfigFileTemplateService implements CreateConfigFileTemplateUseCase {

    private final ConfigFileTemplateValidator configFileTemplateValidator;
    private final ConfigFileTemplateCommandFactory configFileTemplateCommandFactory;
    private final ConfigFileTemplatePersistenceManager configFileTemplatePersistenceManager;

    public CreateConfigFileTemplateService(
            ConfigFileTemplateValidator configFileTemplateValidator,
            ConfigFileTemplateCommandFactory configFileTemplateCommandFactory,
            ConfigFileTemplatePersistenceManager configFileTemplatePersistenceManager) {
        this.configFileTemplateValidator = configFileTemplateValidator;
        this.configFileTemplateCommandFactory = configFileTemplateCommandFactory;
        this.configFileTemplatePersistenceManager = configFileTemplatePersistenceManager;
    }

    @Override
    public Long execute(CreateConfigFileTemplateCommand command) {
        configFileTemplateValidator.validateTechStackExists(TechStackId.of(command.techStackId()));

        ConfigFileTemplate configFileTemplate = configFileTemplateCommandFactory.create(command);
        return configFileTemplatePersistenceManager.persist(configFileTemplate);
    }
}
