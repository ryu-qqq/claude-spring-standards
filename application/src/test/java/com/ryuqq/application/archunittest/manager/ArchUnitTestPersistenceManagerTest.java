package com.ryuqq.application.archunittest.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.archunittest.port.out.ArchUnitTestCommandPort;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ArchUnitTestPersistenceManager 단위 테스트
 *
 * <p>ArchUnitTest 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ArchUnitTestPersistenceManager 단위 테스트")
class ArchUnitTestPersistenceManagerTest {

    @Mock private ArchUnitTestCommandPort archUnitTestCommandPort;

    @Mock private ArchUnitTest archUnitTest;

    private ArchUnitTestPersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ArchUnitTestPersistenceManager(archUnitTestCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - ArchUnitTest 영속화")
        void persist_WithArchUnitTest_ShouldReturnId() {
            // given
            ArchUnitTestId expectedId = ArchUnitTestId.of(1L);
            given(archUnitTestCommandPort.persist(archUnitTest)).willReturn(expectedId);

            // when
            ArchUnitTestId result = sut.persist(archUnitTest);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(archUnitTestCommandPort).should().persist(archUnitTest);
        }
    }
}
