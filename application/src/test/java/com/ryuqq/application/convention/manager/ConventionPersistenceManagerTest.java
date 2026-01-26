package com.ryuqq.application.convention.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.convention.port.out.ConventionCommandPort;
import com.ryuqq.domain.convention.aggregate.Convention;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ConventionPersistenceManager 단위 테스트
 *
 * <p>Convention 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ConventionPersistenceManager 단위 테스트")
class ConventionPersistenceManagerTest {

    @Mock private ConventionCommandPort conventionCommandPort;

    @Mock private Convention convention;

    private ConventionPersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new ConventionPersistenceManager(conventionCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - Convention 영속화")
        void persist_WithConvention_ShouldReturnId() {
            // given
            Long expectedId = 1L;
            given(conventionCommandPort.persist(convention)).willReturn(expectedId);

            // when
            Long result = sut.persist(convention);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(conventionCommandPort).should().persist(convention);
        }
    }
}
