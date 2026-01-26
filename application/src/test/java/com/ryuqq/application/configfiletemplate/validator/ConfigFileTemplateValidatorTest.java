package com.ryuqq.application.configfiletemplate.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplateReadManager;
import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.exception.ConfigFileTemplateNotFoundException;
import com.ryuqq.domain.configfiletemplate.id.ConfigFileTemplateId;
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
 * ConfigFileTemplateValidator 단위 테스트
 *
 * <p>ConfigFileTemplate 검증기의 검증 로직을 테스트합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("ConfigFileTemplateValidator 단위 테스트")
class ConfigFileTemplateValidatorTest {

    @Mock private ConfigFileTemplateReadManager configFileTemplateReadManager;

    @Mock private TechStackReadManager techStackReadManager;

    @Mock private ConfigFileTemplate configFileTemplate;

    private ConfigFileTemplateValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ConfigFileTemplateValidator(configFileTemplateReadManager, techStackReadManager);
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공 - 존재하는 ID인 경우")
        void validateExists_WhenExists_ShouldNotThrow() {
            // given
            ConfigFileTemplateId id = ConfigFileTemplateId.of(1L);
            given(configFileTemplateReadManager.existsById(id)).willReturn(true);

            // when & then
            sut.validateExists(id);

            then(configFileTemplateReadManager).should().existsById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID인 경우")
        void validateExists_WhenNotExists_ShouldThrowException() {
            // given
            ConfigFileTemplateId id = ConfigFileTemplateId.of(999L);
            given(configFileTemplateReadManager.existsById(id)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateExists(id))
                    .isInstanceOf(ConfigFileTemplateNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 ConfigFileTemplate 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnConfigFileTemplate() {
            // given
            ConfigFileTemplateId id = ConfigFileTemplateId.of(1L);
            given(configFileTemplateReadManager.findById(id))
                    .willReturn(Optional.of(configFileTemplate));

            // when
            ConfigFileTemplate result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(configFileTemplate);
            then(configFileTemplateReadManager).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 경우 예외 발생")
        void findExistingOrThrow_WhenNotExists_ShouldThrowException() {
            // given
            ConfigFileTemplateId id = ConfigFileTemplateId.of(999L);
            given(configFileTemplateReadManager.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(ConfigFileTemplateNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateTechStackExists 메서드")
    class ValidateTechStackExists {

        @Test
        @DisplayName("성공 - TechStack이 존재하는 경우")
        void validateTechStackExists_WhenExists_ShouldNotThrow() {
            // given
            TechStackId techStackId = TechStackId.of(1L);
            given(techStackReadManager.existsById(techStackId)).willReturn(true);

            // when & then
            sut.validateTechStackExists(techStackId);

            then(techStackReadManager).should().existsById(techStackId);
        }

        @Test
        @DisplayName("실패 - TechStack이 존재하지 않는 경우")
        void validateTechStackExists_WhenNotExists_ShouldThrowException() {
            // given
            TechStackId techStackId = TechStackId.of(999L);
            given(techStackReadManager.existsById(techStackId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateTechStackExists(techStackId))
                    .isInstanceOf(TechStackNotFoundException.class);
        }
    }
}
