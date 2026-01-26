package com.ryuqq.application.resourcetemplate.port.out;

import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;

/**
 * ResourceTemplateCommandPort - 리소스 템플릿 명령 Port
 *
 * <p>영속성 계층으로의 ResourceTemplate CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ResourceTemplateCommandPort {

    /**
     * ResourceTemplate 영속화 (생성/수정/삭제)
     *
     * @param resourceTemplate 영속화할 ResourceTemplate
     * @return 영속화된 ResourceTemplate ID
     */
    ResourceTemplateId persist(ResourceTemplate resourceTemplate);
}
