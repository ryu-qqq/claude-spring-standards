package com.ryuqq.application.archunittest.manager;

import com.ryuqq.application.archunittest.port.out.ArchUnitTestCommandPort;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ArchUnitTestPersistenceManager - ArchUnit 테스트 영속화 관리자
 *
 * <p>ArchUnit 테스트 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestPersistenceManager {

    private final ArchUnitTestCommandPort archUnitTestCommandPort;

    public ArchUnitTestPersistenceManager(ArchUnitTestCommandPort archUnitTestCommandPort) {
        this.archUnitTestCommandPort = archUnitTestCommandPort;
    }

    /**
     * ArchUnit 테스트 영속화 (생성 또는 수정)
     *
     * @param archUnitTest 영속화할 ArchUnit 테스트
     * @return 영속화된 ArchUnit 테스트 ID
     */
    @Transactional
    public ArchUnitTestId persist(ArchUnitTest archUnitTest) {
        return archUnitTestCommandPort.persist(archUnitTest);
    }
}
