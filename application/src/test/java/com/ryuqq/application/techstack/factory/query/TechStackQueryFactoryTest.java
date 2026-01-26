package com.ryuqq.application.techstack.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.techstack.dto.query.TechStackSearchParams;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.TechStackStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TechStackQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("TechStackQueryFactory 단위 테스트")
class TechStackQueryFactoryTest {

    private TechStackQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new TechStackQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            TechStackSearchParams searchParams = TechStackSearchParams.of(cursorParams);

            // when
            TechStackSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            TechStackSearchParams searchParams = TechStackSearchParams.of(cursorParams);

            // when
            TechStackSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("성공 - 상태 필터 포함 Criteria 생성")
        void createSliceCriteria_WithStatusFilter_ShouldReturnCriteriaWithStatus() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            TechStackSearchParams searchParams =
                    TechStackSearchParams.of(cursorParams, "ACTIVE", null);

            // when
            TechStackSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(TechStackStatus.ACTIVE);
        }

        @Test
        @DisplayName("성공 - 플랫폼 타입 필터 포함 Criteria 생성")
        void createSliceCriteria_WithPlatformTypes_ShouldReturnCriteriaWithPlatformTypes() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            TechStackSearchParams searchParams =
                    TechStackSearchParams.of(cursorParams, null, List.of("BACKEND", "FRONTEND"));

            // when
            TechStackSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.platformTypes())
                    .containsExactlyInAnyOrder(PlatformType.BACKEND, PlatformType.FRONTEND);
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            TechStackSearchParams searchParams =
                    TechStackSearchParams.of(cursorParams, "DEPRECATED", List.of("BACKEND"));

            // when
            TechStackSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.status()).isEqualTo(TechStackStatus.DEPRECATED);
            assertThat(result.platformTypes()).containsExactly(PlatformType.BACKEND);
        }

        @Test
        @DisplayName("성공 - 필터 없이 기본 Criteria 생성")
        void createSliceCriteria_WithoutFilters_ShouldReturnCriteriaWithoutFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            TechStackSearchParams searchParams = TechStackSearchParams.of(cursorParams, null, null);

            // when
            TechStackSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.status()).isNull();
            assertThat(result.platformTypes()).isNull();
        }
    }
}
