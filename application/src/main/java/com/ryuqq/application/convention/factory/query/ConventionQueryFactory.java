package com.ryuqq.application.convention.factory.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ConventionQueryFactory - Convention Query 관련 VO 변환 Factory
 *
 * <p>Query 관련 파라미터를 VO로 변환합니다.
 *
 * <p>Factory 분리: CommandFactory와 QueryFactory는 분리합니다.
 *
 * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ConventionQueryFactory {

    /**
     * Long ID를 ConventionId VO로 변환
     *
     * @param id Long ID
     * @return ConventionId VO
     */
    public ConventionId createId(Long id) {
        return ConventionId.of(id);
    }

    /**
     * Long moduleId를 ModuleId VO로 변환
     *
     * @param moduleId Long moduleId
     * @return ModuleId VO
     */
    public ModuleId createModuleId(Long moduleId) {
        return ModuleId.of(moduleId);
    }

    /**
     * CommonCursorParams를 ConventionSliceCriteria로 변환
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param cursorParams 커서 기반 페이징 파라미터
     * @return ConventionSliceCriteria
     */
    public ConventionSliceCriteria createDefaultSliceCriteria(CommonCursorParams cursorParams) {
        CursorPageRequest<Long> cursorPageRequest = toCursorPageRequest(cursorParams);
        return ConventionSliceCriteria.of(cursorPageRequest);
    }

    /**
     * ConventionSearchParams를 ConventionSliceCriteria로 변환
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 검색 파라미터
     * @return ConventionSliceCriteria
     */
    public ConventionSliceCriteria createSliceCriteria(
            com.ryuqq.application.convention.dto.query.ConventionSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest =
                toCursorPageRequest(searchParams.cursorParams());
        List<ModuleId> moduleIds = null;
        if (searchParams.hasModuleIds()) {
            moduleIds =
                    searchParams.moduleIds().stream()
                            .map(this::createModuleId)
                            .collect(Collectors.toList());
        }
        return ConventionSliceCriteria.of(cursorPageRequest, moduleIds);
    }

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
}
