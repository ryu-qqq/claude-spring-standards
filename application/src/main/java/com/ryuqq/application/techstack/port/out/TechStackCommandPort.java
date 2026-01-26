package com.ryuqq.application.techstack.port.out;

import com.ryuqq.domain.techstack.aggregate.TechStack;

/**
 * TechStackCommandPort - TechStack 명령 Port
 *
 * <p>영속성 계층으로의 TechStack CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface TechStackCommandPort {

    /**
     * TechStack 영속화 (생성/수정/삭제)
     *
     * @param techStack 영속화할 TechStack
     * @return 영속화된 TechStack ID
     */
    Long persist(TechStack techStack);
}
