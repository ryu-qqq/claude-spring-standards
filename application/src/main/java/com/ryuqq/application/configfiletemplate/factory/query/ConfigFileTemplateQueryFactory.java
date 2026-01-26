package com.ryuqq.application.configfiletemplate.factory.query;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.configfiletemplate.dto.query.ConfigFileTemplateSearchParams;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateQueryFactory - ConfigFileTemplate Query → Criteria 변환 Factory
 *
 * <p>Query DTO를 Domain Criteria로 변환합니다.
 *
 * <p>FAC-001: Service에서 Criteria를 직접 생성하지 않고 Factory에 위임합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateQueryFactory {

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
     * ConfigFileTemplateSearchParams로부터 ConfigFileTemplateSliceCriteria 생성
     *
     * @param searchParams 조회 파라미터
     * @return ConfigFileTemplateSliceCriteria
     */
    public ConfigFileTemplateSliceCriteria createSliceCriteria(
            ConfigFileTemplateSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest =
                toCursorPageRequest(searchParams.cursorParams());

        return ConfigFileTemplateSliceCriteria.of(
                cursorPageRequest,
                toTechStackIds(searchParams.techStackIds()),
                toToolTypes(searchParams.toolTypes()),
                toCategories(searchParams.categories()));
    }

    // ==================== Private Helper Methods ====================

    private List<ToolType> toToolTypes(List<String> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        return types.stream().map(ToolType::valueOf).toList();
    }

    private List<TechStackId> toTechStackIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return ids.stream().map(TechStackId::new).toList();
    }

    private List<TemplateCategory> toCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        return categories.stream().map(TemplateCategory::valueOf).toList();
    }
}
