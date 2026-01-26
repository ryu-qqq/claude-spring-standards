package com.ryuqq.application.packagepurpose.port.out;

import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;

/**
 * PackagePurposeCommandPort - 패키지 목적 명령 Port
 *
 * <p>영속성 계층으로의 PackagePurpose CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface PackagePurposeCommandPort {

    /**
     * PackagePurpose 영속화 (생성/수정/삭제)
     *
     * @param packagePurpose 영속화할 PackagePurpose
     * @return 영속화된 PackagePurpose ID
     */
    PackagePurposeId persist(PackagePurpose packagePurpose);
}
