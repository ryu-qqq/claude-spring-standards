package com.ryuqq.application.resourcetemplate.dto.command;

/**
 * UpdateResourceTemplateCommand - 리소스 템플릿 수정 커맨드
 *
 * <p>리소스 템플릿 수정에 필요한 데이터를 전달합니다.
 *
 * @param resourceTemplateId 수정할 리소스 템플릿 ID
 * @param category 카테고리 (nullable)
 * @param filePath 파일 경로 (nullable)
 * @param fileType 파일 타입 (nullable)
 * @param description 설명 (nullable)
 * @param templateContent 템플릿 내용 (nullable)
 * @param required 필수 여부 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdateResourceTemplateCommand(
        Long resourceTemplateId,
        String category,
        String filePath,
        String fileType,
        String description,
        String templateContent,
        Boolean required) {}
