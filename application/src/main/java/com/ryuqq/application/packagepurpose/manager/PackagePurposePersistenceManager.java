package com.ryuqq.application.packagepurpose.manager;

import com.ryuqq.application.packagepurpose.port.out.PackagePurposeCommandPort;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackagePurposePersistenceManager - 패키지 목적 영속화 관리자
 *
 * <p>패키지 목적 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposePersistenceManager {

    private final PackagePurposeCommandPort packagePurposeCommandPort;

    public PackagePurposePersistenceManager(PackagePurposeCommandPort packagePurposeCommandPort) {
        this.packagePurposeCommandPort = packagePurposeCommandPort;
    }

    /**
     * 패키지 목적 영속화 (생성 또는 수정)
     *
     * @param packagePurpose 영속화할 패키지 목적
     * @return 영속화된 패키지 목적 ID
     */
    @Transactional
    public PackagePurposeId persist(PackagePurpose packagePurpose) {
        return packagePurposeCommandPort.persist(packagePurpose);
    }
}
