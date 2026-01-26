package com.ryuqq.application.checklistitem.dto.response;

import java.util.List;

/**
 * ChecklistItemSliceResult - 체크리스트 항목 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 체크리스트 항목 목록을 담습니다.
 *
 * @param checklistItems 체크리스트 항목 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ChecklistItemSliceResult(
        List<ChecklistItemResult> checklistItems, boolean hasNext, Long nextCursor) {

    /**
     * 빈 결과 생성
     *
     * @return 빈 ChecklistItemSliceResult
     */
    public static ChecklistItemSliceResult empty() {
        return new ChecklistItemSliceResult(List.of(), false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param checklistItems 체크리스트 항목 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return ChecklistItemSliceResult
     */
    public static ChecklistItemSliceResult of(
            List<ChecklistItemResult> checklistItems, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !checklistItems.isEmpty()) {
            nextCursor = checklistItems.get(checklistItems.size() - 1).id();
        }
        return new ChecklistItemSliceResult(checklistItems, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 체크리스트 항목 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return checklistItems.isEmpty();
    }

    /**
     * 체크리스트 항목 개수 반환
     *
     * @return 체크리스트 항목 개수
     */
    public int size() {
        return checklistItems.size();
    }
}
