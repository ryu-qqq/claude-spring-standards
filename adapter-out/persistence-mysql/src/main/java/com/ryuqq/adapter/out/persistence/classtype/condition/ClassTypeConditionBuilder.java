package com.ryuqq.adapter.out.persistence.classtype.condition;

import static com.ryuqq.adapter.out.persistence.classtype.entity.QClassTypeJpaEntity.classTypeJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import com.ryuqq.domain.classtype.vo.ClassTypeSearchField;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTypeConditionBuilder - ClassType QueryDSL 조건 빌더
 *
 * <p>ClassType 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return classTypeJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건 (ID 오름차순)
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID > cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorGt(ClassTypeSliceCriteria criteria) {
        return criteria.cursorPageRequest().hasCursor()
                ? classTypeJpaEntity.id.gt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * Category ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return categoryIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression categoryIdsIn(ClassTypeSliceCriteria criteria) {
        if (!criteria.hasCategoryIds()) {
            return null;
        }
        List<Long> categoryIds =
                criteria.categoryIds().stream().map(ClassTypeCategoryId::value).toList();
        return classTypeJpaEntity.categoryId.in(categoryIds);
    }

    /**
     * 카테고리 ID 일치 조건 (Long 값 기반)
     *
     * @param categoryId 카테고리 ID
     * @return categoryId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? classTypeJpaEntity.categoryId.eq(categoryId) : null;
    }

    /**
     * ID 일치 조건
     *
     * @param id ClassType ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? classTypeJpaEntity.id.eq(id) : null;
    }

    /**
     * 코드 일치 조건
     *
     * @param code ClassType 코드
     * @return code 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression codeEq(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return classTypeJpaEntity.code.eq(code);
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? classTypeJpaEntity.id.ne(excludeId) : null;
    }

    /**
     * 검색 조건 (필드별)
     *
     * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION)
     * @param searchWord 검색어 (부분 일치)
     * @return 검색 조건 (nullable이면 null 반환)
     */
    public BooleanExpression searchByField(String searchField, String searchWord) {
        if (searchField == null
                || searchField.isBlank()
                || searchWord == null
                || searchWord.isBlank()) {
            return null;
        }

        ClassTypeSearchField field = ClassTypeSearchField.valueOf(searchField);
        return switch (field) {
            case CODE -> classTypeJpaEntity.code.containsIgnoreCase(searchWord);
            case NAME -> classTypeJpaEntity.name.containsIgnoreCase(searchWord);
            case DESCRIPTION -> classTypeJpaEntity.description.containsIgnoreCase(searchWord);
        };
    }
}
