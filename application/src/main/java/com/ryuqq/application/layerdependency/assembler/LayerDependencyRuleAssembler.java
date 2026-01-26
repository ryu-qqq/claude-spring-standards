package com.ryuqq.application.layerdependency.assembler;

import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleResult;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleAssembler - 레이어 의존성 규칙 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class LayerDependencyRuleAssembler {

    /**
     * LayerDependencyRule 도메인 객체를 LayerDependencyRuleResult로 변환
     *
     * @param rule 레이어 의존성 규칙 도메인 객체
     * @return LayerDependencyRuleResult
     */
    public LayerDependencyRuleResult toResult(LayerDependencyRule rule) {
        return LayerDependencyRuleResult.from(rule);
    }

    /**
     * LayerDependencyRule 목록을 LayerDependencyRuleResult 목록으로 변환
     *
     * @param rules 레이어 의존성 규칙 도메인 객체 목록
     * @return LayerDependencyRuleResult 목록
     */
    public List<LayerDependencyRuleResult> toResults(List<LayerDependencyRule> rules) {
        return rules.stream().map(this::toResult).toList();
    }
}
