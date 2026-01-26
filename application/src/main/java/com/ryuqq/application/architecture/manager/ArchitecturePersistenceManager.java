package com.ryuqq.application.architecture.manager;

import com.ryuqq.application.architecture.port.out.ArchitectureCommandPort;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ArchitecturePersistenceManager - Architecture 영속성 관리자
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
public class ArchitecturePersistenceManager {

    private final ArchitectureCommandPort architectureCommandPort;

    public ArchitecturePersistenceManager(ArchitectureCommandPort architectureCommandPort) {
        this.architectureCommandPort = architectureCommandPort;
    }

    /**
     * Architecture 영속화
     *
     * @param architecture 영속화할 Architecture
     * @return 영속화된 Architecture ID
     */
    @Transactional
    public Long persist(Architecture architecture) {
        return architectureCommandPort.persist(architecture);
    }
}
