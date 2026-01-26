package com.ryuqq.application.architecture.factory.query;

import com.ryuqq.application.architecture.dto.query.ArchitectureSearchParams;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ArchitectureQueryFactory - Architecture Query → Criteria 변환 Factory
 *
 * <p>Query DTO를 Domain Criteria로 변환합니다.
 *
 * <p>FAC-001: Service에서 Criteria를 직접 생성하지 않고 Factory에 위임합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ArchitectureQueryFactory {

    // ==================== Slice Criteria 생성 (커서 기반) ====================

    private CursorPageRequest<Long> toCursorPageRequest(CommonCursorParams cursorParams) {
        if (cursorParams.isFirstPage()) {
            return CursorPageRequest.first(cursorParams.size());
        }
        Long cursor = parseCursorToLong(cursorParams.cursor());
        return CursorPageRequest.afterId(cursor, cursorParams.size());
    }

    private Long parseCursorToLong(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        return Long.valueOf(cursor);
    }

    /**
     * ArchitectureSearchParams로부터 ArchitectureSliceCriteria 생성
     *
     * <p>FAC-001: Service에서 Criteria를 직접 생성하지 않고 Factory에 위임합니다.
     *
     * <p>SearchParams의 필터 값들을 Domain VO로 변환하여 Criteria를 생성합니다.
     *
     * @param searchParams 조회 파라미터
     * @return ArchitectureSliceCriteria
     */
    public ArchitectureSliceCriteria createSliceCriteria(ArchitectureSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest =
                toCursorPageRequest(searchParams.cursorParams());
        List<TechStackId> techStackIds = toTechStackIds(searchParams.techStackIds());
        return ArchitectureSliceCriteria.of(cursorPageRequest, techStackIds);
    }

    private List<TechStackId> toTechStackIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return ids.stream().map(TechStackId::new).toList();
    }
}
