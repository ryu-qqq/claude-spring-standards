package com.ryuqq.adapter.out.persistence.module.condition;

import static com.ryuqq.adapter.out.persistence.module.entity.QModuleJpaEntity.moduleJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ModuleConditionBuilder - Module QueryDSL 조건 빌더
 *
 * <p>Module 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ModuleConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return moduleJpaEntity.deletedAt.isNull();
    }

    /**
     * 레이어 ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 layerIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return layerIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression layerIdsIn(ModuleSliceCriteria criteria) {
        if (!criteria.hasLayerIds()) {
            return null;
        }
        List<Long> layerIds = criteria.layerIds().stream().map(LayerId::value).toList();
        return moduleJpaEntity.layerId.in(layerIds);
    }

    /**
     * 레이어 ID 일치 조건 (Long 값 기반)
     *
     * @param layerId 레이어 ID
     * @return layerId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression layerIdEq(Long layerId) {
        return layerId != null ? moduleJpaEntity.layerId.eq(layerId) : null;
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(ModuleSliceCriteria criteria) {
        return criteria.hasCursor()
                ? moduleJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * ID 일치 조건
     *
     * @param id Module ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? moduleJpaEntity.id.eq(id) : null;
    }

    /**
     * 모듈 이름 일치 조건
     *
     * @param name 모듈 이름
     * @return name 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression nameEq(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return moduleJpaEntity.name.eq(name);
    }

    /**
     * 부모 모듈 ID 일치 조건
     *
     * @param parentModuleId 부모 모듈 ID
     * @return parentModuleId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression parentModuleIdEq(Long parentModuleId) {
        return parentModuleId != null ? moduleJpaEntity.parentModuleId.eq(parentModuleId) : null;
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? moduleJpaEntity.id.ne(excludeId) : null;
    }

    /**
     * 키워드 검색 조건 (이름, 설명 필드)
     *
     * <p>대소문자 구분 없이 이름 또는 설명에 키워드가 포함된 레코드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @return 이름 또는 설명에 키워드 포함 조건 (nullable이면 null 반환)
     */
    public BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return moduleJpaEntity
                .name
                .containsIgnoreCase(keyword)
                .or(moduleJpaEntity.description.containsIgnoreCase(keyword));
    }
}
