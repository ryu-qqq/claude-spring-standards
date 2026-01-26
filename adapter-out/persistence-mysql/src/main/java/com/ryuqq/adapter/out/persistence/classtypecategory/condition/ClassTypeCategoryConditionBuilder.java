package com.ryuqq.adapter.out.persistence.classtypecategory.condition;

import static com.ryuqq.adapter.out.persistence.classtypecategory.entity.QClassTypeCategoryJpaEntity.classTypeCategoryJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import com.ryuqq.domain.classtypecategory.vo.CategorySearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryConditionBuilder - ClassTypeCategory QueryDSL 조건 빌더
 *
 * <p>ClassTypeCategory 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeCategoryConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return classTypeCategoryJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건 (ID 오름차순)
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID > cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorGt(ClassTypeCategorySliceCriteria criteria) {
        return criteria.cursorPageRequest().hasCursor()
                ? classTypeCategoryJpaEntity.id.gt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * Architecture ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return architectureIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression architectureIdsIn(ClassTypeCategorySliceCriteria criteria) {
        if (!criteria.hasArchitectureIds()) {
            return null;
        }
        List<Long> architectureIds =
                criteria.architectureIds().stream().map(ArchitectureId::value).toList();
        return classTypeCategoryJpaEntity.architectureId.in(architectureIds);
    }

    /**
     * 아키텍처 ID 일치 조건 (Long 값 기반)
     *
     * @param architectureId 아키텍처 ID
     * @return architectureId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression architectureIdEq(Long architectureId) {
        return architectureId != null
                ? classTypeCategoryJpaEntity.architectureId.eq(architectureId)
                : null;
    }

    /**
     * ID 일치 조건
     *
     * @param id ClassTypeCategory ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? classTypeCategoryJpaEntity.id.eq(id) : null;
    }

    /**
     * 코드 일치 조건
     *
     * @param code ClassTypeCategory 코드
     * @return code 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression codeEq(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return classTypeCategoryJpaEntity.code.eq(code);
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? classTypeCategoryJpaEntity.id.ne(excludeId) : null;
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

        CategorySearchField field = CategorySearchField.valueOf(searchField);
        return switch (field) {
            case CODE -> classTypeCategoryJpaEntity.code.containsIgnoreCase(searchWord);
            case NAME -> classTypeCategoryJpaEntity.name.containsIgnoreCase(searchWord);
            case DESCRIPTION ->
                    classTypeCategoryJpaEntity.description.containsIgnoreCase(searchWord);
        };
    }
}
