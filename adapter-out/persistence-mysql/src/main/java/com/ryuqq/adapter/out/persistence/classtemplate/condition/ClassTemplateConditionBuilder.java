package com.ryuqq.adapter.out.persistence.classtemplate.condition;

import static com.ryuqq.adapter.out.persistence.classtemplate.entity.QClassTemplateJpaEntity.classTemplateJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateConditionBuilder - 클래스 템플릿 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTemplateConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return classTemplateJpaEntity.deletedAt.isNull();
    }

    /**
     * ID 일치 조건
     *
     * @param id 클래스 템플릿 ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? classTemplateJpaEntity.id.eq(id) : null;
    }

    /**
     * 패키지 구조 ID 일치 조건
     *
     * @param structureId 패키지 구조 ID
     * @return structureId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression structureIdEq(Long structureId) {
        return structureId != null ? classTemplateJpaEntity.structureId.eq(structureId) : null;
    }

    /**
     * 패키지 구조 ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 structureIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return structureIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression structureIdsIn(ClassTemplateSliceCriteria criteria) {
        if (!criteria.hasStructureIds()) {
            return null;
        }
        List<Long> structureIdValues =
                criteria.structureIds().stream().map(id -> id.value()).toList();
        return classTemplateJpaEntity.structureId.in(structureIdValues);
    }

    /**
     * 클래스 타입 ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 classTypeIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return classTypeIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression classTypeIdsIn(ClassTemplateSliceCriteria criteria) {
        if (!criteria.hasClassTypeIds()) {
            return null;
        }
        List<Long> classTypeIdValues =
                criteria.classTypeIds().stream().map(id -> id.value()).toList();
        return classTemplateJpaEntity.classTypeId.in(classTypeIdValues);
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(ClassTemplateSliceCriteria criteria) {
        return criteria.hasCursor()
                ? classTemplateJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * 커서 기반 페이징 조건 (레거시 - 호환성 유지)
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? classTemplateJpaEntity.id.lt(cursor) : null;
    }

    /**
     * 템플릿 코드 일치 조건
     *
     * @param templateCode 템플릿 코드
     * @return templateCode 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression templateCodeEq(String templateCode) {
        return templateCode != null ? classTemplateJpaEntity.templateCode.eq(templateCode) : null;
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? classTemplateJpaEntity.id.ne(excludeId) : null;
    }

    /**
     * 키워드 검색 조건 (templateCode, description 필드)
     *
     * @param keyword 검색 키워드
     * @return 키워드 포함 조건 (nullable 또는 빈 문자열이면 null 반환)
     */
    public BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return classTemplateJpaEntity
                .templateCode
                .containsIgnoreCase(keyword)
                .or(classTemplateJpaEntity.description.containsIgnoreCase(keyword));
    }
}
