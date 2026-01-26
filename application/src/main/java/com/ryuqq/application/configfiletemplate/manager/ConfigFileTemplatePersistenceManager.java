package com.ryuqq.application.configfiletemplate.manager;

import com.ryuqq.application.configfiletemplate.port.out.ConfigFileTemplateCommandPort;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ConfigFileTemplatePersistenceManager - ConfigFileTemplate 영속성 관리자
 *
 * <p>CommandPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplatePersistenceManager {

    private final ConfigFileTemplateCommandPort configFileTemplateCommandPort;

    public ConfigFileTemplatePersistenceManager(
            ConfigFileTemplateCommandPort configFileTemplateCommandPort) {
        this.configFileTemplateCommandPort = configFileTemplateCommandPort;
    }

    /**
     * ConfigFileTemplate 영속화
     *
     * @param configFileTemplate 영속화할 ConfigFileTemplate
     * @return 영속화된 ConfigFileTemplate ID
     */
    @Transactional
    public Long persist(ConfigFileTemplate configFileTemplate) {
        return configFileTemplateCommandPort.persist(configFileTemplate);
    }
}
