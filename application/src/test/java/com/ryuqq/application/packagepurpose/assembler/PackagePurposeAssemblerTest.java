package com.ryuqq.application.packagepurpose.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeResult;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.fixture.PackagePurposeFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PackagePurposeAssembler 단위 테스트
 *
 * <p>PackagePurpose 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("PackagePurposeAssembler 단위 테스트")
class PackagePurposeAssemblerTest {

    private PackagePurposeAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new PackagePurposeAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - PackagePurpose를 PackagePurposeResult로 변환")
        void toResult_WithValidPackagePurpose_ShouldReturnResult() {
            // given
            PackagePurpose packagePurpose = PackagePurposeFixture.reconstitute();

            // when
            PackagePurposeResult result = sut.toResult(packagePurpose);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - PackagePurpose 목록을 PackagePurposeResult 목록으로 변환")
        void toResults_WithValidPackagePurposes_ShouldReturnResults() {
            // given
            PackagePurpose purpose1 = PackagePurposeFixture.reconstitute();
            PackagePurpose purpose2 = PackagePurposeFixture.reconstitute();
            List<PackagePurpose> purposes = List.of(purpose1, purpose2);

            // when
            List<PackagePurposeResult> results = sut.toResults(purposes);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<PackagePurpose> purposes = List.of();

            // when
            List<PackagePurposeResult> results = sut.toResults(purposes);

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
            PackagePurpose purpose1 = PackagePurposeFixture.reconstitute();
            PackagePurpose purpose2 = PackagePurposeFixture.reconstitute();
            PackagePurpose purpose3 = PackagePurposeFixture.reconstitute();
            List<PackagePurpose> purposes = List.of(purpose1, purpose2, purpose3);
            int requestedSize = 2;

            // when
            PackagePurposeSliceResult result = sut.toSliceResult(purposes, requestedSize);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.packagePurposes()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            PackagePurpose purpose1 = PackagePurposeFixture.reconstitute();
            List<PackagePurpose> purposes = List.of(purpose1);
            int requestedSize = 10;

            // when
            PackagePurposeSliceResult result = sut.toSliceResult(purposes, requestedSize);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.packagePurposes()).hasSize(1);
        }
    }
}
