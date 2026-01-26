package com.ryuqq.application.codingrule.assembler;

import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CodingRuleAssembler - 코딩 규칙 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleAssembler {

    /**
     * CodingRule 도메인 객체를 CodingRuleResult로 변환
     *
     * @param codingRule 코딩 규칙 도메인 객체
     * @return CodingRuleResult
     */
    public CodingRuleResult toResult(CodingRule codingRule) {
        return CodingRuleResult.from(codingRule);
    }

    /**
     * CodingRule 목록을 CodingRuleResult 목록으로 변환
     *
     * @param codingRules 코딩 규칙 도메인 객체 목록
     * @return CodingRuleResult 목록
     */
    public List<CodingRuleResult> toResults(List<CodingRule> codingRules) {
        return codingRules.stream().map(this::toResult).toList();
    }

    /**
     * CodingRule 목록을 CodingRuleSliceResult로 변환
     *
     * @param codingRules 코딩 규칙 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return CodingRuleSliceResult
     */
    public CodingRuleSliceResult toSliceResult(List<CodingRule> codingRules, int requestedSize) {
        boolean hasNext = codingRules.size() > requestedSize;
        List<CodingRule> resultCodingRules =
                hasNext ? codingRules.subList(0, requestedSize) : codingRules;
        List<CodingRuleResult> results = toResults(resultCodingRules);
        return CodingRuleSliceResult.of(results, hasNext);
    }
}
