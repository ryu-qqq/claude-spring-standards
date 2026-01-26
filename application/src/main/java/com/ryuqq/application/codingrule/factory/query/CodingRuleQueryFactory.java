package com.ryuqq.application.codingrule.factory.query;

import com.ryuqq.application.codingrule.dto.query.CodingRuleIndexSearchParams;
import com.ryuqq.application.codingrule.dto.query.CodingRuleSearchParams;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.query.CodingRuleIndexCriteria;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * CodingRuleQueryFactory - 코딩 규칙 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 */
@Component
public class CodingRuleQueryFactory {

    /**
     * CodingRuleSearchParams로부터 CodingRuleSliceCriteria 생성
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 검색 파라미터
     * @return CodingRuleSliceCriteria
     */
    public CodingRuleSliceCriteria createSliceCriteria(CodingRuleSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;

        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<RuleCategory> categories = null;
        if (searchParams.hasCategories()) {
            categories =
                    searchParams.categories().stream()
                            .map(RuleCategory::valueOf)
                            .collect(Collectors.toList());
        }

        List<RuleSeverity> severities = null;
        if (searchParams.hasSeverities()) {
            severities =
                    searchParams.severities().stream()
                            .map(RuleSeverity::valueOf)
                            .collect(Collectors.toList());
        }

        return CodingRuleSliceCriteria.of(
                cursorPageRequest,
                categories,
                severities,
                searchParams.searchField(),
                searchParams.searchWord());
    }

    /**
     * Long ID를 CodingRuleId로 변환
     *
     * @param codingRuleId 코딩 규칙 ID
     * @return CodingRuleId
     */
    public CodingRuleId toCodingRuleId(Long codingRuleId) {
        return CodingRuleId.of(codingRuleId);
    }

    /**
     * Long ID를 ConventionId로 변환
     *
     * @param conventionId 컨벤션 ID
     * @return ConventionId
     */
    public ConventionId toConventionId(Long conventionId) {
        return ConventionId.of(conventionId);
    }

    /**
     * CodingRuleIndexSearchParams로부터 CodingRuleIndexCriteria 생성
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 인덱스 검색 파라미터
     * @return CodingRuleIndexCriteria
     */
    public CodingRuleIndexCriteria createIndexCriteria(CodingRuleIndexSearchParams searchParams) {
        List<RuleSeverity> severities = null;
        if (searchParams.hasSeverities()) {
            severities =
                    searchParams.severities().stream()
                            .map(RuleSeverity::valueOf)
                            .collect(Collectors.toList());
        }

        List<RuleCategory> categories = null;
        if (searchParams.hasCategories()) {
            categories =
                    searchParams.categories().stream()
                            .map(RuleCategory::valueOf)
                            .collect(Collectors.toList());
        }

        return CodingRuleIndexCriteria.of(searchParams.conventionId(), severities, categories);
    }
}
