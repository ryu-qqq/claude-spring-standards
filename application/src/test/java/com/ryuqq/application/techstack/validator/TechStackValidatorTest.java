package com.ryuqq.application.techstack.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.exception.TechStackDuplicateNameException;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.fixture.TechStackFixture;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.vo.TechStackName;
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
 * TechStackValidator 단위 테스트
 *
 * <p>TechStack 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("TechStackValidator 단위 테스트")
class TechStackValidatorTest {

    @Mock private TechStackReadManager techStackReadManager;

    private TechStackValidator sut;

    @BeforeEach
    void setUp() {
        sut = new TechStackValidator(techStackReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 TechStack 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnTechStack() {
            // given
            TechStackId id = TechStackId.of(1L);
            TechStack expected = TechStackFixture.defaultExistingTechStack();

            given(techStackReadManager.findById(id)).willReturn(Optional.of(expected));

            // when
            TechStack result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외")
        void findExistingOrThrow_WhenNotExists_ShouldThrowException() {
            // given
            TechStackId id = TechStackId.of(999L);

            given(techStackReadManager.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
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
            TechStackName name = TechStackName.of("New TechStack");

            given(techStackReadManager.existsByName(name)).willReturn(false);

            // when & then - no exception
            sut.validateNameNotDuplicate(name);
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우 예외")
        void validateNameNotDuplicate_WhenDuplicate_ShouldThrowException() {
            // given
            TechStackName name = TechStackName.of("Existing TechStack");

            given(techStackReadManager.existsByName(name)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNameNotDuplicate(name))
                    .isInstanceOf(TechStackDuplicateNameException.class);
        }
    }

    @Nested
    @DisplayName("validateNameNotDuplicateExcluding 메서드")
    class ValidateNameNotDuplicateExcluding {

        @Test
        @DisplayName("성공 - 자신을 제외하고 중복되지 않는 이름")
        void validateNameNotDuplicateExcluding_WhenNotDuplicate_ShouldNotThrow() {
            // given
            TechStackName name = TechStackName.of("Updated TechStack");
            TechStackId excludeId = TechStackId.of(1L);

            given(techStackReadManager.existsByNameAndIdNot(name, excludeId)).willReturn(false);

            // when & then - no exception
            sut.validateNameNotDuplicateExcluding(name, excludeId);
        }

        @Test
        @DisplayName("실패 - 다른 TechStack에서 이미 사용 중인 경우 예외")
        void validateNameNotDuplicateExcluding_WhenDuplicate_ShouldThrowException() {
            // given
            TechStackName name = TechStackName.of("Existing TechStack");
            TechStackId excludeId = TechStackId.of(1L);

            given(techStackReadManager.existsByNameAndIdNot(name, excludeId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNameNotDuplicateExcluding(name, excludeId))
                    .isInstanceOf(TechStackDuplicateNameException.class);
        }
    }
}
