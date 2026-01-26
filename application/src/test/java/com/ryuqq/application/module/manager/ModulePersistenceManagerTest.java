package com.ryuqq.application.module.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.module.port.out.ModuleCommandPort;
import com.ryuqq.domain.module.aggregate.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ModulePersistenceManager 단위 테스트
 *
 * <p>Module 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ModulePersistenceManager 단위 테스트")
class ModulePersistenceManagerTest {

    @Mock private ModuleCommandPort moduleCommandPort;

    @Mock private Module module;

    private ModulePersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ModulePersistenceManager(moduleCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - Module 영속화")
        void persist_WithModule_ShouldReturnId() {
            // given
            Long expectedId = 1L;
            given(moduleCommandPort.persist(module)).willReturn(expectedId);

            // when
            Long result = sut.persist(module);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(moduleCommandPort).should().persist(module);
        }
    }
}
