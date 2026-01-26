package com.ryuqq.adapter.in.rest.classtemplate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * UpdateClassTemplateApiRequest - ClassTemplate 수정 API Request
 *
 * <p>ClassTemplate 수정 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param classTypeId 클래스 타입 ID
 * @param templateCode 템플릿 코드
 * @param namingPattern 네이밍 패턴 (예: *Service, *Repository)
 * @param description 템플릿 설명
 * @param requiredAnnotations 필수 어노테이션 목록
 * @param forbiddenAnnotations 금지 어노테이션 목록
 * @param requiredInterfaces 필수 인터페이스 목록
 * @param forbiddenInheritance 금지 상속 목록
 * @param requiredMethods 필수 메서드 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "클래스 템플릿 수정 요청")
public record UpdateClassTemplateApiRequest(
        @Schema(description = "클래스 타입 ID", example = "1") @NotNull(message = "classTypeId는 필수입니다")
                Long classTypeId,
        @Schema(description = "템플릿 코드", example = "AGG-TMPL-001")
                @NotBlank(message = "templateCode는 필수입니다")
                @Size(max = 100, message = "templateCode는 100자 이내여야 합니다")
                String templateCode,
        @Schema(description = "네이밍 패턴", example = "*Service", nullable = true)
                @Size(max = 200, message = "namingPattern은 200자 이내여야 합니다")
                String namingPattern,
        @Schema(description = "템플릿 설명", example = "Aggregate 클래스 템플릿", nullable = true)
                @Size(max = 2000, message = "description은 2000자 이내여야 합니다")
                String description,
        @Schema(description = "필수 어노테이션 목록", example = "[\"@Entity\", \"@Table\"]", nullable = true)
                List<String> requiredAnnotations,
        @Schema(description = "금지 어노테이션 목록", example = "[\"@Data\", \"@Getter\"]", nullable = true)
                List<String> forbiddenAnnotations,
        @Schema(description = "필수 인터페이스 목록", example = "[\"Serializable\"]", nullable = true)
                List<String> requiredInterfaces,
        @Schema(description = "금지 상속 목록", example = "[\"BaseEntity\"]", nullable = true)
                List<String> forbiddenInheritance,
        @Schema(
                        description = "필수 메서드 목록",
                        example = "[\"validate\", \"toDomain\"]",
                        nullable = true)
                List<String> requiredMethods) {}
