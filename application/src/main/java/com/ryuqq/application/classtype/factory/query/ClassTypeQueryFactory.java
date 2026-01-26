package com.ryuqq.application.classtype.factory.query;

import com.ryuqq.application.classtype.dto.query.ClassTypeSearchParams;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTypeQueryFactory - ClassType Query → Criteria 변환 Factory
 *
 * <p>Query 파라미터를 Domain Criteria로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeQueryFactory {

    /**
     * ClassTypeSearchParams로부터 ClassTypeSliceCriteria 생성
     *
     * @param params 검색 파라미터
     * @return ClassTypeSliceCriteria
     */
    public ClassTypeSliceCriteria createSliceCriteria(ClassTypeSearchParams params) {
        CursorPageRequest<Long> cursorPageRequest =
                params.cursor() == null
                        ? CursorPageRequest.first(params.size())
                        : CursorPageRequest.afterId(params.cursor(), params.size());

        List<ClassTypeId> ids =
                params.ids() == null ? null : params.ids().stream().map(ClassTypeId::of).toList();

        List<ClassTypeCategoryId> categoryIds =
                params.categoryIds() == null
                        ? null
                        : params.categoryIds().stream().map(ClassTypeCategoryId::of).toList();

        List<ArchitectureId> architectureIds =
                params.architectureIds() == null
                        ? null
                        : params.architectureIds().stream().map(ArchitectureId::of).toList();

        return ClassTypeSliceCriteria.of(
                cursorPageRequest,
                ids,
                categoryIds,
                architectureIds,
                params.searchField(),
                params.searchWord());
    }
}
