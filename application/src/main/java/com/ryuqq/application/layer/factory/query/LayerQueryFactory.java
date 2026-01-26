package com.ryuqq.application.layer.factory.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.layer.dto.query.LayerSearchParams;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * LayerQueryFactory - Layer 조회 객체 팩토리
 *
 * <p>Query DTO로부터 도메인 조회 조건을 생성합니다.
 *
 * <p>FAC-001: Service에서 Criteria를 직접 생성하지 않고 Factory에 위임합니다.
 *
 * <p>FAC-002: Factory 메서드에 DTO 통째로 전달.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LayerQueryFactory {

    /**
     * LayerSearchParams로부터 LayerSliceCriteria 생성
     *
     * <p>FAC-001: Service에서 Criteria를 직접 생성하지 않고 Factory에 위임합니다.
     *
     * <p>FAC-002: DTO를 통째로 전달받아 내부에서 필드 접근을 처리합니다.
     *
     * @param searchParams 검색 파라미터 DTO
     * @return LayerSliceCriteria
     */
    public LayerSliceCriteria createSliceCriteria(LayerSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest =
                toCursorPageRequest(searchParams.cursorParams());

        List<ArchitectureId> architectureIds =
                convertArchitectureIds(searchParams.architectureIds());

        return LayerSliceCriteria.of(
                cursorPageRequest,
                architectureIds,
                searchParams.searchField(),
                searchParams.searchWord());
    }

    // ==================== Private Helper Methods ====================

    private CursorPageRequest<Long> toCursorPageRequest(CommonCursorParams cursorParams) {
        if (cursorParams.isFirstPage()) {
            return CursorPageRequest.first(cursorParams.size());
        }
        Long cursor = parseCursorToLong(cursorParams.cursor());
        return CursorPageRequest.afterId(cursor, cursorParams.size());
    }

    /**
     * String 커서를 Long으로 변환
     *
     * <p>Layer 도메인은 Long ID를 커서로 사용하므로 String → Long 변환이 필요합니다.
     *
     * @param cursor String 타입 커서 값
     * @return Long 타입 커서 값
     * @throws NumberFormatException 유효하지 않은 숫자 형식인 경우
     */
    private Long parseCursorToLong(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        return Long.valueOf(cursor);
    }

    private List<ArchitectureId> convertArchitectureIds(List<Long> architectureIds) {
        if (architectureIds == null || architectureIds.isEmpty()) {
            return null;
        }
        return architectureIds.stream().map(ArchitectureId::of).collect(Collectors.toList());
    }
}
