package com.ryuqq.application.onboardingcontext.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.onboardingcontext.port.out.OnboardingContextCommandPort;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OnboardingContextPersistenceManager 단위 테스트
 *
 * <p>OnboardingContext 영속성 관리자의 CommandPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("OnboardingContextPersistenceManager 단위 테스트")
class OnboardingContextPersistenceManagerTest {

    @Mock private OnboardingContextCommandPort onboardingContextCommandPort;

    @Mock private OnboardingContext onboardingContext;

    private OnboardingContextPersistenceManager sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingContextPersistenceManager(onboardingContextCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공 - OnboardingContext 영속화")
        void persist_WithOnboardingContext_ShouldReturnId() {
            // given
            Long expectedId = 1L;
            given(onboardingContextCommandPort.persist(onboardingContext)).willReturn(expectedId);

            // when
            Long result = sut.persist(onboardingContext);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(onboardingContextCommandPort).should().persist(onboardingContext);
        }
    }
}
