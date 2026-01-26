package com.ryuqq.application.architecture.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.architecture.dto.response.ArchitectureResult;
import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.fixture.ArchitectureFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ArchitectureAssembler 단위 테스트
 *
 * <p>Architecture 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ArchitectureAssembler 단위 테스트")
class ArchitectureAssemblerTest {

    private ArchitectureAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ArchitectureAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - Architecture를 ArchitectureResult로 변환")
        void toResult_WithValidArchitecture_ShouldReturnResult() {
            // given
            Architecture architecture = ArchitectureFixture.defaultExistingArchitecture();

            // when
            ArchitectureResult result = sut.toResult(architecture);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(architecture.id().value());
            assertThat(result.techStackId()).isEqualTo(architecture.techStackId().value());
            assertThat(result.name()).isEqualTo(architecture.name().value());
            assertThat(result.patternType()).isEqualTo(architecture.patternType().name());
        }

        @Test
        @DisplayName("성공 - 설명이 포함된 Architecture 변환")
        void toResult_WithDescriptionArchitecture_ShouldReturnResult() {
            // given
            Architecture architecture = ArchitectureFixture.architectureWithDescription();

            // when
            ArchitectureResult result = sut.toResult(architecture);

            // then
            assertThat(result).isNotNull();
            assertThat(result.patternDescription())
                    .isEqualTo(architecture.patternDescription().value());
        }

        @Test
        @DisplayName("성공 - 원칙이 포함된 Architecture 변환")
        void toResult_WithPrinciplesArchitecture_ShouldReturnResult() {
            // given
            Architecture architecture = ArchitectureFixture.architectureWithPrinciples();

            // when
            ArchitectureResult result = sut.toResult(architecture);

            // then
            assertThat(result).isNotNull();
            assertThat(result.patternPrinciples())
                    .isEqualTo(architecture.patternPrinciples().values());
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - Architecture 목록을 ArchitectureResult 목록으로 변환")
        void toResults_WithValidArchitectures_ShouldReturnResults() {
            // given
            Architecture architecture1 = ArchitectureFixture.defaultExistingArchitecture();
            Architecture architecture2 = ArchitectureFixture.architectureWithDescription();
            List<Architecture> architectures = List.of(architecture1, architecture2);

            // when
            List<ArchitectureResult> results = sut.toResults(architectures);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<Architecture> architectures = List.of();

            // when
            List<ArchitectureResult> results = sut.toResults(architectures);

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
            Architecture architecture1 = ArchitectureFixture.defaultExistingArchitecture();
            Architecture architecture2 = ArchitectureFixture.architectureWithDescription();
            Architecture architecture3 = ArchitectureFixture.architectureWithPrinciples();
            List<Architecture> architectures = List.of(architecture1, architecture2, architecture3);
            int size = 2;

            // when
            ArchitectureSliceResult result = sut.toSliceResult(architectures, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isTrue();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            Architecture architecture1 = ArchitectureFixture.defaultExistingArchitecture();
            List<Architecture> architectures = List.of(architecture1);
            int size = 10;

            // when
            ArchitectureSliceResult result = sut.toSliceResult(architectures, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isFalse();
            assertThat(result.content()).hasSize(1);
        }
    }
}
