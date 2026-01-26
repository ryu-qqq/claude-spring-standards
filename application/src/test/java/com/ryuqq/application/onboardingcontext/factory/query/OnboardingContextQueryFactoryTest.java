package com.ryuqq.application.onboardingcontext.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.onboardingcontext.dto.query.OnboardingContextSearchParams;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OnboardingContextQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("OnboardingContextQueryFactory 단위 테스트")
class OnboardingContextQueryFactoryTest {

    private OnboardingContextQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingContextQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            OnboardingContextSearchParams searchParams =
                    OnboardingContextSearchParams.of(cursorParams);

            // when
            OnboardingContextSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            OnboardingContextSearchParams searchParams =
                    OnboardingContextSearchParams.of(cursorParams);

            // when
            OnboardingContextSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - ContextType 필터 포함 Criteria 생성")
        void createSliceCriteria_WithContextTypes_ShouldReturnCriteriaWithContextTypes() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            OnboardingContextSearchParams searchParams =
                    OnboardingContextSearchParams.of(
                            cursorParams, null, null, List.of("SUMMARY", "ZERO_TOLERANCE"));

            // when
            OnboardingContextSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.contextTypes())
                    .containsExactlyInAnyOrder(ContextType.SUMMARY, ContextType.ZERO_TOLERANCE);
        }

        @Test
        @DisplayName("성공 - TechStackId 필터 포함 Criteria 생성")
        void createSliceCriteria_WithTechStackIds_ShouldReturnCriteriaWithTechStackIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            OnboardingContextSearchParams searchParams =
                    OnboardingContextSearchParams.of(cursorParams, List.of(1L, 2L), null, null);

            // when
            OnboardingContextSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.techStackIds()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            OnboardingContextSearchParams searchParams =
                    OnboardingContextSearchParams.of(
                            cursorParams, List.of(1L), null, List.of("SUMMARY"));

            // when
            OnboardingContextSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.techStackIds()).hasSize(1);
            assertThat(result.contextTypes()).containsExactly(ContextType.SUMMARY);
        }
    }
}
