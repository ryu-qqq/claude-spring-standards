package com.ryuqq.adapter.in.rest.resourcetemplate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * UpdateResourceTemplateApiRequest - ResourceTemplate 수정 API Request
 *
 * <p>ResourceTemplate 수정 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param category 카테고리 (필수)
 * @param filePath 파일 경로 (필수)
 * @param fileType 파일 타입 (필수)
 * @param description 설명 (필수)
 * @param templateContent 템플릿 콘텐츠 (필수)
 * @param required 필수 여부 (필수)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ResourceTemplate 수정 요청")
public record UpdateResourceTemplateApiRequest(
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
        @Schema(description = "설명", example = "주문 도메인 엔티티 템플릿")
                @NotNull(message = "description은 필수입니다")
                @Size(max = 2000, message = "description은 2000자 이내여야 합니다")
                String description,
        @Schema(description = "템플릿 콘텐츠", example = "public class Order { ... }")
                @NotNull(message = "templateContent는 필수입니다")
                @Size(max = 50000, message = "templateContent는 50000자 이내여야 합니다")
                String templateContent,
        @Schema(description = "필수 여부", example = "true") @NotNull(message = "required는 필수입니다")
                Boolean required) {}
