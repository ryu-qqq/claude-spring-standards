package com.ryuqq.application.convention.port.out;

import com.ryuqq.domain.convention.aggregate.Convention;

/**
 * ConventionCommandPort - Convention 명령 Port
 *
 * <p>영속성 계층으로의 Convention CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ConventionCommandPort {

    /**
     * Convention 영속화 (생성/수정/삭제)
     *
     * @param convention 영속화할 Convention
     * @return 영속화된 Convention ID
     */
    Long persist(Convention convention);
}
