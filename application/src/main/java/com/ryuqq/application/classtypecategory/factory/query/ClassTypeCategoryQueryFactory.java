package com.ryuqq.application.classtypecategory.factory.query;

import com.ryuqq.application.classtypecategory.dto.query.ClassTypeCategorySearchParams;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryQueryFactory - ClassTypeCategory Query → Criteria 변환 Factory
 *
 * <p>Query 파라미터를 Domain Criteria로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeCategoryQueryFactory {

    /**
     * ClassTypeCategorySearchParams로부터 ClassTypeCategorySliceCriteria 생성
     *
     * @param params 검색 파라미터
     * @return ClassTypeCategorySliceCriteria
     */
    public ClassTypeCategorySliceCriteria createSliceCriteria(
            ClassTypeCategorySearchParams params) {
        CursorPageRequest<Long> cursorPageRequest =
                params.cursor() == null
                        ? CursorPageRequest.first(params.size())
                        : CursorPageRequest.afterId(params.cursor(), params.size());

        List<ClassTypeCategoryId> ids =
                params.ids() == null
                        ? null
                        : params.ids().stream().map(ClassTypeCategoryId::of).toList();

        List<ArchitectureId> architectureIds =
                params.architectureIds() == null
                        ? null
                        : params.architectureIds().stream().map(ArchitectureId::of).toList();

        return ClassTypeCategorySliceCriteria.of(
                cursorPageRequest, ids, architectureIds, params.searchField(), params.searchWord());
    }
}
