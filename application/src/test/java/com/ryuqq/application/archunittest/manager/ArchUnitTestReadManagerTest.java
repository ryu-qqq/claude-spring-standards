package com.ryuqq.application.archunittest.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.archunittest.port.out.ArchUnitTestQueryPort;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.exception.ArchUnitTestNotFoundException;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
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
 * ArchUnitTestReadManager 단위 테스트
 *
 * <p>ArchUnitTest 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ArchUnitTestReadManager 단위 테스트")
class ArchUnitTestReadManagerTest {

    @Mock private ArchUnitTestQueryPort archUnitTestQueryPort;

    @Mock private ArchUnitTest archUnitTest;

    @Mock private ArchUnitTestSliceCriteria criteria;

    private ArchUnitTestReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ArchUnitTestReadManager(archUnitTestQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 ArchUnitTest 조회")
        void getById_WithValidId_ShouldReturnArchUnitTest() {
            // given
            ArchUnitTestId id = ArchUnitTestId.of(1L);
            given(archUnitTestQueryPort.findById(id)).willReturn(Optional.of(archUnitTest));

            // when
            ArchUnitTest result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(archUnitTest);
            then(archUnitTestQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            ArchUnitTestId id = ArchUnitTestId.of(999L);
            given(archUnitTestQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ArchUnitTestNotFoundException.class);
            then(archUnitTestQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ArchUnitTest 조회")
        void findById_WithValidId_ShouldReturnArchUnitTest() {
            // given
            ArchUnitTestId id = ArchUnitTestId.of(1L);
            given(archUnitTestQueryPort.findById(id)).willReturn(Optional.of(archUnitTest));

            // when
            ArchUnitTest result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(archUnitTest);
            then(archUnitTestQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 null 반환")
        void findById_WithNonExistentId_ShouldReturnNull() {
            // given
            ArchUnitTestId id = ArchUnitTestId.of(999L);
            given(archUnitTestQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            ArchUnitTest result = sut.findById(id);

            // then
            assertThat(result).isNull();
            then(archUnitTestQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<ArchUnitTest> tests = List.of(archUnitTest);
            given(archUnitTestQueryPort.findBySliceCriteria(criteria)).willReturn(tests);

            // when
            List<ArchUnitTest> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(archUnitTest);
            then(archUnitTestQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByStructureIdAndCode 메서드")
    class ExistsByStructureIdAndCode {

        @Test
        @DisplayName("성공 - 패키지 구조 내 테스트 코드 존재 확인")
        void existsByStructureIdAndCode_WhenExists_ShouldReturnTrue() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            String code = "ARCH-001";
            given(archUnitTestQueryPort.existsByStructureIdAndCode(structureId, code))
                    .willReturn(true);

            // when
            boolean result = sut.existsByStructureIdAndCode(structureId, code);

            // then
            assertThat(result).isTrue();
            then(archUnitTestQueryPort).should().existsByStructureIdAndCode(structureId, code);
        }
    }

    @Nested
    @DisplayName("existsByStructureIdAndCodeExcluding 메서드")
    class ExistsByStructureIdAndCodeExcluding {

        @Test
        @DisplayName("성공 - 특정 테스트 제외하고 코드 존재 확인")
        void existsByStructureIdAndCodeExcluding_WhenExists_ShouldReturnTrue() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            String code = "ARCH-001";
            ArchUnitTestId excludeId = ArchUnitTestId.of(1L);
            given(
                            archUnitTestQueryPort.existsByStructureIdAndCodeExcluding(
                                    structureId, code, excludeId))
                    .willReturn(true);

            // when
            boolean result = sut.existsByStructureIdAndCodeExcluding(structureId, code, excludeId);

            // then
            assertThat(result).isTrue();
            then(archUnitTestQueryPort)
                    .should()
                    .existsByStructureIdAndCodeExcluding(structureId, code, excludeId);
        }
    }
}
