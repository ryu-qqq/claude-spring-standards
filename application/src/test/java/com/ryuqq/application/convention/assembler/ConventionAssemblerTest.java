package com.ryuqq.application.convention.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.convention.dto.response.ConventionResult;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.fixture.ConventionFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ConventionAssembler 단위 테스트
 *
 * <p>Convention 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ConventionAssembler 단위 테스트")
class ConventionAssemblerTest {

    private ConventionAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ConventionAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - Convention을 ConventionResult로 변환")
        void toResult_WithValidConvention_ShouldReturnResult() {
            // given
            Convention convention = ConventionFixture.defaultExistingConvention();

            // when
            ConventionResult result = sut.toResult(convention);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(convention.idValue());
            assertThat(result.moduleId()).isEqualTo(convention.moduleIdValue());
            assertThat(result.version()).isEqualTo(convention.versionValue());
            assertThat(result.description()).isEqualTo(convention.description());
            assertThat(result.active()).isEqualTo(convention.isActive());
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - Convention 목록을 ConventionResult 목록으로 변환")
        void toResults_WithValidConventions_ShouldReturnResults() {
            // given
            Convention convention1 = ConventionFixture.defaultExistingConvention();
            Convention convention2 = ConventionFixture.defaultExistingConvention();
            List<Convention> conventions = List.of(convention1, convention2);

            // when
            List<ConventionResult> results = sut.toResults(conventions);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<Convention> conventions = List.of();

            // when
            List<ConventionResult> results = sut.toResults(conventions);

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
            Convention convention1 = ConventionFixture.defaultExistingConvention();
            Convention convention2 = ConventionFixture.defaultExistingConvention();
            Convention convention3 = ConventionFixture.defaultExistingConvention();
            List<Convention> conventions = List.of(convention1, convention2, convention3);
            int size = 2;

            // when
            ConventionSliceResult result = sut.toSliceResult(conventions, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isTrue();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            Convention convention1 = ConventionFixture.defaultExistingConvention();
            List<Convention> conventions = List.of(convention1);
            int size = 10;

            // when
            ConventionSliceResult result = sut.toSliceResult(conventions, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isFalse();
            assertThat(result.content()).hasSize(1);
        }
    }
}
