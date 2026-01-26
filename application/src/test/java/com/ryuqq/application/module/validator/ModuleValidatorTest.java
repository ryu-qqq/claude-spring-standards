package com.ryuqq.application.module.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.module.manager.ModuleReadManager;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.aggregate.Module;
import com.ryuqq.domain.module.exception.ModuleDuplicateNameException;
import com.ryuqq.domain.module.fixture.ModuleFixture;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.ModuleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ModuleValidator 단위 테스트
 *
 * <p>Module 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("ModuleValidator 단위 테스트")
class ModuleValidatorTest {

    @Mock private ModuleReadManager moduleReadManager;

    private ModuleValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ModuleValidator(moduleReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 Module 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnModule() {
            // given
            ModuleId id = ModuleId.of(1L);
            Module expected = ModuleFixture.defaultExistingModule();

            given(moduleReadManager.getById(id)).willReturn(expected);

            // when
            Module result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicate 메서드")
    class ValidateNotDuplicate {

        @Test
        @DisplayName("성공 - 중복되지 않는 이름")
        void validateNotDuplicate_WhenNotDuplicate_ShouldNotThrow() {
            // given
            LayerId layerId = LayerId.of(1L);
            ModuleName name = ModuleName.of("new-module");

            given(moduleReadManager.existsByLayerIdAndName(layerId, name)).willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicate(layerId, name))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우 예외")
        void validateNotDuplicate_WhenDuplicate_ShouldThrowException() {
            // given
            LayerId layerId = LayerId.of(1L);
            ModuleName name = ModuleName.of("existing-module");

            given(moduleReadManager.existsByLayerIdAndName(layerId, name)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicate(layerId, name))
                    .isInstanceOf(ModuleDuplicateNameException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicateExcluding 메서드")
    class ValidateNotDuplicateExcluding {

        @Test
        @DisplayName("성공 - 자신을 제외하고 중복되지 않는 이름")
        void validateNotDuplicateExcluding_WhenNotDuplicate_ShouldNotThrow() {
            // given
            LayerId layerId = LayerId.of(1L);
            ModuleName name = ModuleName.of("updated-module");
            ModuleId excludeModuleId = ModuleId.of(1L);

            given(moduleReadManager.existsByLayerIdAndNameExcluding(layerId, name, excludeModuleId))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicateExcluding(layerId, name, excludeModuleId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 다른 Module에서 이미 사용 중인 경우 예외")
        void validateNotDuplicateExcluding_WhenDuplicate_ShouldThrowException() {
            // given
            LayerId layerId = LayerId.of(1L);
            ModuleName name = ModuleName.of("existing-module");
            ModuleId excludeModuleId = ModuleId.of(1L);

            given(moduleReadManager.existsByLayerIdAndNameExcluding(layerId, name, excludeModuleId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () -> sut.validateNotDuplicateExcluding(layerId, name, excludeModuleId))
                    .isInstanceOf(ModuleDuplicateNameException.class);
        }
    }

    @Nested
    @DisplayName("validateDeletable 메서드")
    class ValidateDeletable {

        @Test
        @DisplayName("성공 - 자식 모듈이 없는 경우")
        void validateDeletable_WhenNoChildren_ShouldNotThrow() {
            // given
            ModuleId moduleId = ModuleId.of(1L);

            given(moduleReadManager.hasChildren(moduleId)).willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateDeletable(moduleId)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 자식 모듈이 있는 경우 예외")
        void validateDeletable_WhenHasChildren_ShouldThrowException() {
            // given
            ModuleId moduleId = ModuleId.of(1L);

            given(moduleReadManager.hasChildren(moduleId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateDeletable(moduleId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot delete module with children");
        }
    }
}
