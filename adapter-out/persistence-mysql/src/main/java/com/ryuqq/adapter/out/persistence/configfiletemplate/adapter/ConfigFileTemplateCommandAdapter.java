package com.ryuqq.adapter.out.persistence.configfiletemplate.adapter;

import com.ryuqq.adapter.out.persistence.configfiletemplate.entity.ConfigFileTemplateJpaEntity;
import com.ryuqq.adapter.out.persistence.configfiletemplate.mapper.ConfigFileTemplateEntityMapper;
import com.ryuqq.adapter.out.persistence.configfiletemplate.repository.ConfigFileTemplateJpaRepository;
import com.ryuqq.application.configfiletemplate.port.out.ConfigFileTemplateCommandPort;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateCommandAdapter - ConfigFileTemplate 명령 어댑터
 *
 * <p>ConfigFileTemplateCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateCommandAdapter implements ConfigFileTemplateCommandPort {

    private final ConfigFileTemplateJpaRepository repository;
    private final ConfigFileTemplateEntityMapper mapper;

    public ConfigFileTemplateCommandAdapter(
            ConfigFileTemplateJpaRepository repository, ConfigFileTemplateEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ConfigFileTemplate 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param configFileTemplate 영속화할 ConfigFileTemplate
     * @return 영속화된 ConfigFileTemplate ID
     */
    @Override
    public Long persist(ConfigFileTemplate configFileTemplate) {
        ConfigFileTemplateJpaEntity entity = mapper.toEntity(configFileTemplate);
        ConfigFileTemplateJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
