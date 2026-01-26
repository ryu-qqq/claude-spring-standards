package com.ryuqq.application.checklistitem.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.checklistitem.dto.query.ChecklistItemSearchParams;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ChecklistItemQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ChecklistItemQueryFactory 단위 테스트")
class ChecklistItemQueryFactoryTest {

    private ChecklistItemQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ChecklistItemQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ChecklistItemSearchParams searchParams = ChecklistItemSearchParams.of(cursorParams);

            // when
            ChecklistItemSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            ChecklistItemSearchParams searchParams = ChecklistItemSearchParams.of(cursorParams);

            // when
            ChecklistItemSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - RuleIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithRuleIds_ShouldReturnCriteriaWithRuleIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ChecklistItemSearchParams searchParams =
                    ChecklistItemSearchParams.of(cursorParams, List.of(1L, 2L), null, null, null);

            // when
            ChecklistItemSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.ruleIds()).extracting(CodingRuleId::value).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - CheckTypes 필터 포함 Criteria 생성")
        void createSliceCriteria_WithCheckTypes_ShouldReturnCriteriaWithCheckTypes() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ChecklistItemSearchParams searchParams =
                    ChecklistItemSearchParams.of(
                            cursorParams, null, List.of("MANUAL", "AUTOMATED"), null, null);

            // when
            ChecklistItemSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.checkTypes())
                    .containsExactlyInAnyOrder(CheckType.MANUAL, CheckType.AUTOMATED);
        }

        @Test
        @DisplayName("성공 - AutomationTools 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAutomationTools_ShouldReturnCriteriaWithAutomationTools() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ChecklistItemSearchParams searchParams =
                    ChecklistItemSearchParams.of(
                            cursorParams, null, null, List.of("CHECKSTYLE", "PMD"), null);

            // when
            ChecklistItemSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.automationTools())
                    .containsExactlyInAnyOrder(AutomationTool.CHECKSTYLE, AutomationTool.PMD);
        }

        @Test
        @DisplayName("성공 - Critical 필터 포함 Criteria 생성")
        void createSliceCriteria_WithCritical_ShouldReturnCriteriaWithCritical() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ChecklistItemSearchParams searchParams =
                    ChecklistItemSearchParams.of(cursorParams, null, null, null, true);

            // when
            ChecklistItemSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isCritical()).isTrue();
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            ChecklistItemSearchParams searchParams =
                    ChecklistItemSearchParams.of(
                            cursorParams,
                            List.of(1L),
                            List.of("MANUAL"),
                            List.of("CHECKSTYLE"),
                            true);

            // when
            ChecklistItemSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.ruleIds()).hasSize(1);
            assertThat(result.checkTypes()).containsExactly(CheckType.MANUAL);
            assertThat(result.automationTools()).containsExactly(AutomationTool.CHECKSTYLE);
            assertThat(result.isCritical()).isTrue();
        }
    }

    @Nested
    @DisplayName("toChecklistItemId 메서드")
    class ToChecklistItemId {

        @Test
        @DisplayName("성공 - Long을 ChecklistItemId로 변환")
        void toChecklistItemId_WithValidId_ShouldReturnChecklistItemId() {
            // given
            Long checklistItemId = 1L;

            // when
            ChecklistItemId result = sut.toChecklistItemId(checklistItemId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(checklistItemId);
        }
    }

    @Nested
    @DisplayName("toRuleId 메서드")
    class ToRuleId {

        @Test
        @DisplayName("성공 - Long을 CodingRuleId로 변환")
        void toRuleId_WithValidId_ShouldReturnCodingRuleId() {
            // given
            Long ruleId = 1L;

            // when
            CodingRuleId result = sut.toRuleId(ruleId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(ruleId);
        }
    }
}
