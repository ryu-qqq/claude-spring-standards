package com.ryuqq.application.packagestructure.manager;

import com.ryuqq.application.packagestructure.port.out.PackageStructureCommandPort;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageStructurePersistenceManager - 패키지 구조 영속화 관리자
 *
 * <p>패키지 구조 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class PackageStructurePersistenceManager {

    private final PackageStructureCommandPort packageStructureCommandPort;

    public PackageStructurePersistenceManager(
            PackageStructureCommandPort packageStructureCommandPort) {
        this.packageStructureCommandPort = packageStructureCommandPort;
    }

    /**
     * 패키지 구조 영속화 (생성 또는 수정)
     *
     * @param packageStructure 영속화할 패키지 구조
     * @return 영속화된 패키지 구조 ID
     */
    @Transactional
    public PackageStructureId persist(PackageStructure packageStructure) {
        return packageStructureCommandPort.persist(packageStructure);
    }
}
