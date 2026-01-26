package com.ryuqq.application.architecture.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.architecture.dto.query.ArchitectureSearchParams;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ArchitectureQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ArchitectureQueryFactory 단위 테스트")
class ArchitectureQueryFactoryTest {

    private ArchitectureQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ArchitectureQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ArchitectureSearchParams searchParams =
                    new ArchitectureSearchParams(cursorParams, null);

            // when
            ArchitectureSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            ArchitectureSearchParams searchParams =
                    new ArchitectureSearchParams(cursorParams, null);

            // when
            ArchitectureSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("성공 - TechStackIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithTechStackIds_ShouldReturnCriteriaWithTechStackIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ArchitectureSearchParams searchParams =
                    new ArchitectureSearchParams(cursorParams, List.of(1L, 2L, 3L));

            // when
            ArchitectureSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.techStackIds())
                    .extracting(TechStackId::value)
                    .containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("성공 - 필터 없이 기본 Criteria 생성")
        void createSliceCriteria_WithoutFilters_ShouldReturnCriteriaWithoutFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ArchitectureSearchParams searchParams =
                    new ArchitectureSearchParams(cursorParams, null);

            // when
            ArchitectureSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.techStackIds()).isNull();
        }

        @Test
        @DisplayName("성공 - 빈 TechStackIds 리스트로 Criteria 생성")
        void createSliceCriteria_WithEmptyTechStackIds_ShouldReturnCriteriaWithoutTechStackIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ArchitectureSearchParams searchParams =
                    new ArchitectureSearchParams(cursorParams, List.of());

            // when
            ArchitectureSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.techStackIds()).isNull();
        }
    }
}
