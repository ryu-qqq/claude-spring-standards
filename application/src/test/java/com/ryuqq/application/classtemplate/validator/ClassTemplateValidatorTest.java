package com.ryuqq.application.classtemplate.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.classtemplate.manager.ClassTemplateReadManager;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateDuplicateCodeException;
import com.ryuqq.domain.classtemplate.fixture.ClassTemplateFixture;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ClassTemplateValidator 단위 테스트
 *
 * <p>ClassTemplate 검증 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("validator")
@Tag("application-layer")
@DisplayName("ClassTemplateValidator 단위 테스트")
class ClassTemplateValidatorTest {

    @Mock private ClassTemplateReadManager classTemplateReadManager;

    private ClassTemplateValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTemplateValidator(classTemplateReadManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공 - 존재하는 ClassTemplate 반환")
        void findExistingOrThrow_WhenExists_ShouldReturnClassTemplate() {
            // given
            ClassTemplateId id = ClassTemplateId.of(1L);
            ClassTemplate expected = ClassTemplateFixture.defaultExistingClassTemplate();

            given(classTemplateReadManager.getById(id)).willReturn(expected);

            // when
            ClassTemplate result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicate 메서드")
    class ValidateNotDuplicate {

        @Test
        @DisplayName("성공 - 중복되지 않는 템플릿 코드")
        void validateNotDuplicate_WhenNotDuplicate_ShouldNotThrow() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of("NEW_TEMPLATE");

            given(
                            classTemplateReadManager.existsByStructureIdAndTemplateCode(
                                    structureId, templateCode))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(() -> sut.validateNotDuplicate(structureId, templateCode))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 중복된 템플릿 코드인 경우 예외")
        void validateNotDuplicate_WhenDuplicate_ShouldThrowException() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of("AGGREGATE");

            given(
                            classTemplateReadManager.existsByStructureIdAndTemplateCode(
                                    structureId, templateCode))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicate(structureId, templateCode))
                    .isInstanceOf(ClassTemplateDuplicateCodeException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicateExcluding 메서드")
    class ValidateNotDuplicateExcluding {

        @Test
        @DisplayName("성공 - 자신을 제외하고 중복되지 않는 템플릿 코드")
        void validateNotDuplicateExcluding_WhenNotDuplicate_ShouldNotThrow() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of("AGGREGATE");
            ClassTemplateId excludeId = ClassTemplateId.of(1L);

            given(
                            classTemplateReadManager.existsByStructureIdAndTemplateCodeExcluding(
                                    structureId, templateCode, excludeId))
                    .willReturn(false);

            // when & then - no exception
            assertThatCode(
                            () ->
                                    sut.validateNotDuplicateExcluding(
                                            structureId, templateCode, excludeId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패 - 다른 ClassTemplate에서 이미 사용 중인 경우 예외")
        void validateNotDuplicateExcluding_WhenDuplicate_ShouldThrowException() {
            // given
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of("AGGREGATE");
            ClassTemplateId excludeId = ClassTemplateId.of(1L);

            given(
                            classTemplateReadManager.existsByStructureIdAndTemplateCodeExcluding(
                                    structureId, templateCode, excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateNotDuplicateExcluding(
                                            structureId, templateCode, excludeId))
                    .isInstanceOf(ClassTemplateDuplicateCodeException.class);
        }
    }
}
