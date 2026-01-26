package com.ryuqq.application.convention.manager;

import com.ryuqq.application.convention.port.out.ConventionCommandPort;
import com.ryuqq.domain.convention.aggregate.Convention;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ConventionPersistenceManager - Convention 영속성 관리자
 *
 * <p>CommandPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ConventionPersistenceManager {

    private final ConventionCommandPort conventionCommandPort;

    public ConventionPersistenceManager(ConventionCommandPort conventionCommandPort) {
        this.conventionCommandPort = conventionCommandPort;
    }

    /**
     * Convention 영속화
     *
     * @param convention 영속화할 Convention
     * @return 영속화된 Convention ID
     */
    @Transactional
    public Long persist(Convention convention) {
        return conventionCommandPort.persist(convention);
    }
}
