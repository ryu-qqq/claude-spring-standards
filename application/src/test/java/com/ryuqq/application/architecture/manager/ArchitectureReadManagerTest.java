package com.ryuqq.application.architecture.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.architecture.port.out.ArchitectureQueryPort;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
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
 * ArchitectureReadManager 단위 테스트
 *
 * <p>Architecture 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ArchitectureReadManager 단위 테스트")
class ArchitectureReadManagerTest {

    @Mock private ArchitectureQueryPort architectureQueryPort;

    @Mock private Architecture architecture;

    @Mock private ArchitectureSliceCriteria criteria;

    private ArchitectureReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ArchitectureReadManager(architectureQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 Architecture 조회")
        void findById_WithValidId_ShouldReturnArchitecture() {
            // given
            ArchitectureId id = ArchitectureId.of(1L);
            given(architectureQueryPort.findById(id)).willReturn(Optional.of(architecture));

            // when
            Optional<Architecture> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(architecture);
            then(architectureQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            ArchitectureId id = ArchitectureId.of(999L);
            given(architectureQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Architecture> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(architectureQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID 확인")
        void existsById_WhenExists_ShouldReturnTrue() {
            // given
            ArchitectureId id = ArchitectureId.of(1L);
            given(architectureQueryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(architectureQueryPort).should().existsById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<Architecture> architectures = List.of(architecture);
            given(architectureQueryPort.findBySliceCriteria(criteria)).willReturn(architectures);

            // when
            List<Architecture> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(architecture);
            then(architectureQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByName 메서드")
    class ExistsByName {

        @Test
        @DisplayName("성공 - 존재하는 이름 확인")
        void existsByName_WhenExists_ShouldReturnTrue() {
            // given
            ArchitectureName name = ArchitectureName.of("Hexagonal");
            given(architectureQueryPort.existsByName(name)).willReturn(true);

            // when
            boolean result = sut.existsByName(name);

            // then
            assertThat(result).isTrue();
            then(architectureQueryPort).should().existsByName(name);
        }
    }

    @Nested
    @DisplayName("existsByNameAndIdNot 메서드")
    class ExistsByNameAndIdNot {

        @Test
        @DisplayName("성공 - 특정 ID 제외하고 이름 중복 확인")
        void existsByNameAndIdNot_WhenExists_ShouldReturnTrue() {
            // given
            ArchitectureName name = ArchitectureName.of("Hexagonal");
            ArchitectureId excludeId = ArchitectureId.of(1L);
            given(architectureQueryPort.existsByNameAndIdNot(name, excludeId)).willReturn(true);

            // when
            boolean result = sut.existsByNameAndIdNot(name, excludeId);

            // then
            assertThat(result).isTrue();
            then(architectureQueryPort).should().existsByNameAndIdNot(name, excludeId);
        }
    }

    @Nested
    @DisplayName("existsByTechStackId 메서드")
    class ExistsByTechStackId {

        @Test
        @DisplayName("성공 - TechStack에 속한 Architecture 존재 확인")
        void existsByTechStackId_WhenExists_ShouldReturnTrue() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            given(architectureQueryPort.existsByTechStackId(techStackId)).willReturn(true);

            // when
            boolean result = sut.existsByTechStackId(techStackId);

            // then
            assertThat(result).isTrue();
            then(architectureQueryPort).should().existsByTechStackId(techStackId);
        }
    }
}
