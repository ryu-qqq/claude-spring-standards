package com.ryuqq.application.techstack.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.techstack.port.out.TechStackCommandPort;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TechStackPersistenceManager 단위 테스트
 *
 * <p>TechStack 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("TechStackPersistenceManager 단위 테스트")
class TechStackPersistenceManagerTest {

    @Mock private TechStackCommandPort techStackCommandPort;

    @Mock private TechStack techStack;

    private TechStackPersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new TechStackPersistenceManager(techStackCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - TechStack 영속화")
        void persist_WithTechStack_ShouldReturnId() {
            // given
            Long expectedId = 1L;
            given(techStackCommandPort.persist(techStack)).willReturn(expectedId);

            // when
            Long result = sut.persist(techStack);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(techStackCommandPort).should().persist(techStack);
        }
    }
}
