package com.ryuqq.application.techstack.factory.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.techstack.dto.query.TechStackSearchParams;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.TechStackStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TechStackQueryFactory - TechStack Query → Criteria 변환 Factory
 *
 * <p>Query DTO를 Domain Criteria로 변환합니다.
 *
 * <p>FAC-001: Service에서 Criteria를 직접 생성하지 않고 Factory에 위임합니다.
 *
 * <p>FAC-002: Factory 메서드에 DTO 통째로 전달.
 *
 * @author ryu-qqq
 */
@Component
public class TechStackQueryFactory {

    /**
     * 필터가 포함된 Slice Criteria 생성
     *
     * <p>FAC-001: Service에서 Criteria를 직접 생성하지 않고 Factory에 위임합니다.
     *
     * <p>FAC-002: DTO를 통째로 전달받아 내부에서 필드 접근을 처리합니다.
     *
     * @param searchParams 검색 파라미터 DTO
     * @return TechStackSliceCriteria
     */
    public TechStackSliceCriteria createSliceCriteria(TechStackSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest =
                toCursorPageRequest(searchParams.cursorParams());

        TechStackStatus statusVo = convertStatus(searchParams.status());
        List<PlatformType> platformTypeVos = convertPlatformTypes(searchParams.platformTypes());

        return TechStackSliceCriteria.of(cursorPageRequest, statusVo, platformTypeVos);
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
     * <p>TechStack 도메인은 Long ID를 커서로 사용하므로 String → Long 변환이 필요합니다.
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

    private TechStackStatus convertStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return TechStackStatus.valueOf(status);
    }

    private List<PlatformType> convertPlatformTypes(List<String> platformTypes) {
        if (platformTypes == null || platformTypes.isEmpty()) {
            return null;
        }
        return platformTypes.stream().map(PlatformType::valueOf).toList();
    }
}
