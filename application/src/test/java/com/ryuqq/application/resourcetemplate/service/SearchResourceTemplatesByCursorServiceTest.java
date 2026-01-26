package com.ryuqq.application.resourcetemplate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.resourcetemplate.assembler.ResourceTemplateAssembler;
import com.ryuqq.application.resourcetemplate.dto.query.ResourceTemplateSearchParams;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import com.ryuqq.application.resourcetemplate.factory.query.ResourceTemplateQueryFactory;
import com.ryuqq.application.resourcetemplate.manager.ResourceTemplateReadManager;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SearchResourceTemplatesByCursorService 단위 테스트
 *
 * <p>ResourceTemplate 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchResourceTemplatesByCursorService 단위 테스트")
class SearchResourceTemplatesByCursorServiceTest {

    @Mock private ResourceTemplateQueryFactory resourceTemplateQueryFactory;

    @Mock private ResourceTemplateReadManager resourceTemplateReadManager;

    @Mock private ResourceTemplateAssembler resourceTemplateAssembler;

    @Mock private ResourceTemplateSliceCriteria criteria;

    @Mock private ResourceTemplate resourceTemplate;

    @Mock private ResourceTemplateSliceResult sliceResult;

    private SearchResourceTemplatesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchResourceTemplatesByCursorService(
                        resourceTemplateQueryFactory,
                        resourceTemplateReadManager,
                        resourceTemplateAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 ResourceTemplate 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            ResourceTemplateSearchParams searchParams = createDefaultSearchParams();
            List<ResourceTemplate> resourceTemplates = List.of(resourceTemplate);

            given(resourceTemplateQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(resourceTemplateReadManager.findBySliceCriteria(criteria))
                    .willReturn(resourceTemplates);
            given(resourceTemplateAssembler.toSliceResult(resourceTemplates, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ResourceTemplateSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(resourceTemplateQueryFactory).should().createSliceCriteria(searchParams);
            then(resourceTemplateReadManager).should().findBySliceCriteria(criteria);
            then(resourceTemplateAssembler)
                    .should()
                    .toSliceResult(resourceTemplates, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            ResourceTemplateSearchParams searchParams = createDefaultSearchParams();
            List<ResourceTemplate> emptyList = List.of();

            given(resourceTemplateQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(resourceTemplateReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(resourceTemplateAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ResourceTemplateSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(resourceTemplateQueryFactory).should().createSliceCriteria(searchParams);
            then(resourceTemplateReadManager).should().findBySliceCriteria(criteria);
            then(resourceTemplateAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private ResourceTemplateSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return ResourceTemplateSearchParams.of(cursorParams);
    }
}
