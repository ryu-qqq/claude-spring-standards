package com.ryuqq.application.configfiletemplate.port.out;

import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;

/**
 * ConfigFileTemplateCommandPort - ConfigFileTemplate 명령 Port
 *
 * <p>영속성 계층으로의 ConfigFileTemplate CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ConfigFileTemplateCommandPort {

    /**
     * ConfigFileTemplate 영속화 (생성/수정/삭제)
     *
     * @param configFileTemplate 영속화할 ConfigFileTemplate
     * @return 영속화된 ConfigFileTemplate ID
     */
    Long persist(ConfigFileTemplate configFileTemplate);
}
