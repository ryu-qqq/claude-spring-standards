package com.ryuqq.application.techstack.manager;

import com.ryuqq.application.techstack.port.out.TechStackCommandPort;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TechStackPersistenceManager - TechStack 영속성 관리자
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
public class TechStackPersistenceManager {

    private final TechStackCommandPort techStackCommandPort;

    public TechStackPersistenceManager(TechStackCommandPort techStackCommandPort) {
        this.techStackCommandPort = techStackCommandPort;
    }

    /**
     * TechStack 영속화
     *
     * @param techStack 영속화할 TechStack
     * @return 영속화된 TechStack ID
     */
    @Transactional
    public Long persist(TechStack techStack) {
        return techStackCommandPort.persist(techStack);
    }
}
