package com.ryuqq.adapter.in.rest.codingrule.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * UpdateCodingRuleApiRequest - CodingRule 수정 API Request
 *
 * <p>CodingRule 수정 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param structureId 패키지 구조 ID (nullable)
 * @param code 규칙 코드 (예: AGG-001)
 * @param name 규칙 이름
 * @param severity 심각도 (BLOCKER, CRITICAL, MAJOR, MINOR, INFO)
 * @param category 카테고리 (ANNOTATION, BEHAVIOR, STRUCTURE 등)
 * @param description 규칙 설명
 * @param rationale 규칙 근거
 * @param autoFixable 자동 수정 가능 여부
 * @param appliesTo 적용 대상 목록
 * @param sdkConstraint SDK 제약 조건 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "CodingRule 수정 요청 DTO")
public record UpdateCodingRuleApiRequest(
        @Schema(description = "패키지 구조 ID", example = "1", nullable = true) Long structureId,
        @Schema(description = "규칙 코드", example = "AGG-001")
                @NotBlank(message = "code는 필수입니다")
                @Size(max = 50, message = "code는 50자 이내여야 합니다")
                String code,
        @Schema(description = "규칙 이름", example = "Lombok 사용 금지")
                @NotBlank(message = "name은 필수입니다")
                @Size(max = 200, message = "name은 200자 이내여야 합니다")
                String name,
        @Schema(description = "심각도", example = "BLOCKER") @NotBlank(message = "severity는 필수입니다")
                String severity,
        @Schema(description = "카테고리", example = "ANNOTATION") @NotBlank(message = "category는 필수입니다")
                String category,
        @Schema(description = "규칙 설명", example = "Domain Layer에서 Lombok 어노테이션 사용을 금지합니다.")
                @NotBlank(message = "description은 필수입니다")
                @Size(max = 2000, message = "description은 2000자 이내여야 합니다")
                String description,
        @Schema(
                        description = "규칙 근거",
                        example = "Lombok은 컴파일 타임 코드 생성으로 도메인 로직의 명확성을 해칩니다.",
                        nullable = true)
                @Size(max = 2000, message = "rationale은 2000자 이내여야 합니다")
                String rationale,
        @Schema(description = "자동 수정 가능 여부", example = "false")
                @NotNull(message = "autoFixable은 필수입니다")
                Boolean autoFixable,
        @Schema(
                        description = "적용 대상 목록",
                        example = "[\"Aggregate\", \"ValueObject\"]",
                        nullable = true)
                List<String> appliesTo,
        @Schema(description = "SDK 제약 조건", nullable = true) SdkConstraintRequest sdkConstraint) {

    /**
     * SdkConstraintRequest - SDK 제약 조건 요청 DTO
     *
     * @param artifact SDK 아티팩트 (예: org.springframework.boot:spring-boot-starter)
     * @param minVersion 최소 버전 (nullable)
     * @param maxVersion 최대 버전 (nullable)
     */
    @Schema(description = "SDK 제약 조건 요청 DTO")
    public record SdkConstraintRequest(
            @Schema(
                            description = "SDK 아티팩트",
                            example = "org.springframework.boot:spring-boot-starter")
                    String artifact,
            @Schema(description = "최소 버전", example = "3.0.0", nullable = true) String minVersion,
            @Schema(description = "최대 버전", example = "4.0.0", nullable = true) String maxVersion) {}
}
