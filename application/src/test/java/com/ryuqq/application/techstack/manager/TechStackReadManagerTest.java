package com.ryuqq.application.techstack.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.techstack.port.out.TechStackQueryPort;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import com.ryuqq.domain.techstack.vo.TechStackName;
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
 * TechStackReadManager 단위 테스트
 *
 * <p>TechStack 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("TechStackReadManager 단위 테스트")
class TechStackReadManagerTest {

    @Mock private TechStackQueryPort techStackQueryPort;

    @Mock private TechStack techStack;

    @Mock private TechStackSliceCriteria criteria;

    private TechStackReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new TechStackReadManager(techStackQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 TechStack 조회")
        void findById_WithValidId_ShouldReturnTechStack() {
            // given
            TechStackId id = TechStackId.of(1L);
            given(techStackQueryPort.findById(id)).willReturn(Optional.of(techStack));

            // when
            Optional<TechStack> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(techStack);
            then(techStackQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            TechStackId id = TechStackId.of(999L);
            given(techStackQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<TechStack> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(techStackQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID 확인")
        void existsById_WhenExists_ShouldReturnTrue() {
            // given
            TechStackId id = TechStackId.of(1L);
            given(techStackQueryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(techStackQueryPort).should().existsById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 확인")
        void existsById_WhenNotExists_ShouldReturnFalse() {
            // given
            TechStackId id = TechStackId.of(999L);
            given(techStackQueryPort.existsById(id)).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
            then(techStackQueryPort).should().existsById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<TechStack> techStacks = List.of(techStack);
            given(techStackQueryPort.findBySliceCriteria(criteria)).willReturn(techStacks);

            // when
            List<TechStack> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(techStack);
            then(techStackQueryPort).should().findBySliceCriteria(criteria);
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void findBySliceCriteria_WhenEmpty_ShouldReturnEmptyList() {
            // given
            given(techStackQueryPort.findBySliceCriteria(criteria)).willReturn(List.of());

            // when
            List<TechStack> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).isEmpty();
            then(techStackQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByName 메서드")
    class ExistsByName {

        @Test
        @DisplayName("성공 - 존재하는 이름 확인")
        void existsByName_WhenExists_ShouldReturnTrue() {
            // given
            TechStackName name = TechStackName.of("Spring Boot");
            given(techStackQueryPort.existsByName(name)).willReturn(true);

            // when
            boolean result = sut.existsByName(name);

            // then
            assertThat(result).isTrue();
            then(techStackQueryPort).should().existsByName(name);
        }
    }

    @Nested
    @DisplayName("existsByNameAndIdNot 메서드")
    class ExistsByNameAndIdNot {

        @Test
        @DisplayName("성공 - 특정 ID 제외하고 이름 중복 확인")
        void existsByNameAndIdNot_WhenExists_ShouldReturnTrue() {
            // given
            TechStackName name = TechStackName.of("Spring Boot");
            TechStackId excludeId = TechStackId.of(1L);
            given(techStackQueryPort.existsByNameAndIdNot(name, excludeId)).willReturn(true);

            // when
            boolean result = sut.existsByNameAndIdNot(name, excludeId);

            // then
            assertThat(result).isTrue();
            then(techStackQueryPort).should().existsByNameAndIdNot(name, excludeId);
        }
    }
}
