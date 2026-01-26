package com.ryuqq.application.mcp.dto.query;

import java.util.List;

/**
 * GetConfigFilesQuery - MCP Config Files 조회 쿼리
 *
 * <p>init_project Tool에서 사용할 설정 파일 템플릿 조회 쿼리입니다.
 *
 * <p>DTO-001: Record 필수.
 *
 * @param toolTypes 도구 타입 목록 (예: CLAUDE, CURSOR, COPILOT)
 * @param techStackId 기술 스택 ID
 * @param architectureId 아키텍처 ID (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record GetConfigFilesQuery(List<String> toolTypes, Long techStackId, Long architectureId) {}
