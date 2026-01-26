package com.ryuqq.application.techstack.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.techstack.assembler.TechStackAssembler;
import com.ryuqq.application.techstack.dto.query.TechStackSearchParams;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import com.ryuqq.application.techstack.factory.query.TechStackQueryFactory;
import com.ryuqq.application.techstack.manager.TechStackReadManager;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
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
 * SearchTechStacksByCursorService 단위 테스트
 *
 * <p>TechStack 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchTechStacksByCursorService 단위 테스트")
class SearchTechStacksByCursorServiceTest {

    @Mock private TechStackReadManager techStackReadManager;

    @Mock private TechStackQueryFactory techStackQueryFactory;

    @Mock private TechStackAssembler techStackAssembler;

    @Mock private TechStackSliceCriteria criteria;

    @Mock private TechStack techStack;

    @Mock private TechStackSliceResult sliceResult;

    private SearchTechStacksByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchTechStacksByCursorService(
                        techStackReadManager, techStackQueryFactory, techStackAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 TechStack 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            TechStackSearchParams searchParams = createDefaultSearchParams();
            List<TechStack> techStacks = List.of(techStack);

            given(techStackQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(techStackReadManager.findBySliceCriteria(criteria)).willReturn(techStacks);
            given(techStackAssembler.toSliceResult(techStacks, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            TechStackSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(techStackQueryFactory).should().createSliceCriteria(searchParams);
            then(techStackReadManager).should().findBySliceCriteria(criteria);
            then(techStackAssembler).should().toSliceResult(techStacks, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            TechStackSearchParams searchParams = createDefaultSearchParams();
            List<TechStack> emptyList = List.of();

            given(techStackQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(techStackReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(techStackAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            TechStackSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(techStackQueryFactory).should().createSliceCriteria(searchParams);
            then(techStackReadManager).should().findBySliceCriteria(criteria);
            then(techStackAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private TechStackSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return TechStackSearchParams.of(cursorParams);
    }
}
