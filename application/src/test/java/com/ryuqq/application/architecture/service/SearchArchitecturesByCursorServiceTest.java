package com.ryuqq.application.architecture.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.architecture.assembler.ArchitectureAssembler;
import com.ryuqq.application.architecture.dto.query.ArchitectureSearchParams;
import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import com.ryuqq.application.architecture.factory.query.ArchitectureQueryFactory;
import com.ryuqq.application.architecture.manager.ArchitectureReadManager;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
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
 * SearchArchitecturesByCursorService 단위 테스트
 *
 * <p>Architecture 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchArchitecturesByCursorService 단위 테스트")
class SearchArchitecturesByCursorServiceTest {

    @Mock private ArchitectureReadManager architectureReadManager;

    @Mock private ArchitectureQueryFactory architectureQueryFactory;

    @Mock private ArchitectureAssembler architectureAssembler;

    @Mock private ArchitectureSliceCriteria criteria;

    @Mock private Architecture architecture;

    @Mock private ArchitectureSliceResult sliceResult;

    private SearchArchitecturesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchArchitecturesByCursorService(
                        architectureReadManager, architectureQueryFactory, architectureAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 Architecture 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            ArchitectureSearchParams searchParams = createDefaultSearchParams();
            List<Architecture> architectures = List.of(architecture);

            given(architectureQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(architectureReadManager.findBySliceCriteria(criteria)).willReturn(architectures);
            given(architectureAssembler.toSliceResult(architectures, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ArchitectureSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(architectureQueryFactory).should().createSliceCriteria(searchParams);
            then(architectureReadManager).should().findBySliceCriteria(criteria);
            then(architectureAssembler).should().toSliceResult(architectures, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            ArchitectureSearchParams searchParams = createDefaultSearchParams();
            List<Architecture> emptyList = List.of();

            given(architectureQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(architectureReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(architectureAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ArchitectureSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(architectureQueryFactory).should().createSliceCriteria(searchParams);
            then(architectureReadManager).should().findBySliceCriteria(criteria);
            then(architectureAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private ArchitectureSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return ArchitectureSearchParams.of(cursorParams);
    }
}
