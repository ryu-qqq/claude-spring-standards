package com.ryuqq.application.checklistitem.manager;

import com.ryuqq.application.checklistitem.port.out.ChecklistItemQueryPort;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.exception.ChecklistItemNotFoundException;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ChecklistItemReadManager - 체크리스트 항목 조회 관리자
 *
 * <p>체크리스트 항목 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * <p>MNG-003: Manager 파라미터는 VO(Value Object)만 사용.
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemReadManager {

    private final ChecklistItemQueryPort checklistItemQueryPort;

    public ChecklistItemReadManager(ChecklistItemQueryPort checklistItemQueryPort) {
        this.checklistItemQueryPort = checklistItemQueryPort;
    }

    /**
     * ID로 체크리스트 항목 조회 (존재하지 않으면 예외)
     *
     * @param checklistItemId 체크리스트 항목 ID
     * @return 체크리스트 항목
     * @throws ChecklistItemNotFoundException 체크리스트 항목이 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public ChecklistItem getById(ChecklistItemId checklistItemId) {
        return checklistItemQueryPort
                .findById(checklistItemId)
                .orElseThrow(() -> new ChecklistItemNotFoundException(checklistItemId.value()));
    }

    /**
     * ID로 체크리스트 항목 존재 여부 확인 후 반환
     *
     * @param checklistItemId 체크리스트 항목 ID
     * @return 체크리스트 항목 (nullable)
     */
    @Transactional(readOnly = true)
    public ChecklistItem findById(ChecklistItemId checklistItemId) {
        return checklistItemQueryPort.findById(checklistItemId).orElse(null);
    }

    /**
     * 슬라이스 조건으로 체크리스트 항목 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 체크리스트 항목 목록
     */
    @Transactional(readOnly = true)
    public List<ChecklistItem> findBySliceCriteria(ChecklistItemSliceCriteria criteria) {
        return checklistItemQueryPort.findBySliceCriteria(criteria);
    }

    /**
     * 코딩 규칙 ID로 체크리스트 항목 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 체크리스트 항목 목록
     */
    @Transactional(readOnly = true)
    public List<ChecklistItem> findByRuleId(CodingRuleId ruleId) {
        return checklistItemQueryPort.findByRuleId(ruleId);
    }

    /**
     * 코딩 규칙 ID와 순서로 존재 여부 확인
     *
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByRuleIdAndSequenceOrder(CodingRuleId ruleId, int sequenceOrder) {
        return checklistItemQueryPort.existsByRuleIdAndSequenceOrder(ruleId.value(), sequenceOrder);
    }
}
