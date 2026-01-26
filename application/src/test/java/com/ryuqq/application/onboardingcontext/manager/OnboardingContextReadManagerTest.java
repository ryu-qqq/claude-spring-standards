package com.ryuqq.application.onboardingcontext.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.onboardingcontext.port.out.OnboardingContextQueryPort;
import com.ryuqq.domain.onboardingcontext.aggregate.OnboardingContext;
import com.ryuqq.domain.onboardingcontext.id.OnboardingContextId;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OnboardingContextReadManager 단위 테스트
 *
 * <p>OnboardingContext 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("OnboardingContextReadManager 단위 테스트")
class OnboardingContextReadManagerTest {

    @Mock private OnboardingContextQueryPort onboardingContextQueryPort;

    @Mock private OnboardingContext onboardingContext;

    @Mock private OnboardingContextSliceCriteria criteria;

    private OnboardingContextReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingContextReadManager(onboardingContextQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 OnboardingContext 조회")
        void findById_WithValidId_ShouldReturnOnboardingContext() {
            // given
            OnboardingContextId id = OnboardingContextId.of(1L);
            given(onboardingContextQueryPort.findById(id))
                    .willReturn(Optional.of(onboardingContext));

            // when
            Optional<OnboardingContext> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(onboardingContext);
            then(onboardingContextQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            OnboardingContextId id = OnboardingContextId.of(999L);
            given(onboardingContextQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<OnboardingContext> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(onboardingContextQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID 확인")
        void existsById_WhenExists_ShouldReturnTrue() {
            // given
            OnboardingContextId id = OnboardingContextId.of(1L);
            given(onboardingContextQueryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(onboardingContextQueryPort).should().existsById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<OnboardingContext> onboardingContexts = List.of(onboardingContext);
            given(onboardingContextQueryPort.findBySliceCriteria(criteria))
                    .willReturn(onboardingContexts);

            // when
            List<OnboardingContext> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(onboardingContext);
            then(onboardingContextQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByTechStackId 메서드")
    class ExistsByTechStackId {

        @Test
        @DisplayName("성공 - TechStack에 속한 OnboardingContext 존재 확인")
        void existsByTechStackId_WhenExists_ShouldReturnTrue() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            given(onboardingContextQueryPort.existsByTechStackId(techStackId)).willReturn(true);

            // when
            boolean result = sut.existsByTechStackId(techStackId);

            // then
            assertThat(result).isTrue();
            then(onboardingContextQueryPort).should().existsByTechStackId(techStackId);
        }
    }

    @Nested
    @DisplayName("findForMcp 메서드")
    class FindForMcp {

        @Test
        @DisplayName("성공 - MCP Tool용 조건 기반 조회")
        void findForMcp_WithConditions_ShouldReturnList() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            Long architectureId = 1L;
            List<ContextType> contextTypes = List.of(ContextType.SUMMARY);
            List<OnboardingContext> onboardingContexts = List.of(onboardingContext);
            given(onboardingContextQueryPort.findForMcp(techStackId, architectureId, contextTypes))
                    .willReturn(onboardingContexts);

            // when
            List<OnboardingContext> result =
                    sut.findForMcp(techStackId, architectureId, contextTypes);

            // then
            assertThat(result).hasSize(1).containsExactly(onboardingContext);
            then(onboardingContextQueryPort)
                    .should()
                    .findForMcp(techStackId, architectureId, contextTypes);
        }
    }
}
