package com.ryuqq.application.zerotolerance.service;

import com.ryuqq.application.zerotolerance.dto.query.ZeroToleranceRuleSearchParams;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.application.zerotolerance.factory.query.ZeroToleranceRuleQueryFactory;
import com.ryuqq.application.zerotolerance.port.in.SearchZeroToleranceRulesByCursorUseCase;
import com.ryuqq.application.zerotolerance.port.out.ZeroToleranceRuleQueryPort;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
import org.springframework.stereotype.Service;

/**
 * SearchZeroToleranceRulesByCursorService - Zero-Tolerance 규칙 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchZeroToleranceRulesByCursorUseCase를 구현합니다.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class SearchZeroToleranceRulesByCursorService
        implements SearchZeroToleranceRulesByCursorUseCase {

    private final ZeroToleranceRuleQueryFactory zeroToleranceRuleQueryFactory;
    private final ZeroToleranceRuleQueryPort zeroToleranceRuleQueryPort;

    public SearchZeroToleranceRulesByCursorService(
            ZeroToleranceRuleQueryFactory zeroToleranceRuleQueryFactory,
            ZeroToleranceRuleQueryPort zeroToleranceRuleQueryPort) {
        this.zeroToleranceRuleQueryFactory = zeroToleranceRuleQueryFactory;
        this.zeroToleranceRuleQueryPort = zeroToleranceRuleQueryPort;
    }

    @Override
    public ZeroToleranceRuleSliceResult execute(ZeroToleranceRuleSearchParams searchParams) {
        ZeroToleranceRuleSliceCriteria criteria =
                zeroToleranceRuleQueryFactory.createSliceCriteria(searchParams);
        return zeroToleranceRuleQueryPort.findAllDetails(criteria);
    }
}
