package com.ryuqq.application.resourcetemplate.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.resourcetemplate.dto.query.ResourceTemplateSearchParams;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ResourceTemplateQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ResourceTemplateQueryFactory 단위 테스트")
class ResourceTemplateQueryFactoryTest {

    private ResourceTemplateQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ResourceTemplateQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ResourceTemplateSearchParams searchParams =
                    ResourceTemplateSearchParams.of(cursorParams);

            // when
            ResourceTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
            assertThat(result.cursorPageRequest().cursor()).isNull();
        }

        @Test
        @DisplayName("성공 - 커서 기반 페이징 Criteria 생성")
        void createSliceCriteria_WithCursor_ShouldReturnCriteriaWithCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("100", 20);
            ResourceTemplateSearchParams searchParams =
                    ResourceTemplateSearchParams.of(cursorParams);

            // when
            ResourceTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - ModuleIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithModuleIds_ShouldReturnCriteriaWithModuleIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ResourceTemplateSearchParams searchParams =
                    ResourceTemplateSearchParams.of(cursorParams, List.of(1L, 2L), null, null);

            // when
            ResourceTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleIds()).extracting(ModuleId::value).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - Categories 필터 포함 Criteria 생성")
        void createSliceCriteria_WithCategories_ShouldReturnCriteriaWithCategories() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ResourceTemplateSearchParams searchParams =
                    ResourceTemplateSearchParams.of(
                            cursorParams, null, List.of("CONFIG", "I18N"), null);

            // when
            ResourceTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.categories())
                    .containsExactlyInAnyOrder(TemplateCategory.CONFIG, TemplateCategory.I18N);
        }

        @Test
        @DisplayName("성공 - FileTypes 필터 포함 Criteria 생성")
        void createSliceCriteria_WithFileTypes_ShouldReturnCriteriaWithFileTypes() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ResourceTemplateSearchParams searchParams =
                    ResourceTemplateSearchParams.of(
                            cursorParams, null, null, List.of("YAML", "PROPERTIES"));

            // when
            ResourceTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.fileTypes())
                    .containsExactlyInAnyOrder(FileType.YAML, FileType.PROPERTIES);
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            ResourceTemplateSearchParams searchParams =
                    ResourceTemplateSearchParams.of(
                            cursorParams, List.of(1L), List.of("CONFIG"), List.of("YAML"));

            // when
            ResourceTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.moduleIds()).hasSize(1);
            assertThat(result.categories()).containsExactly(TemplateCategory.CONFIG);
            assertThat(result.fileTypes()).containsExactly(FileType.YAML);
        }
    }

    @Nested
    @DisplayName("toResourceTemplateId 메서드")
    class ToResourceTemplateId {

        @Test
        @DisplayName("성공 - Long을 ResourceTemplateId로 변환")
        void toResourceTemplateId_WithValidId_ShouldReturnResourceTemplateId() {
            // given
            Long resourceTemplateId = 1L;

            // when
            ResourceTemplateId result = sut.toResourceTemplateId(resourceTemplateId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(resourceTemplateId);
        }
    }

    @Nested
    @DisplayName("toModuleId 메서드")
    class ToModuleId {

        @Test
        @DisplayName("성공 - Long을 ModuleId로 변환")
        void toModuleId_WithValidId_ShouldReturnModuleId() {
            // given
            Long moduleId = 1L;

            // when
            ModuleId result = sut.toModuleId(moduleId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(moduleId);
        }
    }
}
