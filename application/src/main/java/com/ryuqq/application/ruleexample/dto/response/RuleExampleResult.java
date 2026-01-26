package com.ryuqq.application.ruleexample.dto.response;

import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import java.time.Instant;
import java.util.List;

/**
 * RuleExampleResult - 규칙 예시 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * @param id 규칙 예시 ID
 * @param ruleId 코딩 규칙 ID
 * @param exampleType 예시 타입 (GOOD/BAD)
 * @param code 예시 코드
 * @param language 언어
 * @param explanation 설명 (nullable)
 * @param highlightLines 강조 라인 번호 목록
 * @param source 예시 소스 (MANUAL/AGENT_FEEDBACK)
 * @param feedbackId 피드백 ID (nullable)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RuleExampleResult(
        Long id,
        Long ruleId,
        String exampleType,
        String code,
        String language,
        String explanation,
        List<Integer> highlightLines,
        String source,
        Long feedbackId,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * @param ruleExample RuleExample 도메인 객체
     * @return RuleExampleResult
     */
    public static RuleExampleResult from(RuleExample ruleExample) {
        return new RuleExampleResult(
                ruleExample.id().value(),
                ruleExample.ruleId().value(),
                ruleExample.exampleType().name(),
                ruleExample.code().value(),
                ruleExample.language().name(),
                ruleExample.explanation(),
                ruleExample.highlightLines() != null
                        ? ruleExample.highlightLines().lines()
                        : List.of(),
                ruleExample.source().name(),
                ruleExample.feedbackId(),
                ruleExample.createdAt(),
                ruleExample.updatedAt());
    }
}
