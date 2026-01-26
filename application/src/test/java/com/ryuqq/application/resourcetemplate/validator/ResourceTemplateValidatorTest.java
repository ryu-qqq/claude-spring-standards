package com.ryuqq.application.resourcetemplate.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.resourcetemplate.manager.ResourceTemplateReadManager;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.fixture.ResourceTemplateFixture;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ResourceTemplateValidator 단위 테스트
 *
 * <p>ResourceTemplate 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("ResourceTemplateValidator 단위 테스트")
class ResourceTemplateValidatorTest {

    @Mock private ResourceTemplateReadManager resourceTemplateReadManager;

    private ResourceTemplateValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ResourceTemplateValidator(resourceTemplateReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 ResourceTemplate 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnResourceTemplate() {
            // given
            ResourceTemplateId id = ResourceTemplateId.of(1L);
            ResourceTemplate expected = ResourceTemplateFixture.reconstitute();

            given(resourceTemplateReadManager.getById(id)).willReturn(expected);

            // when
            ResourceTemplate result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공 - 존재하는 ResourceTemplate")
        void validateExists_WhenExists_ShouldNotThrow() {
            // given
            ResourceTemplateId id = ResourceTemplateId.of(1L);
            ResourceTemplate resourceTemplate = ResourceTemplateFixture.reconstitute();

            given(resourceTemplateReadManager.getById(id)).willReturn(resourceTemplate);

            // when & then - no exception
            assertThatCode(() -> sut.validateExists(id)).doesNotThrowAnyException();
        }
    }
}
