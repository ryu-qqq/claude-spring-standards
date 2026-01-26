package com.ryuqq.application.classtype.port.out;

import com.ryuqq.domain.classtype.aggregate.ClassType;

/**
 * ClassTypeCommandPort - ClassType 명령 Port
 *
 * <p>영속성 계층으로의 ClassType CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ClassTypeCommandPort {

    /**
     * ClassType 영속화 (생성/수정/삭제)
     *
     * @param classType 영속화할 ClassType
     * @return 영속화된 ClassType ID
     */
    Long persist(ClassType classType);
}
