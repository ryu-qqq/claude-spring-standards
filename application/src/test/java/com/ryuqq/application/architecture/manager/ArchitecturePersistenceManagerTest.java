package com.ryuqq.application.architecture.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.architecture.port.out.ArchitectureCommandPort;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ArchitecturePersistenceManager 단위 테스트
 *
 * <p>Architecture 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ArchitecturePersistenceManager 단위 테스트")
class ArchitecturePersistenceManagerTest {

    @Mock private ArchitectureCommandPort architectureCommandPort;

    @Mock private Architecture architecture;

    private ArchitecturePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ArchitecturePersistenceManager(architectureCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - Architecture 영속화")
        void persist_WithArchitecture_ShouldReturnId() {
            // given
            Long expectedId = 1L;
            given(architectureCommandPort.persist(architecture)).willReturn(expectedId);

            // when
            Long result = sut.persist(architecture);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(architectureCommandPort).should().persist(architecture);
        }
    }
}
