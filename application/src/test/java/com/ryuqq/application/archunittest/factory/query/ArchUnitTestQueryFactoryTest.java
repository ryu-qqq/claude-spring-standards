package com.ryuqq.application.archunittest.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.archunittest.dto.query.ArchUnitTestSearchParams;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSearchField;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ArchUnitTestQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ArchUnitTestQueryFactory 단위 테스트")
class ArchUnitTestQueryFactoryTest {

    private ArchUnitTestQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ArchUnitTestQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ArchUnitTestSearchParams searchParams = ArchUnitTestSearchParams.of(cursorParams);

            // when
            ArchUnitTestSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            ArchUnitTestSearchParams searchParams = ArchUnitTestSearchParams.of(cursorParams);

            // when
            ArchUnitTestSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - StructureIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithStructureIds_ShouldReturnCriteriaWithStructureIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ArchUnitTestSearchParams searchParams =
                    ArchUnitTestSearchParams.of(cursorParams, List.of(1L, 2L), null, null, null);

            // when
            ArchUnitTestSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            ArchUnitTestSearchParams searchParams =
                    ArchUnitTestSearchParams.of(cursorParams, null, "NAME", "Domain", null);

            // when
            ArchUnitTestSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo(ArchUnitTestSearchField.NAME);
            assertThat(result.searchWord()).isEqualTo("Domain");
        }

        @Test
        @DisplayName("성공 - Severities 필터 포함 Criteria 생성")
        void createSliceCriteria_WithSeverities_ShouldReturnCriteriaWithSeverities() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ArchUnitTestSearchParams searchParams =
                    ArchUnitTestSearchParams.of(
                            cursorParams, null, null, null, List.of("BLOCKER", "CRITICAL"));

            // when
            ArchUnitTestSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.severities())
                    .containsExactlyInAnyOrder(
                            ArchUnitTestSeverity.BLOCKER, ArchUnitTestSeverity.CRITICAL);
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            ArchUnitTestSearchParams searchParams =
                    ArchUnitTestSearchParams.of(
                            cursorParams, List.of(1L), "CODE", "ARCH", List.of("MAJOR"));

            // when
            ArchUnitTestSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.structureIds()).hasSize(1);
            assertThat(result.searchField()).isEqualTo(ArchUnitTestSearchField.CODE);
            assertThat(result.searchWord()).isEqualTo("ARCH");
            assertThat(result.severities()).containsExactly(ArchUnitTestSeverity.MAJOR);
        }
    }

    @Nested
    @DisplayName("toArchUnitTestId 메서드")
    class ToArchUnitTestId {

        @Test
        @DisplayName("성공 - Long을 ArchUnitTestId로 변환")
        void toArchUnitTestId_WithValidId_ShouldReturnArchUnitTestId() {
            // given
            Long archUnitTestId = 1L;

            // when
            ArchUnitTestId result = sut.toArchUnitTestId(archUnitTestId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(archUnitTestId);
        }
    }

    @Nested
    @DisplayName("toStructureId 메서드")
    class ToStructureId {

        @Test
        @DisplayName("성공 - Long을 PackageStructureId로 변환")
        void toStructureId_WithValidId_ShouldReturnPackageStructureId() {
            // given
            Long structureId = 1L;

            // when
            PackageStructureId result = sut.toStructureId(structureId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(structureId);
        }
    }
}
