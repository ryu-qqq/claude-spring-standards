package com.ryuqq.application.mcp.port.in;

import com.ryuqq.application.mcp.dto.query.PlanningContextQuery;
import com.ryuqq.application.mcp.dto.response.PlanningContextResult;

/**
 * GetPlanningContextUseCase - Planning Context 조회 UseCase
 *
 * <p>개발 계획 수립에 필요한 컨텍스트를 조회합니다.
 *
 * <p>UC-001: UseCase는 Interface로 정의.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface GetPlanningContextUseCase {

    /**
     * Planning Context 조회
     *
     * @param query Planning Context 조회 쿼리
     * @return Planning Context 결과
     */
    PlanningContextResult execute(PlanningContextQuery query);
}
