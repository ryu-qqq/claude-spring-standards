package com.ryuqq.adapter.out.persistence.resourcetemplate.condition;

import static com.ryuqq.adapter.out.persistence.resourcetemplate.entity.QResourceTemplateJpaEntity.resourceTemplateJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateConditionBuilder - 리소스 템플릿 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ResourceTemplateConditionBuilder {

    /**
     * ID 일치 조건
     *
     * @param id 리소스 템플릿 ID
     * @return ID 일치 조건
     */
    public BooleanExpression idEq(Long id) {
        return resourceTemplateJpaEntity.id.eq(id);
    }

    /**
     * 모듈 ID 일치 조건
     *
     * @param moduleId 모듈 ID
     * @return moduleId 일치 조건
     */
    public BooleanExpression moduleIdEq(Long moduleId) {
        return resourceTemplateJpaEntity.moduleId.eq(moduleId);
    }

    /**
     * 모듈 ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return moduleIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression moduleIdsIn(ResourceTemplateSliceCriteria criteria) {
        if (!criteria.hasModuleFilter()) {
            return null;
        }
        List<Long> moduleIdValues = criteria.moduleIds().stream().map(id -> id.value()).toList();
        return resourceTemplateJpaEntity.moduleId.in(moduleIdValues);
    }

    /**
     * 카테고리 일치 조건
     *
     * @param category 카테고리 문자열
     * @return category 일치 조건
     */
    public BooleanExpression categoryEq(String category) {
        return resourceTemplateJpaEntity.category.eq(category);
    }

    /**
     * 카테고리 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return categories IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression categoriesIn(ResourceTemplateSliceCriteria criteria) {
        if (!criteria.hasCategoryFilter()) {
            return null;
        }
        List<String> categoryNames =
                criteria.categories().stream().map(TemplateCategory::name).toList();
        return resourceTemplateJpaEntity.category.in(categoryNames);
    }

    /**
     * 파일 타입 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return fileTypes IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression fileTypesIn(ResourceTemplateSliceCriteria criteria) {
        if (!criteria.hasFileTypeFilter()) {
            return null;
        }
        List<String> fileTypeNames = criteria.fileTypes().stream().map(FileType::name).toList();
        return resourceTemplateJpaEntity.fileType.in(fileTypeNames);
    }

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return resourceTemplateJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? resourceTemplateJpaEntity.id.lt(cursor) : null;
    }

    /**
     * 커서 기반 페이징 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(ResourceTemplateSliceCriteria criteria) {
        return criteria.hasCursor()
                ? resourceTemplateJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }
}
