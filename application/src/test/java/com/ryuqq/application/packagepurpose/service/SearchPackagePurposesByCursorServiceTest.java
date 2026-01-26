package com.ryuqq.application.packagepurpose.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.packagepurpose.assembler.PackagePurposeAssembler;
import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import com.ryuqq.application.packagepurpose.factory.query.PackagePurposeQueryFactory;
import com.ryuqq.application.packagepurpose.manager.PackagePurposeReadManager;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
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
 * SearchPackagePurposesByCursorService 단위 테스트
 *
 * <p>PackagePurpose 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchPackagePurposesByCursorService 단위 테스트")
class SearchPackagePurposesByCursorServiceTest {

    @Mock private PackagePurposeReadManager packagePurposeReadManager;

    @Mock private PackagePurposeQueryFactory packagePurposeQueryFactory;

    @Mock private PackagePurposeAssembler packagePurposeAssembler;

    @Mock private PackagePurposeSliceCriteria criteria;

    @Mock private PackagePurpose packagePurpose;

    @Mock private PackagePurposeSliceResult sliceResult;

    private SearchPackagePurposesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchPackagePurposesByCursorService(
                        packagePurposeReadManager,
                        packagePurposeQueryFactory,
                        packagePurposeAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 PackagePurpose 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            PackagePurposeSearchParams searchParams = createDefaultSearchParams();
            List<PackagePurpose> packagePurposes = List.of(packagePurpose);

            given(packagePurposeQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(packagePurposeReadManager.findBySliceCriteria(criteria))
                    .willReturn(packagePurposes);
            given(packagePurposeAssembler.toSliceResult(packagePurposes, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            PackagePurposeSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(packagePurposeQueryFactory).should().createSliceCriteria(searchParams);
            then(packagePurposeReadManager).should().findBySliceCriteria(criteria);
            then(packagePurposeAssembler)
                    .should()
                    .toSliceResult(packagePurposes, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            PackagePurposeSearchParams searchParams = createDefaultSearchParams();
            List<PackagePurpose> emptyList = List.of();

            given(packagePurposeQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(packagePurposeReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(packagePurposeAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            PackagePurposeSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(packagePurposeQueryFactory).should().createSliceCriteria(searchParams);
            then(packagePurposeReadManager).should().findBySliceCriteria(criteria);
            then(packagePurposeAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private PackagePurposeSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return PackagePurposeSearchParams.of(cursorParams);
    }
}
