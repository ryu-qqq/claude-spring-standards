package com.ryuqq.adapter.in.rest.classtemplate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ClassTemplateApiResponse - ClassTemplate API Response DTO
 *
 * <p>ClassTemplate 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param classTemplateId 클래스 템플릿 ID
 * @param structureId 패키지 구조 ID
 * @param classTypeId 클래스 타입 ID
 * @param templateCode 템플릿 코드
 * @param namingPattern 네이밍 패턴 (nullable)
 * @param description 템플릿 설명 (nullable)
 * @param requiredAnnotations 필수 어노테이션 목록
 * @param forbiddenAnnotations 금지 어노테이션 목록
 * @param requiredInterfaces 필수 인터페이스 목록
 * @param forbiddenInheritance 금지 상속 목록
 * @param requiredMethods 필수 메서드 목록
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "클래스 템플릿 응답")
public record ClassTemplateApiResponse(
        @Schema(description = "클래스 템플릿 ID", example = "1") Long classTemplateId,
        @Schema(description = "패키지 구조 ID", example = "10") Long structureId,
        @Schema(description = "클래스 타입 ID", example = "1") Long classTypeId,
        @Schema(description = "템플릿 코드", example = "AGG-TMPL-001") String templateCode,
        @Schema(description = "네이밍 패턴", example = "*Service", nullable = true) String namingPattern,
        @Schema(description = "템플릿 설명", example = "Aggregate 클래스 템플릿", nullable = true)
                String description,
        @Schema(description = "필수 어노테이션 목록", example = "[\"@Entity\", \"@Table\"]")
                List<String> requiredAnnotations,
        @Schema(description = "금지 어노테이션 목록", example = "[\"@Data\", \"@Getter\"]")
                List<String> forbiddenAnnotations,
        @Schema(description = "필수 인터페이스 목록", example = "[\"Serializable\"]")
                List<String> requiredInterfaces,
        @Schema(description = "금지 상속 목록", example = "[\"BaseEntity\"]")
                List<String> forbiddenInheritance,
        @Schema(description = "필수 메서드 목록", example = "[\"validate\", \"toDomain\"]")
                List<String> requiredMethods,
        @Schema(description = "생성 일시", example = "2024-01-15T10:30:00Z") String createdAt,
        @Schema(description = "수정 일시", example = "2024-01-15T10:30:00Z") String updatedAt) {}
