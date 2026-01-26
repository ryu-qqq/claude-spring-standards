package com.ryuqq.application.resourcetemplate.dto.command;

/**
 * CreateResourceTemplateCommand - 리소스 템플릿 생성 커맨드
 *
 * <p>리소스 템플릿 생성에 필요한 데이터를 전달합니다.
 *
 * @param moduleId 모듈 ID
 * @param category 카테고리 (CONFIG/I18N/STATIC/BUILD)
 * @param filePath 파일 경로
 * @param fileType 파일 타입 (JAVA/KOTLIN/XML/YAML 등)
 * @param description 설명 (nullable)
 * @param templateContent 템플릿 내용 (nullable)
 * @param required 필수 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CreateResourceTemplateCommand(
        Long moduleId,
        String category,
        String filePath,
        String fileType,
        String description,
        String templateContent,
        boolean required) {}
