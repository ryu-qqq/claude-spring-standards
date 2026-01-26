package com.ryuqq.domain.checklistitem.query;

import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import java.util.List;

/**
 * ChecklistItemSliceCriteria - ChecklistItem 슬라이스 조회 조건 (커서 기반)
 *
 * <p>ChecklistItem 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 ChecklistItem ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param ruleIds 필터링할 코딩 규칙 ID 목록 (optional)
 * @param checkTypes 필터링할 체크 타입 목록 (optional)
 * @param automationTools 필터링할 자동화 도구 목록 (optional)
 * @param isCritical 필수 여부 필터 (optional)
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ChecklistItemSliceCriteria(
        List<CodingRuleId> ruleIds,
        List<CheckType> checkTypes,
        List<AutomationTool> automationTools,
        Boolean isCritical,
        CursorPageRequest<Long> cursorPageRequest) {

    public ChecklistItemSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 전체 규칙)
     *
     * @param size 슬라이스 크기
     * @return ChecklistItemSliceCriteria
     */
    public static ChecklistItemSliceCriteria first(int size) {
        return new ChecklistItemSliceCriteria(
                null, null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return ChecklistItemSliceCriteria
     */
    public static ChecklistItemSliceCriteria afterId(Long cursorId, int size) {
        return new ChecklistItemSliceCriteria(
                null, null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * 커서 기반 페이징 요청 생성
     *
     * @param ruleIds 코딩 규칙 ID 목록 (nullable)
     * @param checkTypes 체크 타입 목록 (nullable)
     * @param automationTools 자동화 도구 목록 (nullable)
     * @param isCritical 필수 여부 (nullable)
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return ChecklistItemSliceCriteria
     */
    public static ChecklistItemSliceCriteria of(
            List<CodingRuleId> ruleIds,
            List<CheckType> checkTypes,
            List<AutomationTool> automationTools,
            Boolean isCritical,
            CursorPageRequest<Long> cursorPageRequest) {
        return new ChecklistItemSliceCriteria(
                ruleIds, checkTypes, automationTools, isCritical, cursorPageRequest);
    }

    /**
     * 코딩 규칙 ID 필터 존재 여부 확인
     *
     * @return ruleIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasRuleIds() {
        return ruleIds != null && !ruleIds.isEmpty();
    }

    /**
     * 체크 타입 필터 존재 여부 확인
     *
     * @return checkTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasCheckTypes() {
        return checkTypes != null && !checkTypes.isEmpty();
    }

    /**
     * 자동화 도구 필터 존재 여부 확인
     *
     * @return automationTools가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasAutomationTools() {
        return automationTools != null && !automationTools.isEmpty();
    }

    /**
     * 필수 여부 필터 존재 여부 확인
     *
     * @return isCritical이 있으면 true
     */
    public boolean hasCriticalFilter() {
        return isCritical != null;
    }

    /**
     * 첫 페이지 요청인지 확인
     *
     * @return cursor가 null이면 true
     */
    public boolean isFirstPage() {
        return cursorPageRequest.cursor() == null;
    }

    /**
     * 커서가 있는지 확인
     *
     * @return 커서가 있으면 true
     */
    public boolean hasCursor() {
        return cursorPageRequest.cursor() != null;
    }

    /**
     * 슬라이스 크기 반환 (편의 메서드)
     *
     * @return size
     */
    public int size() {
        return cursorPageRequest.size();
    }

    /**
     * 실제 조회 크기 반환 (hasNext 판단용 +1)
     *
     * @return size + 1
     */
    public int fetchSize() {
        return cursorPageRequest.fetchSize();
    }
}
