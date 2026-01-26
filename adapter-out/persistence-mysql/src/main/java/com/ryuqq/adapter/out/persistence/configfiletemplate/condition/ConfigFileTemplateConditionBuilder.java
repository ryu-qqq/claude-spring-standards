package com.ryuqq.adapter.out.persistence.configfiletemplate.condition;

import static com.ryuqq.adapter.out.persistence.configfiletemplate.entity.QConfigFileTemplateJpaEntity.configFileTemplateJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateConditionBuilder - ConfigFileTemplate QueryDSL 조건 빌더
 *
 * <p>ConfigFileTemplate 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return configFileTemplateJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(ConfigFileTemplateSliceCriteria criteria) {
        return criteria.cursorPageRequest().hasCursor()
                ? configFileTemplateJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * TechStack ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return techStackIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression techStackIdsIn(ConfigFileTemplateSliceCriteria criteria) {
        if (!criteria.hasTechStackIds()) {
            return null;
        }
        List<Long> techStackIds = criteria.techStackIds().stream().map(TechStackId::value).toList();
        return configFileTemplateJpaEntity.techStackId.in(techStackIds);
    }

    /**
     * Tool Type 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return toolTypes IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression toolTypesIn(ConfigFileTemplateSliceCriteria criteria) {
        if (!criteria.hasToolTypes()) {
            return null;
        }
        List<String> toolTypes = criteria.toolTypes().stream().map(ToolType::name).toList();
        return configFileTemplateJpaEntity.toolType.in(toolTypes);
    }

    /**
     * Category 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return categories IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression categoriesIn(ConfigFileTemplateSliceCriteria criteria) {
        if (!criteria.hasCategories()) {
            return null;
        }
        List<String> categories =
                criteria.categories().stream().map(TemplateCategory::name).toList();
        return configFileTemplateJpaEntity.category.in(categories);
    }

    /**
     * TechStack ID 일치 조건
     *
     * @param techStackId TechStack ID
     * @return techStackId 일치 조건
     */
    public BooleanExpression techStackIdEq(Long techStackId) {
        return configFileTemplateJpaEntity.techStackId.eq(techStackId);
    }

    /**
     * ID 일치 조건
     *
     * @param id ConfigFileTemplate ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? configFileTemplateJpaEntity.id.eq(id) : null;
    }
}
