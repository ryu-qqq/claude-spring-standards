package com.ryuqq.adapter.out.persistence.layer.condition;

import static com.ryuqq.adapter.out.persistence.layer.entity.QLayerJpaEntity.layerJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import com.ryuqq.domain.layer.vo.LayerSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LayerConditionBuilder - Layer QueryDSL 조건 빌더
 *
 * <p>Layer 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LayerConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return layerJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건 (ID 오름차순)
     *
     * <p>Layer는 orderIndex 기반 오름차순 정렬이므로, 커서보다 큰 ID를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID > cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorGt(LayerSliceCriteria criteria) {
        return criteria.cursorPageRequest().hasCursor()
                ? layerJpaEntity.id.gt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * Architecture ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 architectureIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return architectureIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression architectureIdsIn(LayerSliceCriteria criteria) {
        if (!criteria.hasArchitectureIds()) {
            return null;
        }
        List<Long> architectureIds =
                criteria.architectureIds().stream().map(ArchitectureId::value).toList();
        return layerJpaEntity.architectureId.in(architectureIds);
    }

    /**
     * 아키텍처 ID 일치 조건 (Long 값 기반)
     *
     * @param architectureId 아키텍처 ID
     * @return architectureId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression architectureIdEq(Long architectureId) {
        return architectureId != null ? layerJpaEntity.architectureId.eq(architectureId) : null;
    }

    /**
     * ID 일치 조건
     *
     * @param id Layer ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? layerJpaEntity.id.eq(id) : null;
    }

    /**
     * 코드 일치 조건
     *
     * @param code Layer 코드
     * @return code 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression codeEq(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return layerJpaEntity.code.eq(code);
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? layerJpaEntity.id.ne(excludeId) : null;
    }

    /**
     * 검색 조건 (필드별)
     *
     * <p>searchField에 따라 해당 필드에서 searchWord를 부분 일치 검색합니다.
     *
     * <p>대소문자 구분 없이 검색합니다.
     *
     * <p>searchField 또는 searchWord가 null이거나 비어있으면 검색하지 않습니다.
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

        LayerSearchField field = LayerSearchField.valueOf(searchField);
        return switch (field) {
            case CODE -> layerJpaEntity.code.containsIgnoreCase(searchWord);
            case NAME -> layerJpaEntity.name.containsIgnoreCase(searchWord);
            case DESCRIPTION -> layerJpaEntity.description.containsIgnoreCase(searchWord);
        };
    }
}
