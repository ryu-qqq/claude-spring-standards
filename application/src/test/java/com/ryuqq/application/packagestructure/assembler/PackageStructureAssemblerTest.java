package com.ryuqq.application.packagestructure.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.packagestructure.dto.response.PackageStructureResult;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureSliceResult;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.fixture.PackageStructureFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PackageStructureAssembler 단위 테스트
 *
 * <p>PackageStructure 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("PackageStructureAssembler 단위 테스트")
class PackageStructureAssemblerTest {

    private PackageStructureAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new PackageStructureAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - PackageStructure를 PackageStructureResult로 변환")
        void toResult_WithValidPackageStructure_ShouldReturnResult() {
            // given
            PackageStructure packageStructure = PackageStructureFixture.reconstitute();

            // when
            PackageStructureResult result = sut.toResult(packageStructure);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(packageStructure.idValue());
        }

        @Test
        @DisplayName("성공 - 기존 PackageStructure 변환")
        void toResult_WithExistingPackageStructure_ShouldReturnResult() {
            // given
            PackageStructure packageStructure =
                    PackageStructureFixture.defaultExistingPackageStructure();

            // when
            PackageStructureResult result = sut.toResult(packageStructure);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - PackageStructure 목록을 PackageStructureResult 목록으로 변환")
        void toResults_WithValidPackageStructures_ShouldReturnResults() {
            // given
            PackageStructure packageStructure1 = PackageStructureFixture.reconstitute();
            PackageStructure packageStructure2 = PackageStructureFixture.reconstitute();
            List<PackageStructure> packageStructures =
                    List.of(packageStructure1, packageStructure2);

            // when
            List<PackageStructureResult> results = sut.toResults(packageStructures);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<PackageStructure> packageStructures = List.of();

            // when
            List<PackageStructureResult> results = sut.toResults(packageStructures);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenHasNext_ShouldReturnSliceWithHasNextTrue() {
            // given
            PackageStructure packageStructure1 = PackageStructureFixture.reconstitute();
            PackageStructure packageStructure2 = PackageStructureFixture.reconstitute();
            PackageStructure packageStructure3 = PackageStructureFixture.reconstitute();
            List<PackageStructure> packageStructures =
                    List.of(packageStructure1, packageStructure2, packageStructure3);
            int size = 2;

            // when
            PackageStructureSliceResult result = sut.toSliceResult(packageStructures, size);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.packageStructures()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            PackageStructure packageStructure1 = PackageStructureFixture.reconstitute();
            List<PackageStructure> packageStructures = List.of(packageStructure1);
            int size = 10;

            // when
            PackageStructureSliceResult result = sut.toSliceResult(packageStructures, size);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.packageStructures()).hasSize(1);
        }
    }
}
