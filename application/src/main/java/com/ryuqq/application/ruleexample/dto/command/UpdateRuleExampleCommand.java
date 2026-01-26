package com.ryuqq.application.ruleexample.dto.command;

import java.util.List;

/**
 * UpdateRuleExampleCommand - 규칙 예시 수정 커맨드
 *
 * <p>규칙 예시 수정에 필요한 데이터를 전달합니다.
 *
 * @param ruleExampleId 수정할 규칙 예시 ID
 * @param exampleType 예시 타입 (GOOD/BAD) (nullable)
 * @param code 예시 코드 (nullable)
 * @param language 언어 (nullable)
 * @param explanation 설명 (nullable)
 * @param highlightLines 강조 라인 번호 목록 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdateRuleExampleCommand(
        Long ruleExampleId,
        String exampleType,
        String code,
        String language,
        String explanation,
        List<Integer> highlightLines) {}
