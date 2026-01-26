package com.ryuqq.application.module.port.in;

import com.ryuqq.application.module.dto.query.ModuleSearchParams;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;

/**
 * SearchModulesByCursorUseCase - Module 복합 조건 조회 UseCase (커서 기반)
 *
 * <p>Module 목록을 커서 기반으로 복합 조건(layerId)으로 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface SearchModulesByCursorUseCase {

    /**
     * Module 목록 조회
     *
     * @param searchParams 조회 파라미터
     * @return Module 슬라이스 결과
     */
    ModuleSliceResult execute(ModuleSearchParams searchParams);
}
