package com.ryuqq.application.module.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.module.dto.response.ModuleResult;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.fixture.ModuleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ModuleAssembler 단위 테스트
 *
 * <p>Module 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ModuleAssembler 단위 테스트")
class ModuleAssemblerTest {

    private ModuleAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ModuleAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - Module을 ModuleResult로 변환")
        void toResult_WithValidModule_ShouldReturnResult() {
            // given
            Module module = ModuleFixture.defaultExistingModule();

            // when
            ModuleResult result = sut.toResult(module);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleId()).isEqualTo(module.idValue());
            assertThat(result.layerId()).isEqualTo(module.layerIdValue());
            assertThat(result.name()).isEqualTo(module.nameValue());
            assertThat(result.description()).isEqualTo(module.descriptionValue());
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - Module 목록을 ModuleResult 목록으로 변환")
        void toResults_WithValidModules_ShouldReturnResults() {
            // given
            Module module1 = ModuleFixture.defaultExistingModule();
            Module module2 = ModuleFixture.childModule();
            List<Module> modules = List.of(module1, module2);

            // when
            List<ModuleResult> results = sut.toResults(modules);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<Module> modules = List.of();

            // when
            List<ModuleResult> results = sut.toResults(modules);

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
            Module module1 = ModuleFixture.defaultExistingModule();
            Module module2 = ModuleFixture.childModule();
            Module module3 = ModuleFixture.moduleWithLayerId(3L);
            List<Module> modules = List.of(module1, module2, module3);
            int size = 2;

            // when
            ModuleSliceResult result = sut.toSliceResult(modules, size);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.modules()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            Module module1 = ModuleFixture.defaultExistingModule();
            List<Module> modules = List.of(module1);
            int size = 10;

            // when
            ModuleSliceResult result = sut.toSliceResult(modules, size);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.modules()).hasSize(1);
        }
    }
}
