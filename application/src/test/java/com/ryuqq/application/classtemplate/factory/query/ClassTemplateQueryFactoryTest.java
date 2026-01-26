package com.ryuqq.application.classtemplate.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.classtemplate.dto.query.ClassTemplateSearchParams;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ClassTemplateQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ClassTemplateQueryFactory 단위 테스트")
class ClassTemplateQueryFactoryTest {

    private ClassTemplateQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ClassTemplateQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ClassTemplateSearchParams searchParams = ClassTemplateSearchParams.of(cursorParams);

            // when
            ClassTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            ClassTemplateSearchParams searchParams = ClassTemplateSearchParams.of(cursorParams);

            // when
            ClassTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
        }

        @Test
        @DisplayName("성공 - StructureIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithStructureIds_ShouldReturnCriteriaWithStructureIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ClassTemplateSearchParams searchParams =
                    ClassTemplateSearchParams.of(cursorParams, List.of(1L, 2L), null);

            // when
            ClassTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.structureIds())
                    .extracting(PackageStructureId::value)
                    .containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - ClassTypeIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithClassTypeIds_ShouldReturnCriteriaWithClassTypeIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ClassTemplateSearchParams searchParams =
                    ClassTemplateSearchParams.of(cursorParams, null, List.of(1L, 2L));

            // when
            ClassTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.classTypeIds())
                    .extracting(ClassTypeId::value)
                    .containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("성공 - 모든 필터 포함 Criteria 생성")
        void createSliceCriteria_WithAllFilters_ShouldReturnCriteriaWithAllFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.of("50", 10);
            ClassTemplateSearchParams searchParams =
                    ClassTemplateSearchParams.of(cursorParams, List.of(1L), List.of(1L));

            // when
            ClassTemplateSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(50L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(10);
            assertThat(result.structureIds()).hasSize(1);
            assertThat(result.classTypeIds()).extracting(ClassTypeId::value).containsExactly(1L);
        }
    }

    @Nested
    @DisplayName("toClassTemplateId 메서드")
    class ToClassTemplateId {

        @Test
        @DisplayName("성공 - Long을 ClassTemplateId로 변환")
        void toClassTemplateId_WithValidId_ShouldReturnClassTemplateId() {
            // given
            Long classTemplateId = 1L;

            // when
            ClassTemplateId result = sut.toClassTemplateId(classTemplateId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(classTemplateId);
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
