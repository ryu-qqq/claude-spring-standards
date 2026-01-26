package com.ryuqq.application.configfiletemplate.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.configfiletemplate.dto.query.ConfigFileTemplateSearchParams;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ConfigFileTemplateQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ConfigFileTemplateQueryFactory 단위 테스트")
class ConfigFileTemplateQueryFactoryTest {

    private ConfigFileTemplateQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ConfigFileTemplateQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConfigFileTemplateSearchParams searchParams =
                    ConfigFileTemplateSearchParams.of(cursorParams);

            // when
            ConfigFileTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            ConfigFileTemplateSearchParams searchParams =
                    ConfigFileTemplateSearchParams.of(cursorParams);

            // when
            ConfigFileTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - ToolType 필터 포함 Criteria 생성")
        void createSliceCriteria_WithToolTypes_ShouldReturnCriteriaWithToolTypes() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConfigFileTemplateSearchParams searchParams =
                    ConfigFileTemplateSearchParams.of(
                            cursorParams, List.of("CLAUDE", "CURSOR"), null, null, null, null);

            // when
            ConfigFileTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toolTypes())
                    .containsExactlyInAnyOrder(ToolType.CLAUDE, ToolType.CURSOR);
        }

        @Test
        @DisplayName("성공 - Category 필터 포함 Criteria 생성")
        void createSliceCriteria_WithCategories_ShouldReturnCriteriaWithCategories() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConfigFileTemplateSearchParams searchParams =
                    ConfigFileTemplateSearchParams.of(
                            cursorParams, null, null, null, List.of("MAIN_CONFIG", "RULE"), null);

            // when
            ConfigFileTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.categories())
                    .containsExactlyInAnyOrder(TemplateCategory.MAIN_CONFIG, TemplateCategory.RULE);
        }

        @Test
        @DisplayName("성공 - TechStackId 필터 포함 Criteria 생성")
        void createSliceCriteria_WithTechStackIds_ShouldReturnCriteriaWithTechStackIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConfigFileTemplateSearchParams searchParams =
                    ConfigFileTemplateSearchParams.of(
                            cursorParams, null, List.of(1L, 2L), null, null, null);

            // when
            ConfigFileTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.techStackIds()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            ConfigFileTemplateSearchParams searchParams =
                    ConfigFileTemplateSearchParams.of(
                            cursorParams,
                            List.of("CLAUDE"),
                            List.of(1L),
                            null,
                            List.of("MAIN_CONFIG"),
                            null);

            // when
            ConfigFileTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.techStackIds()).hasSize(1);
            assertThat(result.toolTypes()).containsExactly(ToolType.CLAUDE);
            assertThat(result.categories()).containsExactly(TemplateCategory.MAIN_CONFIG);
        }
    }
}
