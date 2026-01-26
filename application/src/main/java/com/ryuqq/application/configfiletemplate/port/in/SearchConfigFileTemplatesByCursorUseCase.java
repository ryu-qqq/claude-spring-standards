package com.ryuqq.application.configfiletemplate.port.in;

import com.ryuqq.application.configfiletemplate.dto.query.ConfigFileTemplateSearchParams;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateSliceResult;

/**
 * SearchConfigFileTemplatesByCursorUseCase - ConfigFileTemplate 복합 조건 조회 UseCase (커서 기반)
 *
 * <p>ConfigFileTemplate 목록을 커서 기반으로 조회합니다.
 *
 * <p>UC-001: UseCase는 반드시 interface로 정의.
 *
 * <p>UC-002: UseCase는 execute() 단일 메서드만 제공.
 *
 * <p>UC-007: Query UseCase는 조회 접두어 + UseCase 네이밍.
 *
 * <p>RDTO-009: List 직접 반환 금지 → SliceResult 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface SearchConfigFileTemplatesByCursorUseCase {

    /**
     * ConfigFileTemplate 복합 조건 조회 실행 (커서 기반)
     *
     * @param searchParams 조회 SearchParams DTO
     * @return ConfigFileTemplate 슬라이스 결과
     */
    ConfigFileTemplateSliceResult execute(ConfigFileTemplateSearchParams searchParams);
}
