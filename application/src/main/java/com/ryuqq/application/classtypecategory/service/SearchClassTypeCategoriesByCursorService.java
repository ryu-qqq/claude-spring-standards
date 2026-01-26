package com.ryuqq.application.classtypecategory.service;

import com.ryuqq.application.classtypecategory.assembler.ClassTypeCategoryAssembler;
import com.ryuqq.application.classtypecategory.dto.query.ClassTypeCategorySearchParams;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategorySliceResult;
import com.ryuqq.application.classtypecategory.factory.query.ClassTypeCategoryQueryFactory;
import com.ryuqq.application.classtypecategory.manager.ClassTypeCategoryReadManager;
import com.ryuqq.application.classtypecategory.port.in.SearchClassTypeCategoriesByCursorUseCase;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchClassTypeCategoriesByCursorService - ClassTypeCategory 커서 기반 검색 서비스
 *
 * <p>SearchClassTypeCategoriesByCursorUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class SearchClassTypeCategoriesByCursorService
        implements SearchClassTypeCategoriesByCursorUseCase {

    private final ClassTypeCategoryQueryFactory classTypeCategoryQueryFactory;
    private final ClassTypeCategoryReadManager classTypeCategoryReadManager;
    private final ClassTypeCategoryAssembler classTypeCategoryAssembler;

    public SearchClassTypeCategoriesByCursorService(
            ClassTypeCategoryQueryFactory classTypeCategoryQueryFactory,
            ClassTypeCategoryReadManager classTypeCategoryReadManager,
            ClassTypeCategoryAssembler classTypeCategoryAssembler) {
        this.classTypeCategoryQueryFactory = classTypeCategoryQueryFactory;
        this.classTypeCategoryReadManager = classTypeCategoryReadManager;
        this.classTypeCategoryAssembler = classTypeCategoryAssembler;
    }

    @Override
    public ClassTypeCategorySliceResult execute(ClassTypeCategorySearchParams params) {
        ClassTypeCategorySliceCriteria criteria =
                classTypeCategoryQueryFactory.createSliceCriteria(params);

        List<ClassTypeCategory> categories =
                classTypeCategoryReadManager.findBySliceCriteria(criteria);

        return classTypeCategoryAssembler.toSliceResult(categories, params.size());
    }
}
