package com.ryuqq.application.zerotolerance.assembler;

import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleDetailResult;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleAssembler - Zero-Tolerance 규칙 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleAssembler {

    /**
     * CodingRule과 관련 데이터를 ZeroToleranceRuleDetailResult로 변환
     *
     * @param codingRule 코딩 규칙 도메인 객체
     * @param ruleExamples 규칙 예시 목록
     * @param checklistItems 체크리스트 항목 목록
     * @return ZeroToleranceRuleDetailResult
     */
    public ZeroToleranceRuleDetailResult toDetailResult(
            CodingRule codingRule,
            List<RuleExample> ruleExamples,
            List<ChecklistItem> checklistItems) {

        CodingRuleResult codingRuleResult = CodingRuleResult.from(codingRule);

        List<RuleExampleResult> exampleResults =
                ruleExamples.stream().map(RuleExampleResult::from).toList();

        List<ChecklistItemResult> checklistItemResults =
                checklistItems.stream().map(ChecklistItemResult::from).toList();

        return ZeroToleranceRuleDetailResult.of(
                codingRuleResult, exampleResults, checklistItemResults);
    }

    /**
     * CodingRule만 있는 결과로 변환 (예시와 체크리스트가 없는 경우)
     *
     * @param codingRule 코딩 규칙 도메인 객체
     * @return ZeroToleranceRuleDetailResult
     */
    public ZeroToleranceRuleDetailResult toDetailResultWithRuleOnly(CodingRule codingRule) {
        CodingRuleResult codingRuleResult = CodingRuleResult.from(codingRule);
        return ZeroToleranceRuleDetailResult.withRuleOnly(codingRuleResult);
    }

    /**
     * ZeroToleranceRuleDetailResult 목록을 슬라이스 결과로 변환
     *
     * @param detailResults 상세 결과 목록
     * @param requestedSize 요청한 페이지 크기
     * @return ZeroToleranceRuleSliceResult
     */
    public ZeroToleranceRuleSliceResult toSliceResult(
            List<ZeroToleranceRuleDetailResult> detailResults, int requestedSize) {
        boolean hasNext = detailResults.size() > requestedSize;
        List<ZeroToleranceRuleDetailResult> resultList =
                hasNext ? detailResults.subList(0, requestedSize) : detailResults;
        return ZeroToleranceRuleSliceResult.of(resultList, hasNext);
    }
}
