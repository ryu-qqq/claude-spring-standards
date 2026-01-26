package com.ryuqq.adapter.in.rest.resourcetemplate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CreateResourceTemplateApiRequest - ResourceTemplate 생성 API Request
 *
 * <p>ResourceTemplate 생성 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>DTO-003: *ApiRequest 네이밍.
 *
 * @param moduleId 모듈 ID (필수)
 * @param category 카테고리 (필수, 예: DOMAIN, APPLICATION, PERSISTENCE, REST_API)
 * @param filePath 파일 경로 (필수)
 * @param fileType 파일 타입 (필수, 예: JAVA, KOTLIN, YAML)
 * @param description 설명 (nullable)
 * @param templateContent 템플릿 콘텐츠 (nullable)
 * @param required 필수 여부 (nullable, 기본값: true)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ResourceTemplate 생성 요청")
public record CreateResourceTemplateApiRequest(
        @Schema(description = "모듈 ID", example = "1") @NotNull(message = "moduleId는 필수입니다")
                Long moduleId,
        @Schema(
                        description = "카테고리 (DOMAIN, APPLICATION, PERSISTENCE, REST_API)",
                        example = "DOMAIN")
                @NotBlank(message = "category는 필수입니다")
                @Size(max = 50, message = "category는 50자 이내여야 합니다")
                String category,
        @Schema(description = "파일 경로", example = "src/main/java/com/example/domain/Order.java")
                @NotBlank(message = "filePath는 필수입니다")
                @Size(max = 500, message = "filePath는 500자 이내여야 합니다")
                String filePath,
        @Schema(description = "파일 타입 (JAVA, KOTLIN, YAML)", example = "JAVA")
                @NotBlank(message = "fileType은 필수입니다")
                @Size(max = 30, message = "fileType은 30자 이내여야 합니다")
                String fileType,
        @Schema(description = "설명", example = "주문 도메인 엔티티 템플릿", nullable = true)
                @Size(max = 2000, message = "description은 2000자 이내여야 합니다")
                String description,
        @Schema(description = "템플릿 콘텐츠", example = "public class Order { ... }", nullable = true)
                @Size(max = 50000, message = "templateContent는 50000자 이내여야 합니다")
                String templateContent,
        @Schema(description = "필수 여부", example = "true", nullable = true) Boolean required) {}
