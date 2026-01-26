package com.ryuqq.application.architecture.port.out;

import com.ryuqq.domain.architecture.aggregate.Architecture;

/**
 * ArchitectureCommandPort - Architecture 명령 Port
 *
 * <p>영속성 계층으로의 Architecture CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ArchitectureCommandPort {

    /**
     * Architecture 영속화 (생성/수정/삭제)
     *
     * @param architecture 영속화할 Architecture
     * @return 영속화된 Architecture ID
     */
    Long persist(Architecture architecture);
}
