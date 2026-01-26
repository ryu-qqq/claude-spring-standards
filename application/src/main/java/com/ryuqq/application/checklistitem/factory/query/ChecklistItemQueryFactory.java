package com.ryuqq.application.checklistitem.factory.query;

import com.ryuqq.application.checklistitem.dto.query.ChecklistItemSearchParams;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemQueryFactory - 체크리스트 항목 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemQueryFactory {

    /**
     * ChecklistItemSearchParams로부터 ChecklistItemSliceCriteria 생성
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 검색 파라미터
     * @return ChecklistItemSliceCriteria
     */
    public ChecklistItemSliceCriteria createSliceCriteria(ChecklistItemSearchParams searchParams) {
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

        List<CheckType> checkTypes = null;
        if (searchParams.hasCheckTypes()) {
            checkTypes =
                    searchParams.checkTypes().stream()
                            .map(CheckType::valueOf)
                            .collect(Collectors.toList());
        }

        List<AutomationTool> automationTools = null;
        if (searchParams.hasAutomationTools()) {
            automationTools =
                    searchParams.automationTools().stream()
                            .map(AutomationTool::valueOf)
                            .collect(Collectors.toList());
        }

        return ChecklistItemSliceCriteria.of(
                ruleIds, checkTypes, automationTools, searchParams.isCritical(), cursorPageRequest);
    }

    /**
     * Long ID를 ChecklistItemId로 변환
     *
     * @param checklistItemId 체크리스트 항목 ID
     * @return ChecklistItemId
     */
    public ChecklistItemId toChecklistItemId(Long checklistItemId) {
        return ChecklistItemId.of(checklistItemId);
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
