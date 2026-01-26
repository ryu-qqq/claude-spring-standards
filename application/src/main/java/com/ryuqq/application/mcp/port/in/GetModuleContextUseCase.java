package com.ryuqq.application.mcp.port.in;

import com.ryuqq.application.mcp.dto.query.ModuleContextQuery;
import com.ryuqq.application.mcp.dto.response.ModuleContextResult;

/**
 * GetModuleContextUseCase - Module Context 조회 UseCase
 *
 * <p>코드 생성에 필요한 Module 전체 컨텍스트를 조회합니다.
 *
 * <p>UC-001: UseCase는 Interface로 정의.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface GetModuleContextUseCase {

    /**
     * Module Context 조회
     *
     * @param query Module Context 조회 쿼리
     * @return Module Context 결과
     */
    ModuleContextResult execute(ModuleContextQuery query);
}
