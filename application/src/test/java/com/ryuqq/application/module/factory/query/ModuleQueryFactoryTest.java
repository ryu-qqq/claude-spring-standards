package com.ryuqq.application.module.factory.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.module.dto.query.ModuleSearchParams;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ModuleQueryFactory 단위 테스트
 *
 * <p>Query DTO를 Domain Criteria로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ModuleQueryFactory 단위 테스트")
class ModuleQueryFactoryTest {

    private ModuleQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ModuleQueryFactory();
    }

    @Nested
    @DisplayName("createSliceCriteria 메서드")
    class CreateSliceCriteria {

        @Test
        @DisplayName("성공 - 첫 페이지 조회 Criteria 생성")
        void createSliceCriteria_WithFirstPage_ShouldReturnCriteriaWithoutCursor() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ModuleSearchParams searchParams = ModuleSearchParams.of(cursorParams);

            // when
            ModuleSliceCriteria result = sut.createSliceCriteria(searchParams);

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
            ModuleSearchParams searchParams = ModuleSearchParams.of(cursorParams);

            // when
            ModuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cursorPageRequest().cursor()).isEqualTo(100L);
            assertThat(result.cursorPageRequest().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("성공 - LayerIds 필터 포함 Criteria 생성")
        void createSliceCriteria_WithLayerIds_ShouldReturnCriteriaWithLayerIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ModuleSearchParams searchParams = ModuleSearchParams.of(cursorParams, List.of(1L, 2L));

            // when
            ModuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.layerIds()).extracting(LayerId::value).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("성공 - 필터 없이 기본 Criteria 생성")
        void createSliceCriteria_WithoutFilters_ShouldReturnCriteriaWithoutFilters() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ModuleSearchParams searchParams = ModuleSearchParams.of(cursorParams);

            // when
            ModuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.layerIds()).isNull();
        }

        @Test
        @DisplayName("성공 - 빈 LayerIds 리스트로 Criteria 생성")
        void createSliceCriteria_WithEmptyLayerIds_ShouldReturnCriteriaWithoutLayerIds() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ModuleSearchParams searchParams = ModuleSearchParams.of(cursorParams, List.of());

            // when
            ModuleSliceCriteria result = sut.createSliceCriteria(searchParams);

            // then
            assertThat(result).isNotNull();
            assertThat(result.layerIds()).isNull();
        }
    }
}
