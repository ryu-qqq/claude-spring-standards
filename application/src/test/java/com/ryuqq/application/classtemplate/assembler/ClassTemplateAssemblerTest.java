package com.ryuqq.application.classtemplate.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.classtemplate.dto.response.ClassTemplateResult;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.fixture.ClassTemplateFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ClassTemplateAssembler 단위 테스트
 *
 * <p>ClassTemplate 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ClassTemplateAssembler 단위 테스트")
class ClassTemplateAssemblerTest {

    private ClassTemplateAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTemplateAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - ClassTemplate을 ClassTemplateResult로 변환")
        void toResult_WithValidClassTemplate_ShouldReturnResult() {
            // given
            ClassTemplate classTemplate = ClassTemplateFixture.defaultExistingClassTemplate();

            // when
            ClassTemplateResult result = sut.toResult(classTemplate);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - 필수 어노테이션이 있는 ClassTemplate 변환")
        void toResult_WithRequiredAnnotations_ShouldReturnResult() {
            // given
            ClassTemplate classTemplate =
                    ClassTemplateFixture.classTemplateWithRequiredAnnotations();

            // when
            ClassTemplateResult result = sut.toResult(classTemplate);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공 - 금지 어노테이션이 있는 ClassTemplate 변환")
        void toResult_WithForbiddenAnnotations_ShouldReturnResult() {
            // given
            ClassTemplate classTemplate =
                    ClassTemplateFixture.classTemplateWithForbiddenAnnotations();

            // when
            ClassTemplateResult result = sut.toResult(classTemplate);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - ClassTemplate 목록을 ClassTemplateResult 목록으로 변환")
        void toResults_WithValidClassTemplates_ShouldReturnResults() {
            // given
            ClassTemplate classTemplate1 = ClassTemplateFixture.defaultExistingClassTemplate();
            ClassTemplate classTemplate2 =
                    ClassTemplateFixture.classTemplateWithRequiredAnnotations();
            List<ClassTemplate> classTemplates = List.of(classTemplate1, classTemplate2);

            // when
            List<ClassTemplateResult> results = sut.toResults(classTemplates);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<ClassTemplate> classTemplates = List.of();

            // when
            List<ClassTemplateResult> results = sut.toResults(classTemplates);

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
            ClassTemplate classTemplate1 = ClassTemplateFixture.defaultExistingClassTemplate();
            ClassTemplate classTemplate2 =
                    ClassTemplateFixture.classTemplateWithRequiredAnnotations();
            ClassTemplate classTemplate3 =
                    ClassTemplateFixture.classTemplateWithForbiddenAnnotations();
            List<ClassTemplate> classTemplates =
                    List.of(classTemplate1, classTemplate2, classTemplate3);
            int size = 2;

            // when
            ClassTemplateSliceResult result = sut.toSliceResult(classTemplates, size);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.classTemplates()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            ClassTemplate classTemplate1 = ClassTemplateFixture.defaultExistingClassTemplate();
            List<ClassTemplate> classTemplates = List.of(classTemplate1);
            int size = 10;

            // when
            ClassTemplateSliceResult result = sut.toSliceResult(classTemplates, size);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.classTemplates()).hasSize(1);
        }
    }
}
