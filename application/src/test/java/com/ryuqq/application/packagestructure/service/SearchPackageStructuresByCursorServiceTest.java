package com.ryuqq.application.packagestructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.packagestructure.assembler.PackageStructureAssembler;
import com.ryuqq.application.packagestructure.dto.query.PackageStructureSearchParams;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureSliceResult;
import com.ryuqq.application.packagestructure.factory.query.PackageStructureQueryFactory;
import com.ryuqq.application.packagestructure.manager.PackageStructureReadManager;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
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
 * SearchPackageStructuresByCursorService 단위 테스트
 *
 * <p>PackageStructure 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchPackageStructuresByCursorService 단위 테스트")
class SearchPackageStructuresByCursorServiceTest {

    @Mock private PackageStructureQueryFactory packageStructureQueryFactory;

    @Mock private PackageStructureReadManager packageStructureReadManager;

    @Mock private PackageStructureAssembler packageStructureAssembler;

    @Mock private PackageStructureSliceCriteria criteria;

    @Mock private PackageStructure packageStructure;

    @Mock private PackageStructureSliceResult sliceResult;

    private SearchPackageStructuresByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchPackageStructuresByCursorService(
                        packageStructureQueryFactory,
                        packageStructureReadManager,
                        packageStructureAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 PackageStructure 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            PackageStructureSearchParams searchParams = createDefaultSearchParams();
            List<PackageStructure> packageStructures = List.of(packageStructure);

            given(packageStructureQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(packageStructureReadManager.findBySliceCriteria(criteria))
                    .willReturn(packageStructures);
            given(packageStructureAssembler.toSliceResult(packageStructures, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            PackageStructureSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(packageStructureQueryFactory).should().createSliceCriteria(searchParams);
            then(packageStructureReadManager).should().findBySliceCriteria(criteria);
            then(packageStructureAssembler)
                    .should()
                    .toSliceResult(packageStructures, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            PackageStructureSearchParams searchParams = createDefaultSearchParams();
            List<PackageStructure> emptyList = List.of();

            given(packageStructureQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(packageStructureReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(packageStructureAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            PackageStructureSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(packageStructureQueryFactory).should().createSliceCriteria(searchParams);
            then(packageStructureReadManager).should().findBySliceCriteria(criteria);
            then(packageStructureAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private PackageStructureSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return PackageStructureSearchParams.of(cursorParams);
    }
}
