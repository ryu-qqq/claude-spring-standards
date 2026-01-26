package com.ryuqq.application.mcp.port.in;

import com.ryuqq.application.mcp.dto.query.GetConfigFilesQuery;
import com.ryuqq.application.mcp.dto.response.ConfigFilesResult;

/**
 * GetConfigFilesForMcpUseCase - MCP init_project용 Config Files 조회 UseCase
 *
 * <p>MCP Tool에서 설정 파일 템플릿을 조회할 때 사용하는 Port-In 인터페이스입니다.
 *
 * <p>PRT-001: Port-In은 UseCase 인터페이스.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface GetConfigFilesForMcpUseCase {

    /**
     * 설정 파일 템플릿 목록 조회
     *
     * @param query 조회 조건 (도구 타입, 기술 스택, 아키텍처)
     * @return 설정 파일 템플릿 목록
     */
    ConfigFilesResult execute(GetConfigFilesQuery query);
}
