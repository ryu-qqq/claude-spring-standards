package com.ryuqq.application.zerotolerance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.zerotolerance.dto.query.ZeroToleranceRuleSearchParams;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.application.zerotolerance.factory.query.ZeroToleranceRuleQueryFactory;
import com.ryuqq.application.zerotolerance.port.out.ZeroToleranceRuleQueryPort;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SearchZeroToleranceRulesByCursorService 단위 테스트
 *
 * <p>ZeroToleranceRule 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchZeroToleranceRulesByCursorService 단위 테스트")
class SearchZeroToleranceRulesByCursorServiceTest {

    @Mock private ZeroToleranceRuleQueryFactory zeroToleranceRuleQueryFactory;

    @Mock private ZeroToleranceRuleQueryPort zeroToleranceRuleQueryPort;

    @Mock private ZeroToleranceRuleSliceCriteria criteria;

    @Mock private ZeroToleranceRuleSliceResult sliceResult;

    private SearchZeroToleranceRulesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchZeroToleranceRulesByCursorService(
                        zeroToleranceRuleQueryFactory, zeroToleranceRuleQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 ZeroToleranceRule 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            ZeroToleranceRuleSearchParams searchParams = createDefaultSearchParams();

            given(zeroToleranceRuleQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(zeroToleranceRuleQueryPort.findAllDetails(criteria)).willReturn(sliceResult);

            // when
            ZeroToleranceRuleSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(zeroToleranceRuleQueryFactory).should().createSliceCriteria(searchParams);
            then(zeroToleranceRuleQueryPort).should().findAllDetails(criteria);
        }
    }

    private ZeroToleranceRuleSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return ZeroToleranceRuleSearchParams.of(cursorParams);
    }
}
