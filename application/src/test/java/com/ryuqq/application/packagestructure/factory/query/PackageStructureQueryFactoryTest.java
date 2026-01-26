package com.ryuqq.application.packagestructure.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.packagestructure.dto.query.PackageStructureSearchParams;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PackageStructureQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("PackageStructureQueryFactory 단위 테스트")
class PackageStructureQueryFactoryTest {

    private PackageStructureQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new PackageStructureQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            PackageStructureSearchParams searchParams =
                    PackageStructureSearchParams.of(cursorParams);

            // when
            PackageStructureSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            PackageStructureSearchParams searchParams =
                    PackageStructureSearchParams.of(cursorParams);

            // when
            PackageStructureSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - ModuleIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithModuleIds_ShouldReturnCriteriaWithModuleIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            PackageStructureSearchParams searchParams =
                    PackageStructureSearchParams.of(cursorParams, List.of(1L, 2L));

            // when
            PackageStructureSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleIds()).extracting(ModuleId::value).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - 필터 없이 기본 Criteria 생성")
        void createSliceCriteria_WithoutFilters_ShouldReturnCriteriaWithoutFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            PackageStructureSearchParams searchParams =
                    PackageStructureSearchParams.of(cursorParams);

            // when
            PackageStructureSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleIds()).isNull();
        }

        @Test
        @DisplayName("성공 - 빈 ModuleIds 리스트로 Criteria 생성")
        void createSliceCriteria_WithEmptyModuleIds_ShouldReturnCriteriaWithoutModuleIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            PackageStructureSearchParams searchParams =
                    PackageStructureSearchParams.of(cursorParams, List.of());

            // when
            PackageStructureSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.moduleIds()).isNull();
        }
    }

    @Nested
    @DisplayName("toPackageStructureId 메서드")
    class ToPackageStructureId {

        @Test
        @DisplayName("성공 - Long을 PackageStructureId로 변환")
        void toPackageStructureId_WithValidId_ShouldReturnPackageStructureId() {
            // given
            Long packageStructureId = 1L;

            // when
            PackageStructureId result = sut.toPackageStructureId(packageStructureId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(packageStructureId);
        }
    }

    @Nested
    @DisplayName("toModuleId 메서드")
    class ToModuleId {

        @Test
        @DisplayName("성공 - Long을 ModuleId로 변환")
        void toModuleId_WithValidId_ShouldReturnModuleId() {
            // given
            Long moduleId = 1L;

            // when
            ModuleId result = sut.toModuleId(moduleId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(moduleId);
        }
    }
}
