package com.ryuqq.application.architecture.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.architecture.manager.ArchitectureReadManager;
import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.exception.ArchitectureDuplicateNameException;
import com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException;
import com.ryuqq.domain.architecture.fixture.ArchitectureFixture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.id.TechStackId;
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
 * ArchitectureValidator 단위 테스트
 *
 * <p>Architecture 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("ArchitectureValidator 단위 테스트")
class ArchitectureValidatorTest {

    @Mock private ArchitectureReadManager architectureReadManager;

    @Mock private TechStackReadManager techStackReadManager;

    private ArchitectureValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ArchitectureValidator(architectureReadManager, techStackReadManager);
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공 - 존재하는 Architecture")
        void validateExists_WhenExists_ShouldNotThrow() {
            // given
            ArchitectureId id = ArchitectureId.of(1L);

            given(architectureReadManager.existsById(id)).willReturn(true);

            // when & then - no exception
            assertThatCode(() -> sut.validateExists(id)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void validateExists_WhenNotExists_ShouldThrowException() {
            // given
            ArchitectureId id = ArchitectureId.of(999L);

            given(architectureReadManager.existsById(id)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateExists(id))
                    .isInstanceOf(ArchitectureNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 Architecture 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnArchitecture() {
            // given
            ArchitectureId id = ArchitectureId.of(1L);
            Architecture expected = ArchitectureFixture.defaultExistingArchitecture();

            given(architectureReadManager.findById(id)).willReturn(Optional.of(expected));

            // when
            Architecture result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void findExistingOrThrow_WhenNotExists_ShouldThrowException() {
            // given
            ArchitectureId id = ArchitectureId.of(999L);

            given(architectureReadManager.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(ArchitectureNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateTechStackExists 메서드")
    class ValidateTechStackExists {

        @Test
        @DisplayName("성공 - 존재하는 TechStack")
        void validateTechStackExists_WhenExists_ShouldNotThrow() {
            // given
            TechStackId techStackId = TechStackId.of(1L);

            given(techStackReadManager.existsById(techStackId)).willReturn(true);

            // when & then - no exception
            assertThatCode(() -> sut.validateTechStackExists(techStackId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void validateTechStackExists_WhenNotExists_ShouldThrowException() {
            // given
            TechStackId techStackId = TechStackId.of(999L);

            given(techStackReadManager.existsById(techStackId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateTechStackExists(techStackId))
                    .isInstanceOf(TechStackNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNameNotDuplicate 메서드")
    class ValidateNameNotDuplicate {

        @Test
        @DisplayName("성공 - 중복되지 않는 이름")
        void validateNameNotDuplicate_WhenNotDuplicate_ShouldNotThrow() {
            // given
            ArchitectureName name = ArchitectureName.of("new-architecture");

            given(architectureReadManager.existsByName(name)).willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNameNotDuplicate(name)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우 예외")
        void validateNameNotDuplicate_WhenDuplicate_ShouldThrowException() {
            // given
            ArchitectureName name = ArchitectureName.of("existing-architecture");

            given(architectureReadManager.existsByName(name)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNameNotDuplicate(name))
                    .isInstanceOf(ArchitectureDuplicateNameException.class);
        }
    }

    @Nested
    @DisplayName("validateNameNotDuplicateExcluding 메서드")
    class ValidateNameNotDuplicateExcluding {

        @Test
        @DisplayName("성공 - 자신을 제외하고 중복되지 않는 이름")
        void validateNameNotDuplicateExcluding_WhenNotDuplicate_ShouldNotThrow() {
            // given
            ArchitectureName name = ArchitectureName.of("updated-architecture");
            ArchitectureId excludeId = ArchitectureId.of(1L);

            given(architectureReadManager.existsByNameAndIdNot(name, excludeId)).willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNameNotDuplicateExcluding(name, excludeId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 다른 Architecture에서 이미 사용 중인 경우 예외")
        void validateNameNotDuplicateExcluding_WhenDuplicate_ShouldThrowException() {
            // given
            ArchitectureName name = ArchitectureName.of("existing-architecture");
            ArchitectureId excludeId = ArchitectureId.of(1L);

            given(architectureReadManager.existsByNameAndIdNot(name, excludeId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNameNotDuplicateExcluding(name, excludeId))
                    .isInstanceOf(ArchitectureDuplicateNameException.class);
        }
    }
}
