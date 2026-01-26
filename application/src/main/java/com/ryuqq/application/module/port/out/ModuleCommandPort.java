package com.ryuqq.application.module.port.out;

import com.ryuqq.domain.module.aggregate.Module;

/**
 * ModuleCommandPort - Module 명령 Port
 *
 * <p>영속성 계층으로의 Module CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ModuleCommandPort {

    /**
     * Module 영속화 (생성/수정/삭제)
     *
     * @param module 영속화할 Module
     * @return 영속화된 Module ID
     */
    Long persist(Module module);
}
