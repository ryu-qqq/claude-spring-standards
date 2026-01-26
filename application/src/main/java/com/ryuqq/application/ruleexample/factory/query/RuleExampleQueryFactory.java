package com.ryuqq.application.ruleexample.factory.query;

import com.ryuqq.application.ruleexample.dto.query.RuleExampleSearchParams;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * RuleExampleQueryFactory - 규칙 예시 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 */
@Component
public class RuleExampleQueryFactory {

    /**
     * RuleExampleSearchParams로부터 RuleExampleSliceCriteria 생성
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 검색 파라미터
     * @return RuleExampleSliceCriteria
     */
    public RuleExampleSliceCriteria createSliceCriteria(RuleExampleSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;

        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<CodingRuleId> ruleIds = null;
        if (searchParams.hasRuleIds()) {
            ruleIds =
                    searchParams.ruleIds().stream()
                            .map(CodingRuleId::of)
                            .collect(Collectors.toList());
        }

        List<ExampleType> exampleTypes = null;
        if (searchParams.hasExampleTypes()) {
            exampleTypes =
                    searchParams.exampleTypes().stream()
                            .map(ExampleType::valueOf)
                            .collect(Collectors.toList());
        }

        List<ExampleLanguage> languages = null;
        if (searchParams.hasLanguages()) {
            languages =
                    searchParams.languages().stream()
                            .map(ExampleLanguage::valueOf)
                            .collect(Collectors.toList());
        }

        return RuleExampleSliceCriteria.of(ruleIds, exampleTypes, languages, cursorPageRequest);
    }

    /**
     * Long ID를 RuleExampleId로 변환
     *
     * @param ruleExampleId 규칙 예시 ID
     * @return RuleExampleId
     */
    public RuleExampleId toRuleExampleId(Long ruleExampleId) {
        return RuleExampleId.of(ruleExampleId);
    }

    /**
     * Long ID를 CodingRuleId로 변환
     *
     * @param ruleId 코딩 규칙 ID
     * @return CodingRuleId
     */
    public CodingRuleId toRuleId(Long ruleId) {
        return CodingRuleId.of(ruleId);
    }
}
