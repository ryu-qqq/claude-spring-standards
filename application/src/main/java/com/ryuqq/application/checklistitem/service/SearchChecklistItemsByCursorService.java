package com.ryuqq.application.checklistitem.service;

import com.ryuqq.application.checklistitem.assembler.ChecklistItemAssembler;
import com.ryuqq.application.checklistitem.dto.query.ChecklistItemSearchParams;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemSliceResult;
import com.ryuqq.application.checklistitem.factory.query.ChecklistItemQueryFactory;
import com.ryuqq.application.checklistitem.manager.ChecklistItemReadManager;
import com.ryuqq.application.checklistitem.port.in.SearchChecklistItemsByCursorUseCase;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchChecklistItemsByCursorService - ChecklistItem 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchChecklistItemsByCursorUseCase를 구현합니다.
 *
 * <p>ChecklistItem 목록을 커서 기반으로 복합 조건(코딩 규칙 ID, 체크 타입, 자동화 도구)으로 조회합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-005: Domain 객체 직접 반환 금지 → Assembler 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
 *
 * @author ryu-qqq
 */
@Service
public class SearchChecklistItemsByCursorService implements SearchChecklistItemsByCursorUseCase {

    private final ChecklistItemQueryFactory checklistItemQueryFactory;
    private final ChecklistItemReadManager checklistItemReadManager;
    private final ChecklistItemAssembler checklistItemAssembler;

    public SearchChecklistItemsByCursorService(
            ChecklistItemQueryFactory checklistItemQueryFactory,
            ChecklistItemReadManager checklistItemReadManager,
            ChecklistItemAssembler checklistItemAssembler) {
        this.checklistItemQueryFactory = checklistItemQueryFactory;
        this.checklistItemReadManager = checklistItemReadManager;
        this.checklistItemAssembler = checklistItemAssembler;
    }

    @Override
    public ChecklistItemSliceResult execute(ChecklistItemSearchParams searchParams) {
        // Factory에서 Criteria 생성 (필터 포함)
        ChecklistItemSliceCriteria criteria =
                checklistItemQueryFactory.createSliceCriteria(searchParams);
        List<ChecklistItem> checklistItems = checklistItemReadManager.findBySliceCriteria(criteria);
        return checklistItemAssembler.toSliceResult(checklistItems, searchParams.size());
    }
}
