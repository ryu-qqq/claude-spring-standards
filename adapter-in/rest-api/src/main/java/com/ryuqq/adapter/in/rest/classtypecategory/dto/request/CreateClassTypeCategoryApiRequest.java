package com.ryuqq.adapter.in.rest.classtypecategory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CreateClassTypeCategoryApiRequest - ClassTypeCategory 생성 API Request
 *
 * <p>ClassTypeCategory 생성 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param architectureId 아키텍처 ID
 * @param code 카테고리 코드
 * @param name 카테고리 이름
 * @param description 카테고리 설명 (nullable)
 * @param orderIndex 정렬 순서
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ClassTypeCategory 생성 요청 DTO")
public record CreateClassTypeCategoryApiRequest(
        @Schema(description = "아키텍처 ID", example = "1") @NotNull(message = "architectureId는 필수입니다")
                Long architectureId,
        @Schema(description = "카테고리 코드", example = "DOMAIN_TYPES")
                @NotBlank(message = "code는 필수입니다")
                @Size(max = 50, message = "code는 50자 이내여야 합니다")
                String code,
        @Schema(description = "카테고리 이름", example = "도메인 타입")
                @NotBlank(message = "name은 필수입니다")
                @Size(max = 100, message = "name은 100자 이내여야 합니다")
                String name,
        @Schema(description = "카테고리 설명", example = "도메인 레이어 클래스 타입", nullable = true)
                String description,
        @Schema(description = "정렬 순서", example = "1")
                @NotNull(message = "orderIndex는 필수입니다")
                @Min(value = 0, message = "orderIndex는 0 이상이어야 합니다")
                Integer orderIndex) {}
