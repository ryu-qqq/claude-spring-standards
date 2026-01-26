package com.ryuqq.application.classtemplate.factory.query;

import com.ryuqq.application.classtemplate.dto.query.ClassTemplateSearchParams;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateQueryFactory - 클래스 템플릿 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ClassTemplateQueryFactory {

    /**
     * ClassTemplateSearchParams로부터 ClassTemplateSliceCriteria 생성
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 검색 파라미터
     * @return ClassTemplateSliceCriteria
     */
    public ClassTemplateSliceCriteria createSliceCriteria(ClassTemplateSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;

        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<PackageStructureId> structureIds = null;
        if (searchParams.hasStructureIds()) {
            structureIds =
                    searchParams.structureIds().stream()
                            .map(PackageStructureId::of)
                            .collect(Collectors.toList());
        }

        List<ClassTypeId> classTypeIds = null;
        if (searchParams.hasClassTypeIds()) {
            classTypeIds =
                    searchParams.classTypeIds().stream()
                            .map(ClassTypeId::of)
                            .collect(Collectors.toList());
        }

        return ClassTemplateSliceCriteria.of(cursorPageRequest, structureIds, classTypeIds);
    }

    /**
     * Long ID를 ClassTemplateId로 변환
     *
     * @param classTemplateId 클래스 템플릿 ID
     * @return ClassTemplateId
     */
    public ClassTemplateId toClassTemplateId(Long classTemplateId) {
        return ClassTemplateId.of(classTemplateId);
    }

    /**
     * Long ID를 PackageStructureId로 변환
     *
     * @param structureId 패키지 구조 ID
     * @return PackageStructureId
     */
    public PackageStructureId toStructureId(Long structureId) {
        return PackageStructureId.of(structureId);
    }
}
