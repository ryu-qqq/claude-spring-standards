package com.ryuqq.application.classtype.manager;

import com.ryuqq.application.classtype.port.out.ClassTypeCommandPort;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassTypePersistenceManager - ClassType 영속성 관리자
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
public class ClassTypePersistenceManager {

    private final ClassTypeCommandPort classTypeCommandPort;

    public ClassTypePersistenceManager(ClassTypeCommandPort classTypeCommandPort) {
        this.classTypeCommandPort = classTypeCommandPort;
    }

    /**
     * ClassType 영속화
     *
     * @param classType 영속화할 ClassType
     * @return 영속화된 ClassType ID
     */
    @Transactional
    public Long persist(ClassType classType) {
        return classTypeCommandPort.persist(classType);
    }
}
