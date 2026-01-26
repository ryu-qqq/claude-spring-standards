package com.ryuqq.adapter.in.rest.zerotolerance.dto.response;

import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemApiResponse;
import com.ryuqq.adapter.in.rest.ruleexample.dto.response.RuleExampleApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ZeroToleranceRuleDetailApiResponse - ZeroToleranceRule 상세 API Response DTO
 *
 * <p>Zero-Tolerance 규칙 상세 조회 결과를 API 응답으로 변환합니다. CodingRule 정보와 함께 관련 RuleExample, ChecklistItem을
 * 포함합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * @param id 코딩 규칙 ID
 * @param code 규칙 코드
 * @param name 규칙 이름
 * @param severity 심각도 (BLOCKER, CRITICAL, MAJOR, MINOR, INFO)
 * @param category 카테고리
 * @param description 설명
 * @param rationale 근거
 * @param autoFixable 자동 수정 가능 여부
 * @param appliesTo 적용 대상 목록
 * @param examples 규칙 예시 목록
 * @param checklistItems 체크리스트 항목 목록
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Zero-Tolerance 규칙 상세 응답")
public record ZeroToleranceRuleDetailApiResponse(
        @Schema(description = "코딩 규칙 ID", example = "1") Long id,
        @Schema(description = "규칙 코드", example = "AGG-001") String code,
        @Schema(description = "규칙 이름", example = "Lombok 사용 금지") String name,
        @Schema(description = "심각도", example = "BLOCKER") String severity,
        @Schema(description = "카테고리", example = "ANNOTATION") String category,
        @Schema(description = "규칙 설명", example = "Domain 레이어에서 Lombok 어노테이션 사용을 금지합니다.")
                String description,
        @Schema(description = "규칙 근거", example = "Lombok은 바이트코드 조작으로 예측 불가능한 동작을 유발할 수 있습니다.")
                String rationale,
        @Schema(description = "자동 수정 가능 여부", example = "false") boolean autoFixable,
        @Schema(description = "적용 대상 목록", example = "[\"AGGREGATE\", \"ENTITY\", \"VALUE_OBJECT\"]")
                List<String> appliesTo,
        @Schema(description = "규칙 예시 목록") List<RuleExampleApiResponse> examples,
        @Schema(description = "체크리스트 항목 목록") List<ChecklistItemApiResponse> checklistItems,
        @Schema(description = "생성 일시 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정 일시 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String updatedAt) {}
