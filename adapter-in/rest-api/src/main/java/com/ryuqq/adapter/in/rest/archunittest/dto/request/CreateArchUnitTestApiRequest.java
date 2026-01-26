package com.ryuqq.adapter.in.rest.archunittest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CreateArchUnitTestApiRequest - ArchUnitTest 생성 API Request
 *
 * <p>ArchUnitTest 생성 REST API 요청 DTO입니다.
 *
 * <p>DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>DTO-002: Request DTO @NotNull 필수 (Nullable 금지).
 *
 * <p>DTO-003: *ApiRequest 네이밍.
 *
 * @param structureId 패키지 구조 ID (필수)
 * @param code 테스트 코드 식별자 (예: ARCH-001)
 * @param name 테스트 이름
 * @param description 테스트 설명
 * @param testClassName 테스트 클래스 이름
 * @param testMethodName 테스트 메서드 이름
 * @param testCode 테스트 코드 내용
 * @param severity 심각도 (BLOCKER, CRITICAL, MAJOR, MINOR, INFO)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ArchUnitTest 생성 요청")
public record CreateArchUnitTestApiRequest(
        @Schema(description = "패키지 구조 ID", example = "1") @NotNull(message = "structureId는 필수입니다")
                Long structureId,
        @Schema(description = "테스트 코드 식별자", example = "ARCH-001")
                @NotBlank(message = "code는 필수입니다")
                @Size(max = 50, message = "code는 50자 이내여야 합니다")
                String code,
        @Schema(description = "테스트 이름", example = "Domain 레이어는 외부 의존성을 가지면 안됨")
                @NotBlank(message = "name은 필수입니다")
                @Size(max = 200, message = "name은 200자 이내여야 합니다")
                String name,
        @Schema(description = "테스트 설명", example = "Domain 레이어의 순수성을 보장하는 아키텍처 테스트", nullable = true)
                @Size(max = 2000, message = "description은 2000자 이내여야 합니다")
                String description,
        @Schema(description = "테스트 클래스 이름", example = "DomainLayerArchTest", nullable = true)
                @Size(max = 200, message = "testClassName은 200자 이내여야 합니다")
                String testClassName,
        @Schema(
                        description = "테스트 메서드 이름",
                        example = "domainShouldNotDependOnInfrastructure",
                        nullable = true)
                @Size(max = 200, message = "testMethodName은 200자 이내여야 합니다")
                String testMethodName,
        @Schema(
                        description = "테스트 코드 내용",
                        example = "noClasses().that().resideInAPackage(\"..domain..\")")
                @NotBlank(message = "testCode는 필수입니다")
                String testCode,
        @Schema(description = "심각도", example = "CRITICAL", nullable = true)
                @Size(max = 20, message = "severity는 20자 이내여야 합니다")
                String severity) {}
