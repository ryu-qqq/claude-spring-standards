package com.ryuqq.application.convention.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.convention.dto.query.ConventionSearchParams;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ConventionQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ConventionQueryFactory 단위 테스트")
class ConventionQueryFactoryTest {

    private ConventionQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ConventionQueryFactory();
    }

    @Nested
    @DisplayName("createId 메서드")
    class CreateId {

        @Test
        @DisplayName("성공 - Long ID를 ConventionId로 변환")
        void createId_WithValidId_ShouldReturnConventionId() {
            // given
            Long id = 1L;

            // when
            ConventionId result = sut.createId(id);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(id);
        }
    }

    @Nested
    @DisplayName("createModuleId 메서드")
    class CreateModuleId {

        @Test
        @DisplayName("성공 - Long moduleId를 ModuleId로 변환")
        void createModuleId_WithValidId_ShouldReturnModuleId() {
            // given
            Long moduleId = 1L;

            // when
            ModuleId result = sut.createModuleId(moduleId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(moduleId);
        }
    }

    @Nested
    @DisplayName("createDefaultSliceCriteria 메서드")
    class CreateDefaultSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createDefaultSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);

            // when
            ConventionSliceCriteria result = sut.createDefaultSliceCriteria(cursorParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
            assertThat(result.cursorPageRequest().cursor()).isNull();
        }

        @Test
        @DisplayName("성공 - 커서 기반 페이징 Criteria 생성")
        void createDefaultSliceCriteria_WithCursor_ShouldReturnCriteriaWithCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("100", 20);

            // when
            ConventionSliceCriteria result = sut.createDefaultSliceCriteria(cursorParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConventionSearchParams searchParams = new ConventionSearchParams(cursorParams, null);

            // when
            ConventionSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
            assertThat(result.cursorPageRequest().cursor()).isNull();
        }

        @Test
        @DisplayName("성공 - ModuleIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithModuleIds_ShouldReturnCriteriaWithModuleIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConventionSearchParams searchParams =
                    new ConventionSearchParams(cursorParams, List.of(1L, 2L));

            // when
            ConventionSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleIds()).extracting(ModuleId::value).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - 필터 없이 기본 Criteria 생성")
        void createSliceCriteria_WithoutFilters_ShouldReturnCriteriaWithoutFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConventionSearchParams searchParams = new ConventionSearchParams(cursorParams, null);

            // when
            ConventionSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleIds()).isNull();
        }
    }
}
