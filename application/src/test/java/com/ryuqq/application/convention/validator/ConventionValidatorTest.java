package com.ryuqq.application.convention.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.convention.manager.ConventionReadManager;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.exception.ConventionDuplicateException;
import com.ryuqq.domain.convention.exception.ConventionNotFoundException;
import com.ryuqq.domain.convention.fixture.ConventionFixture;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
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
 * ConventionValidator 단위 테스트
 *
 * <p>Convention 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("ConventionValidator 단위 테스트")
class ConventionValidatorTest {

    @Mock private ConventionReadManager conventionReadManager;

    private ConventionValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ConventionValidator(conventionReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 Convention 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnConvention() {
            // given
            ConventionId id = ConventionId.of(1L);
            Convention expected = ConventionFixture.defaultExistingConvention();

            given(conventionReadManager.findById(id)).willReturn(Optional.of(expected));

            // when
            Convention result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void findExistingOrThrow_WhenNotExists_ShouldThrowException() {
            // given
            ConventionId id = ConventionId.of(999L);

            given(conventionReadManager.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(ConventionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공 - 존재하는 Convention")
        void validateExists_WhenExists_ShouldNotThrow() {
            // given
            ConventionId id = ConventionId.of(1L);

            given(conventionReadManager.existsById(id)).willReturn(true);

            // when & then - no exception
            assertThatCode(() -> sut.validateExists(id)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void validateExists_WhenNotExists_ShouldThrowException() {
            // given
            ConventionId id = ConventionId.of(999L);

            given(conventionReadManager.existsById(id)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateExists(id))
                    .isInstanceOf(ConventionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicate 메서드")
    class ValidateNotDuplicate {

        @Test
        @DisplayName("성공 - 중복되지 않는 모듈+버전")
        void validateNotDuplicate_WhenNotDuplicate_ShouldNotThrow() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            ConventionVersion version = ConventionVersion.of("1.0.0");

            given(conventionReadManager.existsByModuleIdAndVersion(moduleId, version.value()))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicate(moduleId, version))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 중복된 모듈+버전인 경우 예외")
        void validateNotDuplicate_WhenDuplicate_ShouldThrowException() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            ConventionVersion version = ConventionVersion.of("1.0.0");

            given(conventionReadManager.existsByModuleIdAndVersion(moduleId, version.value()))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicate(moduleId, version))
                    .isInstanceOf(ConventionDuplicateException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicateExcluding 메서드")
    class ValidateNotDuplicateExcluding {

        @Test
        @DisplayName("성공 - 자신을 제외하고 중복되지 않는 모듈+버전")
        void validateNotDuplicateExcluding_WhenNotDuplicate_ShouldNotThrow() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            ConventionVersion version = ConventionVersion.of("1.0.0");
            ConventionId excludeId = ConventionId.of(1L);

            given(
                            conventionReadManager.existsByModuleIdAndVersionAndIdNot(
                                    moduleId, version.value(), excludeId))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicateExcluding(moduleId, version, excludeId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 다른 Convention에서 이미 사용 중인 경우 예외")
        void validateNotDuplicateExcluding_WhenDuplicate_ShouldThrowException() {
            // given
            ModuleId moduleId = ModuleId.of(1L);
            ConventionVersion version = ConventionVersion.of("1.0.0");
            ConventionId excludeId = ConventionId.of(1L);

            given(
                            conventionReadManager.existsByModuleIdAndVersionAndIdNot(
                                    moduleId, version.value(), excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () -> sut.validateNotDuplicateExcluding(moduleId, version, excludeId))
                    .isInstanceOf(ConventionDuplicateException.class);
        }
    }
}
