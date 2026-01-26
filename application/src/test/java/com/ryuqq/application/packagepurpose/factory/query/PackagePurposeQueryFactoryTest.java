package com.ryuqq.application.packagepurpose.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import com.ryuqq.domain.packagepurpose.vo.PackagePurposeSearchField;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PackagePurposeQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("PackagePurposeQueryFactory 단위 테스트")
class PackagePurposeQueryFactoryTest {

    private PackagePurposeQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new PackagePurposeQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            PackagePurposeSearchParams searchParams = PackagePurposeSearchParams.of(cursorParams);

            // when
            PackagePurposeSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            PackagePurposeSearchParams searchParams = PackagePurposeSearchParams.of(cursorParams);

            // when
            PackagePurposeSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - StructureIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithStructureIds_ShouldReturnCriteriaWithStructureIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            PackagePurposeSearchParams searchParams =
                    PackagePurposeSearchParams.of(cursorParams, List.of(1L, 2L), null, null);

            // when
            PackagePurposeSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.structureIds())
                    .extracting(PackageStructureId::value)
                    .containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - 검색 조건 포함 Criteria 생성")
        void createSliceCriteria_WithSearch_ShouldReturnCriteriaWithSearch() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            PackagePurposeSearchParams searchParams =
                    PackagePurposeSearchParams.of(cursorParams, null, "CODE", "AGG");

            // when
            PackagePurposeSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo(PackagePurposeSearchField.CODE);
            assertThat(result.searchWord()).isEqualTo("AGG");
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            PackagePurposeSearchParams searchParams =
                    PackagePurposeSearchParams.of(cursorParams, List.of(1L), "NAME", "Aggregate");

            // when
            PackagePurposeSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.structureIds()).hasSize(1);
            assertThat(result.searchField()).isEqualTo(PackagePurposeSearchField.NAME);
            assertThat(result.searchWord()).isEqualTo("Aggregate");
        }
    }
}
