package com.ryuqq.application.ruleexample.dto.command;

import java.util.List;

/**
 * CreateRuleExampleCommand - 규칙 예시 생성 커맨드
 *
 * <p>규칙 예시 생성에 필요한 데이터를 전달합니다.
 *
 * @param ruleId 코딩 규칙 ID
 * @param exampleType 예시 타입 (GOOD/BAD)
 * @param code 예시 코드
 * @param language 언어 (JAVA/KOTLIN/SQL 등)
 * @param explanation 설명 (nullable)
 * @param highlightLines 강조 라인 번호 목록 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CreateRuleExampleCommand(
        Long ruleId,
        String exampleType,
        String code,
        String language,
        String explanation,
        List<Integer> highlightLines) {}
