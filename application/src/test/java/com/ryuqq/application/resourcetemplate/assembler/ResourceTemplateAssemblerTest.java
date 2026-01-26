package com.ryuqq.application.resourcetemplate.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateResult;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.fixture.ResourceTemplateFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ResourceTemplateAssembler 단위 테스트
 *
 * <p>ResourceTemplate 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ResourceTemplateAssembler 단위 테스트")
class ResourceTemplateAssemblerTest {

    private ResourceTemplateAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ResourceTemplateAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - ResourceTemplate을 ResourceTemplateResult로 변환")
        void toResult_WithValidResourceTemplate_ShouldReturnResult() {
            // given
            ResourceTemplate resourceTemplate = ResourceTemplateFixture.reconstitute();

            // when
            ResourceTemplateResult result = sut.toResult(resourceTemplate);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - ResourceTemplate 목록을 ResourceTemplateResult 목록으로 변환")
        void toResults_WithValidResourceTemplates_ShouldReturnResults() {
            // given
            ResourceTemplate template1 = ResourceTemplateFixture.reconstitute();
            ResourceTemplate template2 = ResourceTemplateFixture.reconstitute();
            List<ResourceTemplate> templates = List.of(template1, template2);

            // when
            List<ResourceTemplateResult> results = sut.toResults(templates);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<ResourceTemplate> templates = List.of();

            // when
            List<ResourceTemplateResult> results = sut.toResults(templates);

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
            ResourceTemplate template1 = ResourceTemplateFixture.reconstitute();
            ResourceTemplate template2 = ResourceTemplateFixture.reconstitute();
            ResourceTemplate template3 = ResourceTemplateFixture.reconstitute();
            List<ResourceTemplate> templates = List.of(template1, template2, template3);
            int requestedSize = 2;

            // when
            ResourceTemplateSliceResult result = sut.toSliceResult(templates, requestedSize);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.resourceTemplates()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            ResourceTemplate template1 = ResourceTemplateFixture.reconstitute();
            List<ResourceTemplate> templates = List.of(template1);
            int requestedSize = 10;

            // when
            ResourceTemplateSliceResult result = sut.toSliceResult(templates, requestedSize);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.resourceTemplates()).hasSize(1);
        }
    }
}
