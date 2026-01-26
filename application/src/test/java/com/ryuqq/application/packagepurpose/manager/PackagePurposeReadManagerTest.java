package com.ryuqq.application.packagepurpose.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.packagepurpose.port.out.PackagePurposeQueryPort;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeNotFoundException;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
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
 * PackagePurposeReadManager 단위 테스트
 *
 * <p>PackagePurpose 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("PackagePurposeReadManager 단위 테스트")
class PackagePurposeReadManagerTest {

    @Mock private PackagePurposeQueryPort packagePurposeQueryPort;

    @Mock private PackagePurpose packagePurpose;

    @Mock private PackagePurposeSliceCriteria criteria;

    private PackagePurposeReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new PackagePurposeReadManager(packagePurposeQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 PackagePurpose 조회")
        void getById_WithValidId_ShouldReturnPackagePurpose() {
            // given
            PackagePurposeId id = PackagePurposeId.of(1L);
            given(packagePurposeQueryPort.findById(id)).willReturn(Optional.of(packagePurpose));

            // when
            PackagePurpose result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(packagePurpose);
            then(packagePurposeQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            PackagePurposeId id = PackagePurposeId.of(999L);
            given(packagePurposeQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(PackagePurposeNotFoundException.class);
            then(packagePurposeQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<PackagePurpose> purposes = List.of(packagePurpose);
            given(packagePurposeQueryPort.findBySliceCriteria(criteria)).willReturn(purposes);

            // when
            List<PackagePurpose> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(packagePurpose);
            then(packagePurposeQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByStructureIdAndCode 메서드")
    class ExistsByStructureIdAndCode {

        @Test
        @DisplayName("성공 - 패키지 구조 ID와 코드로 존재 확인")
        void existsByStructureIdAndCode_WhenExists_ShouldReturnTrue() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            PurposeCode code = PurposeCode.of("AGGREGATE");
            given(packagePurposeQueryPort.existsByStructureIdAndCode(structureId, code))
                    .willReturn(true);

            // when
            boolean result = sut.existsByStructureIdAndCode(structureId, code);

            // then
            assertThat(result).isTrue();
            then(packagePurposeQueryPort).should().existsByStructureIdAndCode(structureId, code);
        }
    }

    @Nested
    @DisplayName("existsByStructureIdAndCodeExcluding 메서드")
    class ExistsByStructureIdAndCodeExcluding {

        @Test
        @DisplayName("성공 - 특정 ID 제외하고 존재 확인")
        void existsByStructureIdAndCodeExcluding_WhenExists_ShouldReturnTrue() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            PurposeCode code = PurposeCode.of("AGGREGATE");
            PackagePurposeId excludeId = PackagePurposeId.of(1L);
            given(
                            packagePurposeQueryPort.existsByStructureIdAndCodeExcluding(
                                    structureId, code, excludeId))
                    .willReturn(true);

            // when
            boolean result = sut.existsByStructureIdAndCodeExcluding(structureId, code, excludeId);

            // then
            assertThat(result).isTrue();
            then(packagePurposeQueryPort)
                    .should()
                    .existsByStructureIdAndCodeExcluding(structureId, code, excludeId);
        }
    }
}
