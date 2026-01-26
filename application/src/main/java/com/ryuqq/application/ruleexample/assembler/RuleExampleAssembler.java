package com.ryuqq.application.ruleexample.assembler;

import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RuleExampleAssembler - 규칙 예시 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleAssembler {

    /**
     * RuleExample 도메인 객체를 RuleExampleResult로 변환
     *
     * @param ruleExample 규칙 예시 도메인 객체
     * @return RuleExampleResult
     */
    public RuleExampleResult toResult(RuleExample ruleExample) {
        return RuleExampleResult.from(ruleExample);
    }

    /**
     * RuleExample 목록을 RuleExampleResult 목록으로 변환
     *
     * @param ruleExamples 규칙 예시 도메인 객체 목록
     * @return RuleExampleResult 목록
     */
    public List<RuleExampleResult> toResults(List<RuleExample> ruleExamples) {
        return ruleExamples.stream().map(this::toResult).toList();
    }

    /**
     * RuleExample 목록을 RuleExampleSliceResult로 변환
     *
     * @param ruleExamples 규칙 예시 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return RuleExampleSliceResult
     */
    public RuleExampleSliceResult toSliceResult(List<RuleExample> ruleExamples, int requestedSize) {
        boolean hasNext = ruleExamples.size() > requestedSize;
        List<RuleExample> resultRuleExamples =
                hasNext ? ruleExamples.subList(0, requestedSize) : ruleExamples;
        List<RuleExampleResult> results = toResults(resultRuleExamples);
        return RuleExampleSliceResult.of(results, hasNext);
    }
}
