package com.ryuqq.application.zerotolerance.dto.response;

import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import java.util.List;

/**
 * ZeroToleranceRuleDetailResult - Zero-Tolerance 규칙 상세 조회 결과 DTO
 *
 * <p>CodingRule과 관련된 RuleExample, ChecklistItem을 함께 반환합니다.
 *
 * @param codingRule 코딩 규칙 정보
 * @param examples 규칙 예시 목록
 * @param checklistItems 체크리스트 항목 목록
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ZeroToleranceRuleDetailResult(
        CodingRuleResult codingRule,
        List<RuleExampleResult> examples,
        List<ChecklistItemResult> checklistItems) {

    public ZeroToleranceRuleDetailResult {
        if (codingRule == null) {
            throw new IllegalArgumentException("codingRule must not be null");
        }
        if (examples == null) {
            examples = List.of();
        }
        if (checklistItems == null) {
            checklistItems = List.of();
        }
    }

    /**
     * CodingRule만 있는 결과 생성 (예시와 체크리스트가 없는 경우)
     *
     * @param codingRule 코딩 규칙 정보
     * @return ZeroToleranceRuleDetailResult
     */
    public static ZeroToleranceRuleDetailResult withRuleOnly(CodingRuleResult codingRule) {
        return new ZeroToleranceRuleDetailResult(codingRule, List.of(), List.of());
    }

    /**
     * 전체 정보로 결과 생성
     *
     * @param codingRule 코딩 규칙 정보
     * @param examples 규칙 예시 목록
     * @param checklistItems 체크리스트 항목 목록
     * @return ZeroToleranceRuleDetailResult
     */
    public static ZeroToleranceRuleDetailResult of(
            CodingRuleResult codingRule,
            List<RuleExampleResult> examples,
            List<ChecklistItemResult> checklistItems) {
        return new ZeroToleranceRuleDetailResult(codingRule, examples, checklistItems);
    }

    /**
     * 예시가 있는지 확인
     *
     * @return 예시가 있으면 true
     */
    public boolean hasExamples() {
        return !examples.isEmpty();
    }

    /**
     * 체크리스트 항목이 있는지 확인
     *
     * @return 체크리스트 항목이 있으면 true
     */
    public boolean hasChecklistItems() {
        return !checklistItems.isEmpty();
    }

    /**
     * 예시 개수 반환
     *
     * @return 예시 개수
     */
    public int exampleCount() {
        return examples.size();
    }

    /**
     * 체크리스트 항목 개수 반환
     *
     * @return 체크리스트 항목 개수
     */
    public int checklistItemCount() {
        return checklistItems.size();
    }
}
