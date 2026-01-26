package com.ryuqq.application.codingrule.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.codingrule.dto.query.CodingRuleSearchParams;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CodingRuleQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("CodingRuleQueryFactory 단위 테스트")
class CodingRuleQueryFactoryTest {

    private CodingRuleQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new CodingRuleQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            CodingRuleSearchParams searchParams = CodingRuleSearchParams.of(cursorParams);

            // when
            CodingRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            CodingRuleSearchParams searchParams = CodingRuleSearchParams.of(cursorParams);

            // when
            CodingRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - 카테고리 필터 포함 Criteria 생성")
        void createSliceCriteria_WithCategories_ShouldReturnCriteriaWithCategories() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            CodingRuleSearchParams searchParams =
                    CodingRuleSearchParams.of(
                            cursorParams, List.of("NAMING", "STRUCTURE"), null, null, null);

            // when
            CodingRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.categories())
                    .containsExactlyInAnyOrder(RuleCategory.NAMING, RuleCategory.STRUCTURE);
        }

        @Test
        @DisplayName("성공 - 심각도 필터 포함 Criteria 생성")
        void createSliceCriteria_WithSeverities_ShouldReturnCriteriaWithSeverities() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            CodingRuleSearchParams searchParams =
                    CodingRuleSearchParams.of(
                            cursorParams, null, List.of("CRITICAL", "MAJOR"), null, null);

            // when
            CodingRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.severities())
                    .containsExactlyInAnyOrder(RuleSeverity.CRITICAL, RuleSeverity.MAJOR);
        }

        @Test
        @DisplayName("성공 - 검색 조건 포함 Criteria 생성")
        void createSliceCriteria_WithSearchCondition_ShouldReturnCriteriaWithSearch() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            CodingRuleSearchParams searchParams =
                    CodingRuleSearchParams.of(cursorParams, null, null, "CODE", "DOM-001");

            // when
            CodingRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo("CODE");
            assertThat(result.searchWord()).isEqualTo("DOM-001");
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            CodingRuleSearchParams searchParams =
                    CodingRuleSearchParams.of(
                            cursorParams, List.of("NAMING"), List.of("CRITICAL"), "NAME", "Lombok");

            // when
            CodingRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.categories()).containsExactly(RuleCategory.NAMING);
            assertThat(result.severities()).containsExactly(RuleSeverity.CRITICAL);
            assertThat(result.searchField()).isEqualTo("NAME");
            assertThat(result.searchWord()).isEqualTo("Lombok");
        }
    }

    @Nested
    @DisplayName("toCodingRuleId 메서드")
    class ToCodingRuleId {

        @Test
        @DisplayName("성공 - Long을 CodingRuleId로 변환")
        void toCodingRuleId_WithValidId_ShouldReturnCodingRuleId() {
            // given
            Long codingRuleId = 1L;

            // when
            CodingRuleId result = sut.toCodingRuleId(codingRuleId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(codingRuleId);
        }
    }

    @Nested
    @DisplayName("toConventionId 메서드")
    class ToConventionId {

        @Test
        @DisplayName("성공 - Long을 ConventionId로 변환")
        void toConventionId_WithValidId_ShouldReturnConventionId() {
            // given
            Long conventionId = 1L;

            // when
            ConventionId result = sut.toConventionId(conventionId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(conventionId);
        }
    }
}
