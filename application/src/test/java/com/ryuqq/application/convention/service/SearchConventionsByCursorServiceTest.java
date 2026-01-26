package com.ryuqq.application.convention.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.convention.assembler.ConventionAssembler;
import com.ryuqq.application.convention.dto.query.ConventionSearchParams;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import com.ryuqq.application.convention.factory.query.ConventionQueryFactory;
import com.ryuqq.application.convention.manager.ConventionReadManager;
import com.ryuqq.domain.convention.aggregate.Convention;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
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
 * SearchConventionsByCursorService 단위 테스트
 *
 * <p>Convention 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchConventionsByCursorService 단위 테스트")
class SearchConventionsByCursorServiceTest {

    @Mock private ConventionReadManager conventionReadManager;

    @Mock private ConventionQueryFactory conventionQueryFactory;

    @Mock private ConventionAssembler conventionAssembler;

    @Mock private ConventionSliceCriteria criteria;

    @Mock private Convention convention;

    @Mock private ConventionSliceResult sliceResult;

    private SearchConventionsByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchConventionsByCursorService(
                        conventionReadManager, conventionQueryFactory, conventionAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 Convention 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            ConventionSearchParams searchParams = createDefaultSearchParams();
            List<Convention> conventions = List.of(convention);

            given(conventionQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(conventionReadManager.findBySliceCriteria(criteria)).willReturn(conventions);
            given(conventionAssembler.toSliceResult(conventions, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ConventionSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(conventionQueryFactory).should().createSliceCriteria(searchParams);
            then(conventionReadManager).should().findBySliceCriteria(criteria);
            then(conventionAssembler).should().toSliceResult(conventions, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            ConventionSearchParams searchParams = createDefaultSearchParams();
            List<Convention> emptyList = List.of();

            given(conventionQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(conventionReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(conventionAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ConventionSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(conventionQueryFactory).should().createSliceCriteria(searchParams);
            then(conventionReadManager).should().findBySliceCriteria(criteria);
            then(conventionAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private ConventionSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return ConventionSearchParams.of(cursorParams);
    }
}
