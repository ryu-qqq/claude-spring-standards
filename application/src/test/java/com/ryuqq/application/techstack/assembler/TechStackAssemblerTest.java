package com.ryuqq.application.techstack.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.techstack.dto.response.TechStackResult;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.fixture.TechStackFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TechStackAssembler 단위 테스트
 *
 * <p>TechStack 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("TechStackAssembler 단위 테스트")
class TechStackAssemblerTest {

    private TechStackAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new TechStackAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - TechStack을 TechStackResult로 변환")
        void toResult_WithValidTechStack_ShouldReturnResult() {
            // given
            TechStack techStack = TechStackFixture.defaultExistingTechStack();

            // when
            TechStackResult result = sut.toResult(techStack);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(techStack.id().value());
            assertThat(result.name()).isEqualTo(techStack.name().value());
            assertThat(result.status()).isEqualTo(techStack.status().name());
            assertThat(result.languageType()).isEqualTo(techStack.languageType().name());
            assertThat(result.languageVersion()).isEqualTo(techStack.languageVersion().value());
        }

        @Test
        @DisplayName("성공 - 언어 기능이 포함된 TechStack 변환")
        void toResult_WithLanguageFeatures_ShouldReturnResult() {
            // given
            TechStack techStack = TechStackFixture.techStackWithLanguageFeatures();

            // when
            TechStackResult result = sut.toResult(techStack);

            // then
            assertThat(result).isNotNull();
            assertThat(result.languageFeatures()).isEqualTo(techStack.languageFeatures().values());
        }

        @Test
        @DisplayName("성공 - 프레임워크 모듈이 포함된 TechStack 변환")
        void toResult_WithFrameworkModules_ShouldReturnResult() {
            // given
            TechStack techStack = TechStackFixture.techStackWithFrameworkModules();

            // when
            TechStackResult result = sut.toResult(techStack);

            // then
            assertThat(result).isNotNull();
            assertThat(result.frameworkModules()).isEqualTo(techStack.frameworkModules().values());
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - TechStack 목록을 TechStackResult 목록으로 변환")
        void toResults_WithValidTechStacks_ShouldReturnResults() {
            // given
            TechStack techStack1 = TechStackFixture.defaultExistingTechStack();
            TechStack techStack2 = TechStackFixture.deprecatedTechStack();
            List<TechStack> techStacks = List.of(techStack1, techStack2);

            // when
            List<TechStackResult> results = sut.toResults(techStacks);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<TechStack> techStacks = List.of();

            // when
            List<TechStackResult> results = sut.toResults(techStacks);

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
            TechStack techStack1 = TechStackFixture.defaultExistingTechStack();
            TechStack techStack2 = TechStackFixture.deprecatedTechStack();
            TechStack techStack3 = TechStackFixture.archivedTechStack();
            List<TechStack> techStacks = List.of(techStack1, techStack2, techStack3);
            int size = 2;

            // when
            TechStackSliceResult result = sut.toSliceResult(techStacks, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isTrue();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            TechStack techStack1 = TechStackFixture.defaultExistingTechStack();
            List<TechStack> techStacks = List.of(techStack1);
            int size = 10;

            // when
            TechStackSliceResult result = sut.toSliceResult(techStacks, size);

            // then
            assertThat(result.sliceMeta().hasNext()).isFalse();
            assertThat(result.content()).hasSize(1);
        }
    }
}
