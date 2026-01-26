package com.ryuqq.application.checklistitem.port.out;

import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import java.util.List;
import java.util.Optional;

/**
 * ChecklistItemQueryPort - 체크리스트 항목 조회 아웃바운드 포트
 *
 * <p>영속성 계층에서 구현합니다.
 *
 * @author ryu-qqq
 */
public interface ChecklistItemQueryPort {

    /**
     * ID로 체크리스트 항목 조회
     *
     * @param id 체크리스트 항목 ID
     * @return 체크리스트 항목 Optional
     */
    Optional<ChecklistItem> findById(Long id);

    /**
     * ChecklistItemId로 체크리스트 항목 조회
     *
     * @param checklistItemId 체크리스트 항목 ID
     * @return 체크리스트 항목 Optional
     */
    Optional<ChecklistItem> findById(ChecklistItemId checklistItemId);

    /**
     * 코딩 규칙 ID로 체크리스트 항목 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 체크리스트 항목 목록
     */
    List<ChecklistItem> findByRuleId(Long ruleId);

    /**
     * CodingRuleId 값 객체로 체크리스트 항목 목록 조회
     *
     * @param ruleId 코딩 규칙 ID
     * @return 체크리스트 항목 목록
     */
    List<ChecklistItem> findByRuleId(CodingRuleId ruleId);

    /**
     * 슬라이스 조건으로 체크리스트 항목 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 체크리스트 항목 목록
     */
    List<ChecklistItem> findBySliceCriteria(ChecklistItemSliceCriteria criteria);

    /**
     * 전체 체크리스트 항목 목록 조회
     *
     * @return 체크리스트 항목 목록
     */
    List<ChecklistItem> findAll();

    /**
     * 코딩 규칙 ID와 순서로 중복 확인
     *
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @return 존재하면 true
     */
    boolean existsByRuleIdAndSequenceOrder(Long ruleId, int sequenceOrder);
}
