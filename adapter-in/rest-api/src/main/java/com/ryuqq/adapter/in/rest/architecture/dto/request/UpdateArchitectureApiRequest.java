package com.ryuqq.adapter.in.rest.architecture.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * UpdateArchitectureApiRequest - Architecture 수정 API Request
 *
 * <p>Architecture 수정 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param name 아키텍처 이름
 * @param patternType 패턴 타입 (HEXAGONAL, LAYERED, CLEAN, ONION, MICROSERVICES, MODULAR_MONOLITH,
 *     EVENT_DRIVEN)
 * @param patternDescription 패턴 설명 (nullable)
 * @param patternPrinciples 패턴 원칙 목록 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Architecture 수정 요청 DTO")
public record UpdateArchitectureApiRequest(
        @Schema(description = "아키텍처 이름", example = "Hexagonal Architecture")
                @NotBlank(message = "name은 필수입니다")
                @Size(max = 100, message = "name은 100자 이내여야 합니다")
                String name,
        @Schema(description = "패턴 타입", example = "HEXAGONAL")
                @NotBlank(message = "patternType은 필수입니다")
                String patternType,
        @Schema(description = "패턴 설명", example = "포트와 어댑터 패턴 기반의 아키텍처", nullable = true)
                String patternDescription,
        @Schema(
                        description = "패턴 원칙 목록",
                        example = "[\"Dependency Inversion\", \"Single Responsibility\"]",
                        nullable = true)
                List<String> patternPrinciples,
        @Schema(
                        description = "참조 링크 목록",
                        example = "[\"https://alistair.cockburn.us/hexagonal-architecture\"]",
                        nullable = true)
                List<String> referenceLinks) {}
