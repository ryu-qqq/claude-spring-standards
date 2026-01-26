package com.ryuqq.application.module.manager;

import com.ryuqq.application.module.port.out.ModuleCommandPort;
import com.ryuqq.domain.module.aggregate.Module;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ModulePersistenceManager - Module 영속성 관리자
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
public class ModulePersistenceManager {

    private final ModuleCommandPort moduleCommandPort;

    public ModulePersistenceManager(ModuleCommandPort moduleCommandPort) {
        this.moduleCommandPort = moduleCommandPort;
    }

    /**
     * Module 영속화
     *
     * @param module 영속화할 Module
     * @return 영속화된 Module ID
     */
    @Transactional
    public Long persist(Module module) {
        return moduleCommandPort.persist(module);
    }
}
