package com.ryuqq.application.archunittest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.archunittest.assembler.ArchUnitTestAssembler;
import com.ryuqq.application.archunittest.dto.query.ArchUnitTestSearchParams;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestSliceResult;
import com.ryuqq.application.archunittest.factory.query.ArchUnitTestQueryFactory;
import com.ryuqq.application.archunittest.manager.ArchUnitTestReadManager;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
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
 * SearchArchUnitTestsByCursorService 단위 테스트
 *
 * <p>ArchUnitTest 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchArchUnitTestsByCursorService 단위 테스트")
class SearchArchUnitTestsByCursorServiceTest {

    @Mock private ArchUnitTestQueryFactory archUnitTestQueryFactory;

    @Mock private ArchUnitTestReadManager archUnitTestReadManager;

    @Mock private ArchUnitTestAssembler archUnitTestAssembler;

    @Mock private ArchUnitTestSliceCriteria criteria;

    @Mock private ArchUnitTest archUnitTest;

    @Mock private ArchUnitTestSliceResult sliceResult;

    private SearchArchUnitTestsByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchArchUnitTestsByCursorService(
                        archUnitTestQueryFactory, archUnitTestReadManager, archUnitTestAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 ArchUnitTest 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            ArchUnitTestSearchParams searchParams = createDefaultSearchParams();
            List<ArchUnitTest> archUnitTests = List.of(archUnitTest);

            given(archUnitTestQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(archUnitTestReadManager.findBySliceCriteria(criteria)).willReturn(archUnitTests);
            given(archUnitTestAssembler.toSliceResult(archUnitTests, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ArchUnitTestSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(archUnitTestQueryFactory).should().createSliceCriteria(searchParams);
            then(archUnitTestReadManager).should().findBySliceCriteria(criteria);
            then(archUnitTestAssembler).should().toSliceResult(archUnitTests, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            ArchUnitTestSearchParams searchParams = createDefaultSearchParams();
            List<ArchUnitTest> emptyList = List.of();

            given(archUnitTestQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(archUnitTestReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(archUnitTestAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ArchUnitTestSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(archUnitTestQueryFactory).should().createSliceCriteria(searchParams);
            then(archUnitTestReadManager).should().findBySliceCriteria(criteria);
            then(archUnitTestAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private ArchUnitTestSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return ArchUnitTestSearchParams.of(cursorParams);
    }
}
