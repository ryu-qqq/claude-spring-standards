package com.ryuqq.application.configfiletemplate.dto.command;

/**
 * UpdateConfigFileTemplateCommand - ConfigFileTemplate 수정 Command DTO
 *
 * <p>ConfigFileTemplate 수정에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-003: 수정용은 Update{Domain}Command 네이밍.
 *
 * <p>CDTO-004: Update Command는 UpdateData 생성에 필요한 전체 필드 포함.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * @param id 수정 대상 ConfigFileTemplate ID
 * @param toolType 도구 타입 (CLAUDE, CURSOR, COPILOT 등)
 * @param filePath 파일 경로
 * @param fileName 파일명
 * @param content 파일 내용
 * @param category 카테고리
 * @param description 템플릿 설명
 * @param variables 치환 가능한 변수 정의 (JSON 문자열)
 * @param displayOrder 정렬 순서
 * @param isRequired 필수 파일 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdateConfigFileTemplateCommand(
        Long id,
        String toolType,
        String filePath,
        String fileName,
        String content,
        String category,
        String description,
        String variables,
        Integer displayOrder,
        Boolean isRequired) {}
