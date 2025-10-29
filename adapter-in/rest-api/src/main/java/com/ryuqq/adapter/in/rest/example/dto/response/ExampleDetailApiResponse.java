package com.ryuqq.adapter.in.rest.example.dto.response;

import java.time.LocalDateTime;

/**
 * Example 상세 조회 REST API 응답 DTO
 *
 * <p>REST API Layer 전용 응답 DTO로, Application Layer의 ExampleDetailResponse를 변환하여 사용합니다.</p>
 *
 * <p><strong>의존성 역전 원칙 준수:</strong></p>
 * <ul>
 *   <li>REST API Layer는 Application Layer에 의존해도 되지만, Application Layer가 REST API Layer에 의존하면 안 됩니다</li>
 *   <li>따라서 REST API Layer 전용 Response DTO를 별도로 만들어 사용합니다</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * // Application Layer Response → REST API Layer Response 변환
 * ExampleDetailResponse appResponse = getExampleQueryService.getById(query);
 * ExampleDetailApiResponse apiResponse = ExampleDetailApiResponse.from(appResponse);
 * }</pre>
 *
 * @param id Example ID
 * @param message 메시지 내용
 * @param status 상태 (ACTIVE, INACTIVE, DELETED)
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author windsurf
 * @since 1.0.0
 */
public record ExampleDetailApiResponse(
    Long id,
    String message,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    /**
     * Application Layer Response로부터 REST API Response 생성
     *
     * @param appResponse Application Layer의 ExampleDetailResponse
     * @return REST API Layer의 ExampleDetailApiResponse
     */
    public static ExampleDetailApiResponse from(com.ryuqq.application.example.dto.response.ExampleDetailResponse appResponse) {
        return new ExampleDetailApiResponse(
            appResponse.id(),
            appResponse.message(),
            appResponse.status(),
            appResponse.createdAt(),
            appResponse.updatedAt()
        );
    }
}
