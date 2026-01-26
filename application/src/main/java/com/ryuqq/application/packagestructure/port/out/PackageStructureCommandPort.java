package com.ryuqq.application.packagestructure.port.out;

import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;

/**
 * PackageStructureCommandPort - 패키지 구조 명령 Port
 *
 * <p>영속성 계층으로의 PackageStructure CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface PackageStructureCommandPort {

    /**
     * PackageStructure 영속화 (생성/수정/삭제)
     *
     * @param packageStructure 영속화할 PackageStructure
     * @return 영속화된 PackageStructure ID
     */
    PackageStructureId persist(PackageStructure packageStructure);
}
