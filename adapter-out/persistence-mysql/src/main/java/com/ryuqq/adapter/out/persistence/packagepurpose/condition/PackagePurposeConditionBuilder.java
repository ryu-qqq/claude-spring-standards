package com.ryuqq.adapter.out.persistence.packagepurpose.condition;

import static com.ryuqq.adapter.out.persistence.packagepurpose.entity.QPackagePurposeJpaEntity.packagePurposeJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import com.ryuqq.domain.packagepurpose.vo.PackagePurposeSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeConditionBuilder - 패키지 목적 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposeConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return packagePurposeJpaEntity.deletedAt.isNull();
    }

    /**
     * 패키지 구조 ID 일치 조건
     *
     * @param structureId 패키지 구조 ID
     * @return structureId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression structureIdEq(Long structureId) {
        return structureId != null ? packagePurposeJpaEntity.structureId.eq(structureId) : null;
    }

    public BooleanExpression structureIdsIn(PackagePurposeSliceCriteria criteria) {
        if (!criteria.hasStructureIds()) {
            return null;
        }
        List<Long> structureIdValues =
                criteria.structureIds().stream().map(id -> id.value()).toList();
        return packagePurposeJpaEntity.structureId.in(structureIdValues);
    }

    public BooleanExpression searchByField(PackagePurposeSliceCriteria criteria) {
        if (!criteria.hasSearch()) {
            return null;
        }
        PackagePurposeSearchField field = criteria.searchField();
        String word = criteria.searchWord();
        return switch (field) {
            case CODE -> packagePurposeJpaEntity.code.containsIgnoreCase(word);
            case NAME -> packagePurposeJpaEntity.name.containsIgnoreCase(word);
            case DESCRIPTION -> packagePurposeJpaEntity.description.containsIgnoreCase(word);
        };
    }

    public BooleanExpression cursorLt(PackagePurposeSliceCriteria criteria) {
        return criteria.hasCursor()
                ? packagePurposeJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }
}
