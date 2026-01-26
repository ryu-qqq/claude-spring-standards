package com.ryuqq.adapter.in.rest.archunittest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ArchUnitTestApiResponse - ArchUnitTest API Response DTO
 *
 * <p>ArchUnitTest 조회 결과를 API 응답으로 변환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param archUnitTestId ArchUnitTest ID
 * @param structureId 패키지 구조 ID
 * @param code 테스트 코드 식별자
 * @param name 테스트 이름
 * @param description 테스트 설명 (nullable)
 * @param testClassName 테스트 클래스 이름 (nullable)
 * @param testMethodName 테스트 메서드 이름 (nullable)
 * @param testCode 테스트 코드 내용
 * @param severity 심각도 (nullable)
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ArchUnitTest 응답")
public record ArchUnitTestApiResponse(
        @Schema(description = "ArchUnitTest ID", example = "1") Long archUnitTestId,
        @Schema(description = "패키지 구조 ID", example = "1") Long structureId,
        @Schema(description = "테스트 코드 식별자", example = "ARCH-001") String code,
        @Schema(description = "테스트 이름", example = "Domain Layer Dependency Test") String name,
        @Schema(description = "테스트 설명", example = "도메인 레이어 의존성 검증 테스트", nullable = true)
                String description,
        @Schema(description = "테스트 클래스 이름", example = "DomainLayerArchTest", nullable = true)
                String testClassName,
        @Schema(
                        description = "테스트 메서드 이름",
                        example = "domainShouldNotDependOnApplication",
                        nullable = true)
                String testMethodName,
        @Schema(
                        description = "테스트 코드 내용",
                        example = "@ArchTest void domainShouldNotDependOnApplication() { ... }")
                String testCode,
        @Schema(description = "심각도", example = "ERROR", nullable = true) String severity,
        @Schema(description = "생성 일시 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601)", example = "2024-01-15T10:30:00Z")
                String updatedAt) {}
