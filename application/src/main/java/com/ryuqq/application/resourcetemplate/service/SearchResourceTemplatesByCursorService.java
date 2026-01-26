package com.ryuqq.application.resourcetemplate.service;

import com.ryuqq.application.resourcetemplate.assembler.ResourceTemplateAssembler;
import com.ryuqq.application.resourcetemplate.dto.query.ResourceTemplateSearchParams;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import com.ryuqq.application.resourcetemplate.factory.query.ResourceTemplateQueryFactory;
import com.ryuqq.application.resourcetemplate.manager.ResourceTemplateReadManager;
import com.ryuqq.application.resourcetemplate.port.in.SearchResourceTemplatesByCursorUseCase;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchResourceTemplatesByCursorService - ResourceTemplate 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>SearchResourceTemplatesByCursorUseCase를 구현합니다.
 *
 * <p>ResourceTemplate 목록을 커서 기반으로 복합 조건(모듈 ID, 카테고리, 파일 타입)으로 조회합니다.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class SearchResourceTemplatesByCursorService
        implements SearchResourceTemplatesByCursorUseCase {

    private final ResourceTemplateQueryFactory resourceTemplateQueryFactory;
    private final ResourceTemplateReadManager resourceTemplateReadManager;
    private final ResourceTemplateAssembler resourceTemplateAssembler;

    public SearchResourceTemplatesByCursorService(
            ResourceTemplateQueryFactory resourceTemplateQueryFactory,
            ResourceTemplateReadManager resourceTemplateReadManager,
            ResourceTemplateAssembler resourceTemplateAssembler) {
        this.resourceTemplateQueryFactory = resourceTemplateQueryFactory;
        this.resourceTemplateReadManager = resourceTemplateReadManager;
        this.resourceTemplateAssembler = resourceTemplateAssembler;
    }

    @Override
    public ResourceTemplateSliceResult execute(ResourceTemplateSearchParams searchParams) {
        ResourceTemplateSliceCriteria criteria =
                resourceTemplateQueryFactory.createSliceCriteria(searchParams);
        List<ResourceTemplate> resourceTemplates =
                resourceTemplateReadManager.findBySliceCriteria(criteria);
        return resourceTemplateAssembler.toSliceResult(resourceTemplates, searchParams.size());
    }
}
