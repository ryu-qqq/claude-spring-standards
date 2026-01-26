package com.ryuqq.application.mcp.port.in;

import com.ryuqq.application.mcp.dto.query.ValidationContextQuery;
import com.ryuqq.application.mcp.dto.response.ValidationContextResult;

/**
 * GetValidationContextUseCase - Validation Context 조회 UseCase
 *
 * <p>코드 검증에 필요한 Zero-Tolerance + Checklist를 조회합니다.
 *
 * <p>UC-001: UseCase는 Interface로 정의.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface GetValidationContextUseCase {

    /**
     * Validation Context 조회
     *
     * @param query Validation Context 조회 쿼리
     * @return Validation Context 결과
     */
    ValidationContextResult execute(ValidationContextQuery query);
}
