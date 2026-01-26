package com.ryuqq.application.mcp.dto.response;

import java.util.List;

/**
 * ConfigFilesResult - MCP Config Files 조회 결과
 *
 * <p>init_project Tool 응답용 설정 파일 목록 결과입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param configFiles 설정 파일 템플릿 목록
 * @param totalCount 전체 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConfigFilesResult(List<ConfigFileResult> configFiles, int totalCount) {}
