package com.ryuqq.application.zerotolerance.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.zerotolerance.dto.query.ZeroToleranceRuleSearchParams;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceRuleSearchField;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ZeroToleranceRuleQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ZeroToleranceRuleQueryFactory 단위 테스트")
class ZeroToleranceRuleQueryFactoryTest {

    private ZeroToleranceRuleQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ZeroToleranceRuleQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ZeroToleranceRuleSearchParams searchParams =
                    ZeroToleranceRuleSearchParams.of(cursorParams);

            // when
            ZeroToleranceRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            ZeroToleranceRuleSearchParams searchParams =
                    ZeroToleranceRuleSearchParams.of(cursorParams);

            // when
            ZeroToleranceRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - ConventionIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithConventionIds_ShouldReturnCriteriaWithConventionIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ZeroToleranceRuleSearchParams searchParams =
                    ZeroToleranceRuleSearchParams.of(
                            cursorParams, List.of(1L, 2L), null, null, null, null);

            // when
            ZeroToleranceRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.conventionIds())
                    .extracting(ConventionId::value)
                    .containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - DetectionTypes 필터 포함 Criteria 생성")
        void createSliceCriteria_WithDetectionTypes_ShouldReturnCriteriaWithDetectionTypes() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ZeroToleranceRuleSearchParams searchParams =
                    ZeroToleranceRuleSearchParams.of(
                            cursorParams, null, List.of("REGEX", "AST"), null, null, null);

            // when
            ZeroToleranceRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.detectionTypes())
                    .containsExactlyInAnyOrder(DetectionType.REGEX, DetectionType.AST);
        }

        @Test
        @DisplayName("성공 - 검색 조건 포함 Criteria 생성")
        void createSliceCriteria_WithSearch_ShouldReturnCriteriaWithSearch() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ZeroToleranceRuleSearchParams searchParams =
                    ZeroToleranceRuleSearchParams.of(
                            cursorParams, null, null, "TYPE", "LOMBOK", null);

            // when
            ZeroToleranceRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo(ZeroToleranceRuleSearchField.TYPE);
            assertThat(result.searchWord()).isEqualTo("LOMBOK");
        }

        @Test
        @DisplayName("성공 - AutoRejectPr 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAutoRejectPr_ShouldReturnCriteriaWithAutoRejectPr() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ZeroToleranceRuleSearchParams searchParams =
                    ZeroToleranceRuleSearchParams.of(cursorParams, null, null, null, null, true);

            // when
            ZeroToleranceRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.autoRejectPr()).isTrue();
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            ZeroToleranceRuleSearchParams searchParams =
                    ZeroToleranceRuleSearchParams.of(
                            cursorParams, List.of(1L), List.of("REGEX"), "TYPE", "LOMBOK", false);

            // when
            ZeroToleranceRuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.conventionIds()).hasSize(1);
            assertThat(result.detectionTypes()).containsExactly(DetectionType.REGEX);
            assertThat(result.searchField()).isEqualTo(ZeroToleranceRuleSearchField.TYPE);
            assertThat(result.searchWord()).isEqualTo("LOMBOK");
            assertThat(result.autoRejectPr()).isFalse();
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

        @Test
        @DisplayName("성공 - null ID는 null 반환")
        void toConventionId_WithNullId_ShouldReturnNull() {
            // given
            Long conventionId = null;

            // when
            ConventionId result = sut.toConventionId(conventionId);

            // then
            assertThat(result).isNull();
        }
    }
}
