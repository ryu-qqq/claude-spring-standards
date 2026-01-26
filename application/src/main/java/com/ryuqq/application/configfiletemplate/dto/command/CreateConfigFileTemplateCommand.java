package com.ryuqq.application.configfiletemplate.dto.command;

/**
 * CreateConfigFileTemplateCommand - ConfigFileTemplate 생성 Command DTO
 *
 * <p>ConfigFileTemplate 생성에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-002: 생성용은 Create{Domain}Command 네이밍.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * @param techStackId 기술 스택 ID (FK)
 * @param architectureId 아키텍처 ID (FK, nullable)
 * @param toolType 도구 타입 (CLAUDE, CURSOR, COPILOT 등)
 * @param filePath 파일 경로 (예: .claude/CLAUDE.md)
 * @param fileName 파일명 (예: CLAUDE.md)
 * @param content 파일 내용
 * @param category 카테고리 (MAIN_CONFIG, SKILL, RULE, AGENT, HOOK)
 * @param description 템플릿 설명
 * @param variables 치환 가능한 변수 정의 (JSON 문자열)
 * @param displayOrder 정렬 순서
 * @param isRequired 필수 파일 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CreateConfigFileTemplateCommand(
        Long techStackId,
        Long architectureId,
        String toolType,
        String filePath,
        String fileName,
        String content,
        String category,
        String description,
        String variables,
        Integer displayOrder,
        Boolean isRequired) {}
