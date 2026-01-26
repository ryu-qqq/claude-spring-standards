package com.ryuqq.application.classtemplate.port.out;

import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;

/**
 * ClassTemplateCommandPort - 클래스 템플릿 명령 Port
 *
 * <p>영속성 계층으로의 ClassTemplate CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ClassTemplateCommandPort {

    /**
     * ClassTemplate 영속화 (생성/수정/삭제)
     *
     * @param classTemplate 영속화할 ClassTemplate
     * @return 영속화된 ClassTemplate ID
     */
    ClassTemplateId persist(ClassTemplate classTemplate);
}
