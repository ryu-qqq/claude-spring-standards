package com.ryuqq.application.checklistitem.assembler;

import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemSliceResult;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ChecklistItemAssembler - 체크리스트 항목 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class ChecklistItemAssembler {

    /**
     * ChecklistItem 도메인 객체를 ChecklistItemResult로 변환
     *
     * @param checklistItem 체크리스트 항목 도메인 객체
     * @return ChecklistItemResult
     */
    public ChecklistItemResult toResult(ChecklistItem checklistItem) {
        return ChecklistItemResult.from(checklistItem);
    }

    /**
     * ChecklistItem 목록을 ChecklistItemResult 목록으로 변환
     *
     * @param checklistItems 체크리스트 항목 도메인 객체 목록
     * @return ChecklistItemResult 목록
     */
    public List<ChecklistItemResult> toResults(List<ChecklistItem> checklistItems) {
        return checklistItems.stream().map(this::toResult).toList();
    }

    /**
     * ChecklistItem 목록을 ChecklistItemSliceResult로 변환
     *
     * @param checklistItems 체크리스트 항목 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return ChecklistItemSliceResult
     */
    public ChecklistItemSliceResult toSliceResult(
            List<ChecklistItem> checklistItems, int requestedSize) {
        boolean hasNext = checklistItems.size() > requestedSize;
        List<ChecklistItem> resultChecklistItems =
                hasNext ? checklistItems.subList(0, requestedSize) : checklistItems;
        List<ChecklistItemResult> results = toResults(resultChecklistItems);
        return ChecklistItemSliceResult.of(results, hasNext);
    }
}
