package com.ryuqq.application.checklistitem.validator;

import com.ryuqq.application.checklistitem.manager.ChecklistItemReadManager;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.exception.ChecklistItemNotFoundException;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemValidator - 체크리스트 항목 검증기
 *
 * <p>체크리스트 항목 비즈니스 규칙을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemValidator {

    private final ChecklistItemReadManager checklistItemReadManager;

    public ChecklistItemValidator(ChecklistItemReadManager checklistItemReadManager) {
        this.checklistItemReadManager = checklistItemReadManager;
    }

    /**
     * 체크리스트 항목 존재 여부 검증 후 반환 (조회 + 검증 통합)
     *
     * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param checklistItemId 체크리스트 항목 ID
     * @return 존재하는 ChecklistItem
     * @throws ChecklistItemNotFoundException 체크리스트 항목이 존재하지 않으면
     */
    public ChecklistItem findExistingOrThrow(ChecklistItemId checklistItemId) {
        return checklistItemReadManager.getById(checklistItemId);
    }

    /**
     * 동일 규칙 내 순서 중복 검증
     *
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @throws IllegalArgumentException 이미 해당 순서가 존재하면
     */
    public void validateSequenceOrderNotDuplicate(CodingRuleId ruleId, int sequenceOrder) {
        if (checklistItemReadManager.existsByRuleIdAndSequenceOrder(ruleId, sequenceOrder)) {
            throw new IllegalArgumentException(
                    String.format(
                            "ChecklistItem with ruleId %d and sequenceOrder %d already exists",
                            ruleId.value(), sequenceOrder));
        }
    }
}
