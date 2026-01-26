package com.ryuqq.application.resourcetemplate.dto.response;

import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import java.time.Instant;

/**
 * ResourceTemplateResult - 리소스 템플릿 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * @param id 리소스 템플릿 ID
 * @param moduleId 모듈 ID
 * @param category 카테고리
 * @param filePath 파일 경로
 * @param fileType 파일 타입
 * @param description 설명 (nullable)
 * @param templateContent 템플릿 내용 (nullable)
 * @param required 필수 여부
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ResourceTemplateResult(
        Long id,
        Long moduleId,
        String category,
        String filePath,
        String fileType,
        String description,
        String templateContent,
        boolean required,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * @param resourceTemplate ResourceTemplate 도메인 객체
     * @return ResourceTemplateResult
     */
    public static ResourceTemplateResult from(ResourceTemplate resourceTemplate) {
        return new ResourceTemplateResult(
                resourceTemplate.id().value(),
                resourceTemplate.moduleId().value(),
                resourceTemplate.category().name(),
                resourceTemplate.filePath().value(),
                resourceTemplate.fileType().name(),
                resourceTemplate.description(),
                resourceTemplate.templateContent().value(),
                resourceTemplate.required(),
                resourceTemplate.createdAt(),
                resourceTemplate.updatedAt());
    }
}
