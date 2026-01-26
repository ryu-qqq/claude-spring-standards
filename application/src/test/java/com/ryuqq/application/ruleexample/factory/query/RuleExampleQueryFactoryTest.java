package com.ryuqq.application.ruleexample.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.ruleexample.dto.query.RuleExampleSearchParams;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RuleExampleQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("RuleExampleQueryFactory 단위 테스트")
class RuleExampleQueryFactoryTest {

    private RuleExampleQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new RuleExampleQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            RuleExampleSearchParams searchParams = RuleExampleSearchParams.of(cursorParams);

            // when
            RuleExampleSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            RuleExampleSearchParams searchParams = RuleExampleSearchParams.of(cursorParams);

            // when
            RuleExampleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - RuleIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithRuleIds_ShouldReturnCriteriaWithRuleIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            RuleExampleSearchParams searchParams =
                    RuleExampleSearchParams.of(cursorParams, List.of(1L, 2L), null, null);

            // when
            RuleExampleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.ruleIds()).extracting(CodingRuleId::value).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - ExampleTypes 필터 포함 Criteria 생성")
        void createSliceCriteria_WithExampleTypes_ShouldReturnCriteriaWithExampleTypes() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            RuleExampleSearchParams searchParams =
                    RuleExampleSearchParams.of(cursorParams, null, List.of("GOOD", "BAD"), null);

            // when
            RuleExampleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.exampleTypes())
                    .containsExactlyInAnyOrder(ExampleType.GOOD, ExampleType.BAD);
        }

        @Test
        @DisplayName("성공 - Languages 필터 포함 Criteria 생성")
        void createSliceCriteria_WithLanguages_ShouldReturnCriteriaWithLanguages() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            RuleExampleSearchParams searchParams =
                    RuleExampleSearchParams.of(cursorParams, null, null, List.of("JAVA", "KOTLIN"));

            // when
            RuleExampleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.languages())
                    .containsExactlyInAnyOrder(ExampleLanguage.JAVA, ExampleLanguage.KOTLIN);
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            RuleExampleSearchParams searchParams =
                    RuleExampleSearchParams.of(
                            cursorParams, List.of(1L), List.of("GOOD"), List.of("JAVA"));

            // when
            RuleExampleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.ruleIds()).hasSize(1);
            assertThat(result.exampleTypes()).containsExactly(ExampleType.GOOD);
            assertThat(result.languages()).containsExactly(ExampleLanguage.JAVA);
        }
    }

    @Nested
    @DisplayName("toRuleExampleId 메서드")
    class ToRuleExampleId {

        @Test
        @DisplayName("성공 - Long을 RuleExampleId로 변환")
        void toRuleExampleId_WithValidId_ShouldReturnRuleExampleId() {
            // given
            Long ruleExampleId = 1L;

            // when
            RuleExampleId result = sut.toRuleExampleId(ruleExampleId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(ruleExampleId);
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
