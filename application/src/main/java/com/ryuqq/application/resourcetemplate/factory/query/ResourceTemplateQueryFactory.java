package com.ryuqq.application.resourcetemplate.factory.query;

import com.ryuqq.application.resourcetemplate.dto.query.ResourceTemplateSearchParams;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateQueryFactory - 리소스 템플릿 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateQueryFactory {

    /**
     * ResourceTemplateSearchParams로부터 ResourceTemplateSliceCriteria 생성
     *
     * <p>FAC-001: Criteria 생성도 Factory에서 담당합니다.
     *
     * @param searchParams 검색 파라미터
     * @return ResourceTemplateSliceCriteria
     */
    public ResourceTemplateSliceCriteria createSliceCriteria(
            ResourceTemplateSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;

        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<ModuleId> moduleIds = null;
        if (searchParams.hasModuleIds()) {
            moduleIds =
                    searchParams.moduleIds().stream()
                            .map(ModuleId::of)
                            .collect(Collectors.toList());
        }

        List<TemplateCategory> categories = null;
        if (searchParams.hasCategories()) {
            categories =
                    searchParams.categories().stream()
                            .map(TemplateCategory::valueOf)
                            .collect(Collectors.toList());
        }

        List<FileType> fileTypes = null;
        if (searchParams.hasFileTypes()) {
            fileTypes =
                    searchParams.fileTypes().stream()
                            .map(FileType::valueOf)
                            .collect(Collectors.toList());
        }

        return ResourceTemplateSliceCriteria.of(
                moduleIds, categories, fileTypes, cursorPageRequest);
    }

    /**
     * Long ID를 ResourceTemplateId로 변환
     *
     * @param resourceTemplateId 리소스 템플릿 ID
     * @return ResourceTemplateId
     */
    public ResourceTemplateId toResourceTemplateId(Long resourceTemplateId) {
        return ResourceTemplateId.of(resourceTemplateId);
    }

    /**
     * Long ID를 ModuleId로 변환
     *
     * @param moduleId 모듈 ID
     * @return ModuleId
     */
    public ModuleId toModuleId(Long moduleId) {
        return ModuleId.of(moduleId);
    }
}
